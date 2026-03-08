package com.example.taxiclient;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomerMap extends AppCompatActivity {
    private MapView mapView;

    private MyLocationNewOverlay mLocationOverlay;
    private GpsMyLocationProvider locationProvider;
    private GraphHopper hopper;
    private boolean isHopperReady = false;
    private Polyline currentRouteLine;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        AndroidGraphicFactory.createInstance(getApplication());
        mapView = findViewById(R.id.mapview);

        Configuration.getInstance().load(this,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));

         dbHelper = new DatabaseHelper(this);
        setupOfflineMap();
        initNavigation();

        String addrB = getIntent().getStringExtra("ARG_ADDR_B");
        if (addrB != null) {
            startRouteWhenReady(addrB);
        }


    }

    private void processRouteRequest(String addressB) {
        GeoPoint driverPos = mLocationOverlay.getMyLocation(); // точка А
        LatLong end = dbHelper.getCoordinates(addressB);       // точка B

        if (driverPos != null && end != null) {
            calcPath(driverPos.getLatitude(), driverPos.getLongitude(),
                    end.getLatitude(), end.getLongitude());

            // Маркер для текущего положения (зелёный)
            addMarker(driverPos, "Вы здесь", R.drawable.human);

            // Маркер для назначения (красный)
            addMarker(new GeoPoint(end.getLatitude(), end.getLongitude()), "Назначение", R.drawable.home);
        }

    }


    private void setupOfflineMap() {
        mapView.setMultiTouchControls(true);
        locationProvider = new GpsMyLocationProvider(this);
        mLocationOverlay = new MyLocationNewOverlay(locationProvider, mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(mLocationOverlay);
        mapView.getController().setZoom(15.0);
    }

    private void initNavigation() {
        // Основная папка в Android/data/com.example.taxiclient/files/graph-data
        File rootFolder = new File(getExternalFilesDir(null), "graph-data");

        new Thread(() -> {
            try {
                // Проверяем, нет ли внутри еще одной папки "graph-data" (как было в логах)
                File nestedFolder = new File(rootFolder, "graph-data");

                // Если внутри есть файл "nodes" (признак данных GraphHopper), используем вложенную папку
                // Иначе используем rootFolder
                File finalDataPath = new File(nestedFolder, "nodes").exists() ? nestedFolder : rootFolder;

                Log.d("GH", "Попытка загрузки из: " + finalDataPath.getAbsolutePath());

                if (!new File(finalDataPath, "nodes").exists()) {
                    Log.e("GH", "Файлы графа не найдены! Проверьте распаковку.");
                    return;
                }

                hopper = new GraphHopper().forMobile();
                // Важно: setGraphHopperLocation и load должны указывать на одну и ту же папку
                hopper.load(finalDataPath.getAbsolutePath());

                isHopperReady = true;
                Log.d("GH", "GraphHopper успешно загружен!");

            } catch (Exception e) {
                Log.e("GH", "Ошибка инициализации: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void startRouteWhenReady(String addressB) {
        new Thread(() -> {
            int attempts = 0;
            while (!isHopperReady && attempts < 50) {
                try { Thread.sleep(100); attempts++; } catch (InterruptedException ignored) {}
            }
            if (isHopperReady) {
                runOnUiThread(() -> processRouteRequest(addressB));
            }
        }).start();
    }




    private void calcPath(double fromLat, double fromLon, double toLat, double toLon) {
        new AsyncTask<Void, Void, PathWrapper>() {
            @Override
            protected PathWrapper doInBackground(Void... voids) {
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon)
                        .setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI)
                        .setVehicle("car");
                GHResponse res = hopper.route(req);
                return res.hasErrors() ? null : res.getBest();
            }

            @Override
            protected void onPostExecute(PathWrapper path) {
                if (path == null) {
                    Toast.makeText(CustomerMap.this, "Маршрут не найден", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Очистка старой линии
                if (currentRouteLine != null) {
                    mapView.getOverlays().remove(currentRouteLine);
                }

                currentRouteLine = new Polyline();
                currentRouteLine.setColor(Color.BLUE);
                currentRouteLine.getOutlinePaint().setStrokeWidth(10f);

                // 2. Преобразование точек GraphHopper в точки osmdroid
                PointList points = path.getPoints();
                List<GeoPoint> geoPoints = new ArrayList<>();
                for (int i = 0; i < points.size(); i++) {
                    geoPoints.add(new GeoPoint(points.getLat(i), points.getLon(i)));
                }
                currentRouteLine.setPoints(geoPoints);
                mapView.getOverlays().add(currentRouteLine);

                // 3. Масштабирование карты под весь маршрут
                if (!geoPoints.isEmpty()) {
                    // Создаем ограничивающую рамку (BoundingBox) на основе всех точек линии
                    org.osmdroid.util.BoundingBox boundingBox = org.osmdroid.util.BoundingBox.fromGeoPoints(geoPoints);

                    // Увеличиваем отступы (padding), чтобы маркеры не прижимались к краям экрана (в пикселях)
                    int padding = 150;

                    // Вызываем масштабирование
                    mapView.zoomToBoundingBox(boundingBox, true, padding);
                }

                mapView.invalidate();
            }
        }.execute();
    }



    private void addMarker(GeoPoint point, String title, int iconResId) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Устанавливаем кастомную иконку
        marker.setIcon(getResources().getDrawable(iconResId, null));

        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }


}

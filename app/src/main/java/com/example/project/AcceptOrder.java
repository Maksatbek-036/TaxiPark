package com.example.project;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AcceptOrder extends AppCompatActivity {

    private MapView mapView;
    private MyLocationNewOverlay mLocationOverlay;
    private Polyline currentRouteLine;

    private GraphHopper hopper;
    private boolean isHopperReady = false;
    private String mapFileName = "kyrgyzstan.map";

    private TextView orderNumber, orderTariff, orderDistance, orderAddress;
    private Button acceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Инициализация Mapsforge Graphics
        AndroidGraphicFactory.createInstance(getApplication());

        // Настройка OSMDroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_accept_order);

        initViews();
        setupMap();

        // 2. Запуск подготовки навигатора
        initNavigation();
    }

    private void initViews() {
        mapView = findViewById(R.id.map);
        orderNumber = findViewById(R.id.orderNumber);
        orderTariff = findViewById(R.id.orderTariff);
        orderDistance = findViewById(R.id.orderDistance);
        orderAddress = findViewById(R.id.orderAddress);
        acceptButton = findViewById(R.id.acceptButton);

        // Пример данных
        orderNumber.setText("Заказ №12345");
        orderAddress.setText("ул. Ленина, 10");

        acceptButton.setOnClickListener(v -> {
            // Пример: при принятии заказа строим маршрут до точки клиента
            // Координаты клиента (в реальности придут из БД/API)
            GeoPoint destination = new GeoPoint(42.8746, 74.5698);
            drawRouteToClient(destination);
            Toast.makeText(this, "Маршрут построен", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMap() {
        mapView.setMultiTouchControls(true);

        // Попытка загрузить офлайн карту
        File mapFile = new File(getExternalFilesDir(null), mapFileName);
        if (mapFile.exists()) {
            MapsForgeTileSource tileSource = MapsForgeTileSource.createFromFiles(new File[]{mapFile});
            MapsForgeTileProvider tileProvider = new MapsForgeTileProvider(
                    new SimpleRegisterReceiver(this), tileSource, null);
            mapView.setTileProvider(tileProvider);
        }

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(mLocationOverlay);

        mapView.getController().setZoom(15.0);
    }

    private void initNavigation() {
        File rootFolder = new File(getExternalFilesDir(null), "graph-data");

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    if (!rootFolder.exists() || rootFolder.listFiles() == null || rootFolder.listFiles().length < 2) {
                        unpackZipFromAssets("graph-data.zip", rootFolder);
                    }

                    hopper = new GraphHopper().forMobile();
                    hopper.setGraphHopperLocation(rootFolder.getAbsolutePath());
                    hopper.load(rootFolder.getAbsolutePath());
                    return true;
                } catch (Exception e) {
                    Log.e("GH", "Ошибка GraphHopper: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                isHopperReady = success;
                if (success) Log.d("GH", "Навигация готова");
            }
        }.execute();
    }

    public void drawRouteToClient(GeoPoint dest) {
        GeoPoint start = mLocationOverlay.getMyLocation();
        if (start == null || !isHopperReady) {
            Toast.makeText(this, "Ждем GPS или загрузку данных...", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, PointList>() {
            @Override
            protected PointList doInBackground(Void... voids) {
                GHRequest req = new GHRequest(start.getLatitude(), start.getLongitude(),
                        dest.getLatitude(), dest.getLongitude())
                        .setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI)
                        .setVehicle("car");
                GHResponse res = hopper.route(req);
                return res.hasErrors() ? null : res.getBest().getPoints();
            }

            @Override
            protected void onPostExecute(PointList points) {
                if (points == null) return;

                if (currentRouteLine != null) mapView.getOverlays().remove(currentRouteLine);

                currentRouteLine = new Polyline();
                currentRouteLine.setColor(Color.parseColor("#1C7CD6"));
                currentRouteLine.getOutlinePaint().setStrokeWidth(12f);

                List<GeoPoint> geoPoints = new ArrayList<>();
                for (int i = 0; i < points.size(); i++) {
                    geoPoints.add(new GeoPoint(points.getLat(i), points.getLon(i)));
                }

                currentRouteLine.setPoints(geoPoints);
                mapView.getOverlays().add(currentRouteLine);
                mapView.invalidate();
            }
        }.execute();
    }

    private void unpackZipFromAssets(String zipName, File targetDir) throws IOException {
        if (!targetDir.exists()) targetDir.mkdirs();
        try (InputStream is = getAssets().open(zipName);
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(targetDir, entry.getName());
                if (entry.isDirectory()) file.mkdirs();
                else {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    @Override
    protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override
    protected void onPause() { super.onPause(); mapView.onPause(); }
}
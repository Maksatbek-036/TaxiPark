package com.example.taxiclient;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MapsForge extends Fragment {

    private AlertDialog progressDialog;
    private MapView mapView;
    private MyLocationNewOverlay mLocationOverlay;
    private GpsMyLocationProvider locationProvider;
    private SearchView searchView;
    private DatabaseHelper dbHelper;
    private Polyline currentRouteLine;
    private String mapFileName = "kyrgyzstan.map";
    private FloatingActionButton fabMyLocation;
    private MapsForgeTileProvider tileProvider;

    private boolean isHopperReady = false;
    private GraphHopper hopper;


    public MapsForge() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация графики Mapsforge
        AndroidGraphicFactory.createInstance(requireActivity().getApplication());

        // Настройка OSMDroid
        Configuration.getInstance().load(requireContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()));

        dbHelper = new DatabaseHelper(requireContext());
        checkPermissions();
        initNavigation();



    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String addrA = getArguments().getString("ARG_ADDR_A");
            String addrB = getArguments().getString("ARG_ADDR_B");
            initNavigation();
            startRouteWhenReady(addrA, addrB);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_forge, container, false);

        mapView = view.findViewById(R.id.mapview);
        fabMyLocation = view.findViewById(R.id.fab_my_location);

        searchView = view.findViewById(R.id.search_view);

        mapView.setMultiTouchControls(true);
        setupOfflineMap();

        fabMyLocation.setOnClickListener(v -> {
            if (mLocationOverlay != null) {
                GeoPoint myLocation = mLocationOverlay.getMyLocation();
                if (myLocation != null) {
                    mapView.getController().animateTo(myLocation);
                    mapView.getController().setZoom(17.0);
                } else {
                    Toast.makeText(getContext(), "Поиск спутников...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    private void showLoading(String message) {
        if (progressDialog == null) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            builder.setView(R.layout.layout_loading_dialog);
            builder.setCancelable(false);
            progressDialog = builder.create();
        }
        progressDialog.show();
    }

    // Метод для скрытия диалога
    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void setupOfflineMap() {
        File mapFile = getMapFile();
        if (mapFile.exists()) {
            try {
                MapsForgeTileSource tileSource = MapsForgeTileSource.createFromFiles(new File[]{mapFile});
                tileProvider = new MapsForgeTileProvider(
                        new SimpleRegisterReceiver(requireContext()),
                        tileSource,
                        null
                );

                mapView.setTileProvider(tileProvider);
                locationProvider = new GpsMyLocationProvider(requireContext());
                mLocationOverlay = new MyLocationNewOverlay(locationProvider, mapView);
                mLocationOverlay.enableMyLocation();
                mLocationOverlay.enableFollowLocation();
                mLocationOverlay.setDrawAccuracyEnabled(true);

                mapView.getOverlays().add(mLocationOverlay);
                mapView.getController().setZoom(15.0);
            } catch (Exception e) {
                Log.e("MAP", "Ошибка загрузки карты: " + e.getMessage());
            }
        }
    }

    private void initNavigation() {
        File rootFolder = new File(requireContext().getExternalFilesDir(null), "graph-data");
        showLoading("Подготовка навигатора...");

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    // 1. Проверка и распаковка (с защитой от вложенных папок)
                    if (!rootFolder.exists() || rootFolder.listFiles() == null || rootFolder.listFiles().length < 2) {
                        Log.d("GH", "Распаковка архива...");
                        unpackZipFromAssets("graph-data.zip", rootFolder);
                    }


                    File finalFolder = rootFolder;
                    if (!new File(finalFolder, "properties").exists()) {
                        File nested = new File(rootFolder, "graph-data");
                        if (new File(nested, "properties").exists()) {
                            finalFolder = nested;
                        }
                    }


                    hopper = new GraphHopper().forMobile();

                    // В 0.13.0 важно сначала указать путь, а потом вызвать load
                    hopper.setDataReaderFile(new File(finalFolder, "osm.pbf").getAbsolutePath()); // необязательно, но полезно
                    hopper.setGraphHopperLocation(finalFolder.getAbsolutePath());


                    hopper.load(finalFolder.getAbsolutePath());

                    Log.d("GH", "GraphHopper 0.13.0 успешно загружен!");
                    return true;
                } catch (Exception e) {
                    Log.e("GH", "Ошибка инициализации: " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                isHopperReady = success;
                hideLoading();
                if (!success) {
                    Toast.makeText(getContext(), "Ошибка данных графа", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void startRouteWhenReady(String a, String b) {
        new Thread(() -> {

            int attempts = 0;
            while (hopper == null && attempts < 50) {
                try { Thread.sleep(100); attempts++; } catch (InterruptedException e) {}
            }

            if (hopper != null) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> processRouteRequest(a, b));
                }
            }
        }).start();
    }


    public void processRouteRequest(String addressA, String addressB) {
        LatLong start = dbHelper.getCoordinates(addressA);
        LatLong end = dbHelper.getCoordinates(addressB);

        if (start != null && end != null) {
            calcPath(start.getLatitude(), start.getLongitude(),
                    end.getLatitude(), end.getLongitude());

            mapView.getController().animateTo(new GeoPoint(start.getLatitude(), start.getLongitude()));
            mapView.getController().setZoom(16.5);
        } else {
            Toast.makeText(getContext(), "Адрес не найден в базе", Toast.LENGTH_SHORT).show();
        }
    }

    public void calcPath(double fromLat, double fromLon, double toLat, double toLon) {
        if (!isHopperReady || hopper == null) {
            new android.os.Handler().postDelayed(() ->
                    calcPath(fromLat, fromLon, toLat, toLon), 500);
            return;
        }

        new AsyncTask<Void, Void, PathWrapper>() {
            @Override
            protected PathWrapper doInBackground(Void... voids) {
                try {
                    GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon)
                            .setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI)
                            .setVehicle("car");

                    GHResponse res = hopper.route(req);
                    if (res.hasErrors()) {
                        Log.e("GH", "Errors: " + res.getErrors());
                        return null;
                    }
                    return res.getBest();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(PathWrapper path) {
                if (path == null) {
                    Toast.makeText(getContext(), "Маршрут не найден", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Удаляем старую линию, если она есть
                if (currentRouteLine != null) {
                    mapView.getOverlays().remove(currentRouteLine);
                }

                // 2. Создаем новую линию
                currentRouteLine = new Polyline();
                currentRouteLine.setColor(Color.BLUE); // Цвет маршрута
                currentRouteLine.getOutlinePaint().setStrokeWidth(10f); // Толщина

                // 3. Конвертируем точки GraphHopper в GeoPoint для OSMDroid
                PointList points = path.getPoints();
                java.util.List<GeoPoint> geoPoints = new java.util.ArrayList<>();
                for (int i = 0; i < points.size(); i++) {
                    geoPoints.add(new GeoPoint(points.getLat(i), points.getLon(i)));
                }

                currentRouteLine.setPoints(geoPoints);

                // 4. Добавляем на карту и обновляем её
                mapView.getOverlays().add(currentRouteLine);
                mapView.invalidate();

                // Опционально: показать информацию о маршруте
                double distanceKm = path.getDistance() / 1000;
                long timeMin = path.getTime() / 60000;
                Log.d("GH", "Дистанция: " + distanceKm + "км, Время: " + timeMin + " мин.");
            }
        }.execute();
    }

    private void performSearch(String query) {
        if (query == null || query.isEmpty()) return;
        LatLong result = dbHelper.getCoordinates(query);

        if (result != null) {
            GeoPoint targetPoint = new GeoPoint(result.getLatitude(), result.getLongitude());

            // Расчет данных (без линии)
            if (mLocationOverlay.getMyLocation() != null) {
                calcPath(mLocationOverlay.getMyLocation().getLatitude(),
                        mLocationOverlay.getMyLocation().getLongitude(),
                        result.getLatitude(), result.getLongitude());
            }

            mapView.getController().animateTo(targetPoint);
            mapView.getController().setZoom(18.0);

            Marker addressMarker = new Marker(mapView);
            addressMarker.setPosition(targetPoint);
            addressMarker.setTitle(query);

            mapView.getOverlays().removeIf(o -> o instanceof Marker && !(o instanceof MyLocationNewOverlay));
            mapView.getOverlays().add(addressMarker);
            mapView.invalidate();
        }
    }

    private File getMapFile() {
        File file = new File(requireContext().getExternalFilesDir(null), mapFileName);
        if (!file.exists()) {
            try (InputStream is = requireContext().getAssets().open(mapFileName);
                 OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = is.read(buffer)) > 0) os.write(buffer, 0, length);
            } catch (IOException e) {
                Log.e("MAP", "Ошибка карты: " + e.getMessage());
            }
        }
        return file;
    }

    private void unpackZipFromAssets(String zipName, File targetDir) throws IOException {
        if (!targetDir.exists()) targetDir.mkdirs();

        try (InputStream is = requireContext().getAssets().open(zipName);
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(targetDir, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    Log.d("UNPACK", "Распакован файл: " + file.getAbsolutePath());
                }
                zis.closeEntry();
            }
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) { return false; }
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                var myPosition=mLocationOverlay.getMyLocation();
                calcPath(myPosition.getLatitude(),myPosition.getLongitude(),

                        dbHelper.getCoordinates(query).getLatitude(),
                        dbHelper.getCoordinates(query).getLongitude());
                searchView.clearFocus();

                return true;
            }
        });
    }

    @Override public void onPause() { super.onPause(); if (mapView != null) mapView.onPause(); }
    @Override public void onDestroyView() {
        super.onDestroyView();
        if (tileProvider != null) tileProvider.detach();
        if (mapView != null) mapView.onDetach();
    }
}
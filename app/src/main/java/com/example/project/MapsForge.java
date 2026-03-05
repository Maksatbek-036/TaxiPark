package com.example.project;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MapsForge extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay mLocationOverlay;
    private GpsMyLocationProvider locationProvider;
    SearchView searchView;
    private DatabaseHelper dbHelper;
    // Имя файла в assets (без папки assets/)
    private String mapFileName = "kyrgyzstan.map";
    private FloatingActionButton fabMyLocation;
    private MapsForgeTileProvider tileProvider;

    public MapsForge() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Обязательно инициализируем фабрику графики Mapsforge
        AndroidGraphicFactory.createInstance(requireActivity().getApplication());


        Configuration.getInstance().load(requireContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()));

        dbHelper = new DatabaseHelper(requireContext());
        checkPermissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_forge, container, false);
        mapView = view.findViewById(R.id.mapview);
        fabMyLocation=view.findViewById(R.id.fab_my_location);
        searchView=view.findViewById(R.id.search_view);

        mapView.setMultiTouchControls(true);
        setupOfflineMap();
        getCurrentLocation();

        fabMyLocation.setOnClickListener(v -> {
            if (mLocationOverlay != null) {
                GeoPoint myLocation = mLocationOverlay.getMyLocation();
                if (myLocation != null) {
                    mapView.getController().animateTo(myLocation);
                    mapView.getController().setZoom(17.0);
                    Toast.makeText(getContext(), "Широта: " + myLocation.getLatitude() + ", Долгота: " + myLocation, Toast.LENGTH_SHORT).show();
                    mLocationOverlay.enableFollowLocation(); // Снова следовать за мной
                } else {
                    Toast.makeText(getContext(), "Поиск спутников...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;

    }

    private void setupOfflineMap() {
        File mapFile = getMapFile();
        if (mapFile.exists()) {
            try {
                // Создаем источник плиток из файла .map
                MapsForgeTileSource tileSource = MapsForgeTileSource.createFromFiles(new File[]{mapFile});

                tileProvider = new MapsForgeTileProvider(
                        new SimpleRegisterReceiver(requireContext()),
                        tileSource,
                        null
                );

                mapView.setTileProvider(tileProvider);
                locationProvider = new GpsMyLocationProvider(requireContext());
                // Можно настроить частоту обновления (например, раз в 2 секунды или 5 метров)
                locationProvider.setLocationUpdateMinDistance(5);
                locationProvider.setLocationUpdateMinTime(2000);
                mLocationOverlay = new MyLocationNewOverlay(locationProvider, mapView);

                // Включаем отображение точки на карте
                mLocationOverlay.enableMyLocation();

                // Включаем следование камеры за местоположением (авто-центровка)
                mLocationOverlay.enableFollowLocation();

                // Делаем иконку в виде стрелочки (показывает направление движения)
                mLocationOverlay.setDrawAccuracyEnabled(true);

                // Добавляем слой локации поверх карты
                mapView.getOverlays().add(mLocationOverlay);
                // --------------------------------------------

                mapView.getController().setZoom(15.0);


            } catch (Exception e) {
                Log.e("MAP", "Ошибка загрузки карты: " + e.getMessage());
            }
        } else {
            Toast.makeText(getContext(), "Файл карты не найден!", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    public LatLong getCurrentLocation() {
        if (mLocationOverlay != null && mLocationOverlay.getMyLocation() != null) {
            double lat = mLocationOverlay.getMyLocation().getLatitude();
            double lon = mLocationOverlay.getMyLocation().getLongitude();
            Toast.makeText(getContext(), "Широта: " + lat + ", Долгота: " + lon, Toast.LENGTH_SHORT).show();
            return new LatLong(lat, lon);

        }
        return null;
    }

    private void performSearch(String query) {
        if (query == null || query.isEmpty()) return;

        // Вызываем наш универсальный метод
        LatLong result = dbHelper.getCoordinates(query);

        if (result != null) {
            GeoPoint targetPoint = new GeoPoint(result.getLatitude(), result.getLongitude());

            // 1. Двигаем камеру
            mapView.getController().animateTo(targetPoint);
            mapView.getController().setZoom(18.0);

            // 2. Добавляем маркер на найденный адрес
            Marker addressMarker = new Marker(mapView);
            addressMarker.setPosition(targetPoint);
            addressMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            addressMarker.setTitle(query);


            // Очищаем предыдущие поисковые маркеры (но не mLocationOverlay)
            mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker && !(overlay instanceof MyLocationNewOverlay));

            mapView.getOverlays().add(addressMarker);
            mapView.invalidate(); // Перерисовать карту
        } else {
            Toast.makeText(getContext(), "Адрес не найден в базе Бишкека", Toast.LENGTH_SHORT).show();
        }
    }
    private File getMapFile() {
        // Копируем во внутреннюю память приложения
        File file = new File(requireContext().getExternalFilesDir(null), mapFileName);

        if (!file.exists()) {
            try (InputStream is = requireContext().getAssets().open(mapFileName);
                 OutputStream os = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192]; // Буфер побольше для ускорения
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                Log.d("MAP", "Карта успешно скопирована");
            } catch (IOException e) {
                Log.e("MAP", "Ошибка копирования карты: " + e.getMessage());
            }
        }
        return file;
    }




    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // Просто выполняем поиск, НЕ трогаем фокус
                performSearch(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // А вот здесь фокус можно убрать, так как пользователь нажал Enter
                searchView.clearFocus();
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tileProvider != null) tileProvider.detach();
        if (mapView != null) mapView.onDetach();
    }
}
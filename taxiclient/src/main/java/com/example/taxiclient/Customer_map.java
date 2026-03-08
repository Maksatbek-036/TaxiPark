package com.example.taxiclient;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.osmdroid.config.Configuration;
import org.osmdroid.mapsforge.MapsForgeTileProvider;
import org.osmdroid.mapsforge.MapsForgeTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Customer_map extends AppCompatActivity {
    private String mapFileName = "kyrgyzstan.map";
    private MapView mapView;
    private MyLocationNewOverlay mLocationOverlay;
    private MapsForgeTileProvider tileProvider;
    private GpsMyLocationProvider locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AndroidGraphicFactory.createInstance(getApplication());

        // Настройка OSMDroid
        Configuration.getInstance().load(this,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this));

        setupOfflineMap();

    }
    private void setupOfflineMap() {
        File mapFile = getMapFile();
        if (mapFile.exists()) {
            try {
                MapsForgeTileSource tileSource = MapsForgeTileSource.createFromFiles(new File[]{mapFile});
                tileProvider = new MapsForgeTileProvider(
                        new SimpleRegisterReceiver(this),
                        tileSource,
                        null
                );

                mapView.setTileProvider(tileProvider);
                locationProvider = new GpsMyLocationProvider(this);
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
    private File getMapFile() {
        File file = new File(this.getExternalFilesDir(null), mapFileName);
        if (!file.exists()) {
            try (InputStream is = this.getAssets().open(mapFileName);
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
}
package com.example.project;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private String pointB; // координаты клиента

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidGraphicFactory.createInstance(getApplication());
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_accept_order);

        initViews();
        setupMap();
        initNavigation();
    }

    private void initViews() {
        mapView = findViewById(R.id.map);
        orderNumber = findViewById(R.id.orderNumber);
        orderTariff = findViewById(R.id.orderTariff);
        orderDistance = findViewById(R.id.orderDistance);
        orderAddress = findViewById(R.id.orderAddress);
       

        Intent intent = getIntent();
        int orderId = intent.getIntExtra("ORDER_ID", -1);
        String pointA = intent.getStringExtra("POINT_A");
        pointB = intent.getStringExtra("POINT_B");

        orderNumber.setText("Заказ №" + orderId);
        orderAddress.setText(pointB);

        acceptButton.setOnClickListener(v -> {
            try {
                // pointB в формате "lat,lon"
                String[] coords = pointB.split(",");
                double lat = Double.parseDouble(coords[0]);
                double lon = Double.parseDouble(coords[1]);

                GeoPoint destination = new GeoPoint(lat, lon);
                drawRouteToClient(destination);

                Toast.makeText(this, "Маршрут построен", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка координат: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMap() {
        mapView.setMultiTouchControls(true);

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

        new AsyncTask<Void, Void, GHResponse>() {
            @Override
            protected GHResponse doInBackground(Void... voids) {
                GHRequest req = new GHRequest(start.getLatitude(), start.getLongitude(),
                        dest.getLatitude(), dest.getLongitude())
                        .setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI)
                        .setVehicle("car");
                return hopper.route(req);
            }

            @Override
            protected void onPostExecute(GHResponse res) {
                if (res == null || res.hasErrors()) return;

                // расстояние
                double distanceKm = res.getBest().getDistance() / 1000.0;
                orderDistance.setText(String.format("%.2f км", distanceKm));

                PointList points = res.getBest().getPoints();
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

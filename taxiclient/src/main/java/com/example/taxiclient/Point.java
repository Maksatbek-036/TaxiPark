package com.example.taxiclient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxiclient.API.Api;
import com.example.taxiclient.API.RetrofitClient;
import com.example.taxiclient.Layouttariff.TarifAdatpter;
import com.example.taxiclient.Layouttariff.Tariff;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;

import org.mapsforge.core.model.LatLong;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Point extends Fragment {

    private RecyclerView recyclerView;
    private EditText editTextPointB;
    private Button btn;

    private List<Tariff> tariffList;
    private TarifAdatpter adapter;

    private FusedLocationProviderClient fusedLocationClient;
    private GraphHopper hopper;
    private DatabaseHelper dbHelper;
    private boolean isHopperReady = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация сервиса геолокации
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        dbHelper = new DatabaseHelper(getContext());
        tariffList = new ArrayList<>();

        initGraphHopper();
        loadTariffs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_point, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.tariffs);
        editTextPointB = view.findViewById(R.id.edit_text_b);
        btn = view.findViewById(R.id.btn_order);

        // Настройка горизонтального списка тарифов
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new TarifAdatpter(tariffList);
        recyclerView.setAdapter(adapter);

        btn.setOnClickListener(v -> startOrderProcess());
    }

    private void startOrderProcess() {
        // 1. Проверка разрешений
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        // 2. Получение координат через FusedLocation
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                calculateDistanceAndPrice(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(getContext(), "Включите GPS для определения местоположения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDistanceAndPrice(double myLat, double myLon) {
        String addressB = editTextPointB.getText().toString().trim();
        Tariff selectedTariff = adapter.getSelectedTariff();

        if (addressB.isEmpty() || selectedTariff == null) {
            Toast.makeText(getContext(), "Введите адрес и выберите тариф", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLong coordsB = dbHelper.getCoordinates(addressB);
        if (coordsB == null) {
            Toast.makeText(getContext(), "Адрес не найден в базе данных", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(() -> {
            double distance = getDistanceBetween(myLat, myLon, coordsB.getLatitude(), coordsB.getLongitude());

            requireActivity().runOnUiThread(() -> {
                if (distance > 0) {

                    int finalPrice = (int) (selectedTariff.getPrice() * distance);

                   Memory memory = new Memory(requireContext());

                    Order order = new Order();
                    order.setClientId(memory.getClient().getId()); // ID текущего клиента
                    order.setPointA("Мое местоположение");
                    order.setTariffId(selectedTariff.getId());
                    order.setTotalPrice(finalPrice);

                    // Переход на оплату
                    Intent intent = new Intent(getActivity(), FormPay.class);
                    intent.putExtra("ORDER_DATA", order);
                    intent.putExtra("ARG_ADDR_B", addressB);
                    intent.putExtra("DISTANCE", String.format("%.1f", distance));
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Не удалось проложить маршрут", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void loadTariffs() {
        RetrofitClient.getInstance().getApi().getTariffs().enqueue(new Callback<List<Tariff>>() {
            @Override
            public void onResponse(Call<List<Tariff>> call, Response<List<Tariff>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tariffList.clear();
                    tariffList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Tariff>> call, Throwable t) {
                Log.e("API", "Ошибка загрузки тарифов: " + t.getMessage());
                // Резервные данные, если сервер не отвечает
                tariffList.add(new Tariff("Эконом", 120));
                tariffList.add(new Tariff("Комфорт", 180));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initGraphHopper() {
        File rootFolder = new File(requireContext().getExternalFilesDir(null), "graph-data");
        new Thread(() -> {
            try {
                File nestedFolder = new File(rootFolder, "graph-data");
                File finalPath = new File(nestedFolder, "nodes").exists() ? nestedFolder : rootFolder;

                if (new File(finalPath, "nodes").exists()) {
                    hopper = new GraphHopper().forMobile();
                    hopper.load(finalPath.getAbsolutePath());
                    isHopperReady = true;
                }
            } catch (Exception e) {
                Log.e("GH", "Ошибка GraphHopper: " + e.getMessage());
            }
        }).start();
    }

    private double getDistanceBetween(double fromLat, double fromLon, double toLat, double toLon) {
        if (!isHopperReady || hopper == null) return 0.0;
        GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("car");
        GHResponse res = hopper.route(req);
        if (res.hasErrors()) return 0.0;
        return res.getBest().getDistance() / 1000.0; // Возвращаем в КМ
    }
}
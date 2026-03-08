package com.example.taxiclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taxiclient.API.Api;
import com.example.taxiclient.Layouttariff.TarifAdatpter;
import  com.example.taxiclient.Layouttariff.Tariff;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
ate an instance of this fragment.
 */
public class Point extends Fragment  {
EditText editTextPointA;
private RecyclerView recyclerView;
EditText editTextPointB;
private List<Tariff> tariffList;
Retrofit retrofit;
Button btn;
TarifAdatpter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
tariffList=new ArrayList<>();
loadTariffs();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_point, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.tariffs);
        editTextPointA=view.findViewById(R.id.edit_text_a);
        editTextPointB=view.findViewById(R.id.edit_text_b);
        btn=view.findViewById(R.id.btn_order);






        // Внутри Point.java
        btn.setOnClickListener(v -> {
            String addressA = editTextPointA.getText().toString().trim();
            String addressB = editTextPointB.getText().toString().trim();

            if (!addressA.isEmpty() && !addressB.isEmpty()) {
                // Вызываем метод в MainActivity
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onRouteSelected(addressA, addressB);
                }
            } else {
                Toast.makeText(getContext(), "Введите оба адреса", Toast.LENGTH_SHORT).show();
            }
        });
        adapter=new TarifAdatpter(tariffList);
        recyclerView.setAdapter(adapter);



    }
    private void loadTariffs() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.106:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);

        api.getTariffs().enqueue(new Callback<List<Tariff>>() {

            @Override
            public void onResponse(Call<List<Tariff>> call, Response<List<Tariff>> response) {
if(response.isSuccessful() && response.body()!=null) {
    tariffList = response.body();
    adapter=new TarifAdatpter(tariffList);
    recyclerView.setAdapter(adapter);
}else{
    Toast.makeText(getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();

}
            }

            @Override
            public void onFailure(Call<List<Tariff>> call, Throwable t) {
                Log.e("API", "Ошибка загрузки: " + t.getMessage());
            }
        });



    }
}
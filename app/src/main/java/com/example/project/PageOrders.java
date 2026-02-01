package com.example.project;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.API.Api;
import com.example.project.API.LoginApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PageOrders extends Fragment {
private RecyclerView recyclerView;


private TextView emptyView;
private ArrayList<Order> orderArrayList=new ArrayList<>();
private Retrofit retrofit;
    MyAdapter myAdapter;



    public PageOrders() {


    }




    private void loadData() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.106:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        api.getOrders().enqueue(new retrofit2.Callback<List<Order>>() {

            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                // Проверяем, что фрагмент всё еще "жив"
                if (!isAdded() || getContext() == null)
                    return;

                if (response.isSuccessful() && response.body() != null) {
                   // Очищаем старые, если нужно
                    orderArrayList.addAll(response.body());

                    myAdapter=new MyAdapter(orderArrayList,requireContext(),api);
                    recyclerView.setAdapter(myAdapter);

                    updateUI();


                }
                else {
                    Toast.makeText(getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;

                Log.e("RETROFIT", "Ошибка сети: " + t.getMessage());
                updateUI();
                Toast.makeText(getContext(), "Проверьте интернет-соединение", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI() {
        if (orderArrayList == null || orderArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
        View root=inflater.inflate(R.layout.fragment_page_orders,container,false);

        recyclerView=root.findViewById(R.id.orders);
        emptyView=root.findViewById(R.id.emptyView);
        loadData();



        return root;

    }


}
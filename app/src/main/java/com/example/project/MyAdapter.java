package com.example.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.API.Api;
import com.example.project.API.OrderAcceptRequest;
import com.example.project.Responses.DriverResponce;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Api api;
    private ArrayList<Order> orderArrayList;
    private Retrofit retrofit;
    private int orderId;
Memory memory;


    public MyAdapter(ArrayList<Order> orderArrayList, Context context, Api api) {

        this.orderArrayList = orderArrayList;

        memory=new Memory(context);
        this.api=api;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false));

    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order currentOrder = orderArrayList.get(position);
        holder.bind(currentOrder);

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                // 1. Создаем диалоговое окно
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Подтверждение")
                        .setMessage("Вы действительно хотите принять заказ №" + currentOrder.getId() + "?")
                        .setNegativeButton("Отмена", null) // При нажатии "Отмена" ничего не происходит
                        .setPositiveButton("Принять", (dialog, which) -> {

                            // 2. Если пользователь нажал "Принять", выполняем вашу логику
                            acceptOrderLogic(v, currentOrder);

                        })
                        .show();
            }
        });
    }

    // Выносим логику в отдельный метод для чистоты кода
    private void acceptOrderLogic(View v, Order currentOrder) {
        DriverResponce driver = memory.getDriver();
        if (driver == null) {
            Toast.makeText(v.getContext(), "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }

        int driverId = driver.getId();
        int orderId = currentOrder.getId();
        OrderAcceptRequest request = new OrderAcceptRequest(driverId, orderId);

        api.acceptOrder(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    Toast.makeText(v.getContext(), "Заказ принят", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), AcceptOrder.class);

                    // Совет: передайте ID заказа в следующую активность, чтобы там его отобразить
                    intent.putExtra("ORDER_ID", orderId);
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(v.getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }


}

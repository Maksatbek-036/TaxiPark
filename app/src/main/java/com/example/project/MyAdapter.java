package com.example.project;

import android.annotation.SuppressLint;
import android.content.Context;
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
        Order currentOrder=orderArrayList.get(position);
        holder.bind(orderArrayList.get(position));
holder.btnAccept.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        DriverResponce driver=memory.getDriver();
        if(driver==null){
            Toast.makeText(v.getContext(), "Войдите в аккаунт", Toast.LENGTH_SHORT).show();

        }
        int driverId=driver.getId();
        int orderId=orderArrayList.get(position).getId();
        OrderAcceptRequest request=new OrderAcceptRequest(driverId,orderId);


api.acceptOrder(request).enqueue(new retrofit2.Callback<Void>() {
    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
if(response.isSuccessful() && response.code()==200) {
    Toast.makeText(v.getContext(), "Заказ принят", Toast.LENGTH_SHORT).show();
}else{
    Toast.makeText(v.getContext(), "Ошибка сервера"+response.code(), Toast.LENGTH_SHORT).show();
}
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        Toast.makeText(v.getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }
});

    }
});





    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }


}

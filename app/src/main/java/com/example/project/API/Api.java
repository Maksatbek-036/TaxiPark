package com.example.project.API;

import com.example.project.Order;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

public interface Api {
    @GET("orders")
    Call<List<Order>>  getOrders();
    @PATCH("orders/order/accept")
    Call<Void> acceptOrder(@Body OrderAcceptRequest request);



}


package com.example.taxiclient.API;

import com.example.taxiclient.Layouttariff.Tariff;
import com.example.taxiclient.Order;
import com.example.taxiclient.Response.ClientResponce;
import com.example.taxiclient.Response.DriverInfoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    @POST("client") // Ваш эндпоинт
    Call<Void> registerUser(@Body RegisterRequest request);
    @POST("client/login")
    Call<ClientResponce> login(@Body RegisterRequest request);

    @POST("order")
    Call<Order> createOrder(@Body OrderRequest request);
    @GET("tariff")
    Call<List<Tariff>> getTariffs();
    @GET("orders/driver")
    Call<DriverInfoDTO> getOrdersForDriver(@Query("driverId") int driverId);



}

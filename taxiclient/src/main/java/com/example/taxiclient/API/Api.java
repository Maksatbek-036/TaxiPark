package com.example.taxiclient.API;

import com.example.taxiclient.Layouttariff.Tariff;
import com.example.taxiclient.Response.ClientResponce;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    @POST("client") // Ваш эндпоинт
    Call<Void> registerUser(@Body RegisterRequest request);
    @POST("login")
    Call<ClientResponce> login(@Body RegisterRequest request);

    @POST("orders")
    Call<Void> createOrder(@Body OrderRequest request);
    @GET("tariff")
    Call<List<Tariff>> getTariffs();



}

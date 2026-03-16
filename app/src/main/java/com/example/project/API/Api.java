package com.example.project.API;

import com.example.project.Order;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {
    @GET("orders")
    Call<List<Order>>  getOrders();
    @PATCH("orders/order/accept")
    Call<Void> acceptOrder(@Body OrderAcceptRequest request);



    @Multipart
    @POST("driver/edit")
    Call<Void> driverEdit(
            @Part MultipartBody.Part id,
            @Part MultipartBody.Part name,
            @Part MultipartBody.Part phone,
            @Part MultipartBody.Part login,
            @Part MultipartBody.Part profilePhoto,
            @Part MultipartBody.Part licenses
    );


}


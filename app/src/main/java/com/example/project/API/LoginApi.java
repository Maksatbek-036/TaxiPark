package com.example.project.API;

import com.example.project.Responses.DriverResponce;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("driver/login")
    Call<DriverResponce> login(@Body RegisterRequest request);

}

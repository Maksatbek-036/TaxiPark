package com.example.project.API;

import com.example.project.Responses.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterAPI {
    @POST("driver") // Ваш эндпоинт
    Call<Void> registerUser(@Body RegisterRequest request);
}

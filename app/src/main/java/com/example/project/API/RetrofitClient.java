package com.example.project.API;

import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Укажите ваш базовый URL (обязательно заканчивается на /)
    private static final String BASE_URL = ApiConfig.getBaseUrl();
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {



        OkHttpClient client = new OkHttpClient.Builder()
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL+"/")
                .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON в Java объекты
                .client(client)
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
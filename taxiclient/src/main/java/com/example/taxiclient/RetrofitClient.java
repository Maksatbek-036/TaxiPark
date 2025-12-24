package com.example.taxiclient;

import com.example.taxiclient.RequesModel.Client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Client getApi(String username,String password) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.106:5000/client?login=" +username+
                            "&password="+password) // Укажите IP вашего ПК
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(Client.class);
    }
}
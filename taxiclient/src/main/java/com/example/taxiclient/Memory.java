package com.example.taxiclient;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.taxiclient.Response.ClientResponce;
import com.google.gson.Gson;

public class Memory {
    private static final String PREFS_NAME = "TaxiClientPrefs";
    private static final String KEY_CLIENT = "client_data";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public Memory(Context context) {
        // Используем context.getApplicationContext(), чтобы избежать утечек памяти
        this.sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // Сохраняем весь объект клиента целиком
    public void saveClient(ClientResponce client) {
        String json = gson.toJson(client);
        sharedPreferences.edit().putString(KEY_CLIENT, json).apply();
    }

    // Достаем объект клиента
    public ClientResponce getClient() {
        String json = sharedPreferences.getString(KEY_CLIENT, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, ClientResponce.class);
    }

    // Метод для выхода из аккаунта (очистка данных)
    public void clear() {
        sharedPreferences.edit().remove(KEY_CLIENT).apply();
    }
}
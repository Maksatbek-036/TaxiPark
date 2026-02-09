package com.example.taxiclient;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.taxiclient.Response.ClientResponce;
import com.google.gson.Gson;

public class Memory {
    private static final String PREF_NAME = "client_prefs";
    private static final String KEY_CLIENT = "current_client";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public Memory(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Сохранение
    public void saveClient(ClientResponce driver) {
        String json = gson.toJson(driver);
        sharedPreferences.edit().putString(KEY_CLIENT, json).apply();
    }

    // Получение
    public ClientResponce getClient() {
        String json = sharedPreferences.getString(KEY_CLIENT, null);
        if (json == null) return null;
        return gson.fromJson(json, ClientResponce.class);
    }

    // Очистка (при Logout)
    public void clear() {
        sharedPreferences.edit().remove(KEY_CLIENT).apply();
    }
}

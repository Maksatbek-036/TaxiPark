package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.project.Responses.DriverResponce;
import com.google.gson.Gson;

public class Memory {
    private static final String PREF_NAME = "driver_prefs";
    private static final String KEY_DRIVER = "current_driver";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public Memory(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Сохранение
    public void saveDriver(DriverResponce driver) {
        String json = gson.toJson(driver);
        sharedPreferences.edit().putString(KEY_DRIVER, json).apply();
    }

    // Получение
    public DriverResponce getDriver() {
        String json = sharedPreferences.getString(KEY_DRIVER, null);
        if (json == null) return null;
        return gson.fromJson(json, DriverResponce.class);
    }

    // Очистка (при Logout)
    public void clear() {
        sharedPreferences.edit().remove(KEY_DRIVER).apply();
    }
}

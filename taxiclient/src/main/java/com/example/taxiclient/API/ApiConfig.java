package com.example.taxiclient.API;

import android.os.Build;

public class ApiConfig {
    public static String getBaseUrl() {
        String emulatorUrl = "http://10.0.2.2:5001";
        String phoneUrl = "http://192.168.0.106:5001"; // IP твоего ПК

        if (isEmulator()) {
            return emulatorUrl;
        } else {
            return phoneUrl;
        }
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.contains("generic")
                || Build.FINGERPRINT.contains("emulator")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86");
    }
}

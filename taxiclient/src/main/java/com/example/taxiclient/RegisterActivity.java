package com.example.taxiclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxiclient.API.RegisterRequest;

import com.example.taxiclient.API.Api;
import com.example.taxiclient.API.RetrofitClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etConfirm;
    Button btnRegister, btnToLogin;
    private Api registerAPI;

RetrofitClient retrofitClient = RetrofitClient.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        btnToLogin = findViewById(R.id.btnToLogin); // кнопка "Уже есть аккаунт"

//        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        registerAPI = retrofitClient.getApi();


        // Регистрация нового пользователя
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Все поля обязательны!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser(username, password);
//

        });

        // Кнопка "Уже есть аккаунт" → переход на LoginActivity
        btnToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser(String username, String password) {

registerAPI.registerUser(new RegisterRequest(username, password)).enqueue(new Callback<Void>() {
    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
  if (response.isSuccessful()) {
      Log.d("RETROFIT", "Успех!");
      startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
      finish();
  } else {
      Log.e("RETROFIT", "Ошибка сервера: " + response.code());
  }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
Toast.makeText(RegisterActivity.this, "Ошибка регистрации: " + t.getMessage(), Toast.LENGTH_SHORT).show();

    }
});

    }
}
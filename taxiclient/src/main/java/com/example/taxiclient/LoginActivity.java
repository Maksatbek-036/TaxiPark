package com.example.taxiclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxiclient.API.Api;
import com.example.taxiclient.API.RegisterRequest;
import com.example.taxiclient.API.RetrofitClient;
import com.example.taxiclient.Response.ClientResponce;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    Api loginApi;
    Memory memory; // Только объявляем

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Инициализируем память ПЕРЕД проверкой и установкой View
        memory = new Memory(this);

        // 2. Если клиент уже залогинен — сразу на главную
        if (memory.getClient() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String login = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Все поля обязательны!", Toast.LENGTH_SHORT).show();
                return;
            }
            login(login, password);
        });
    }

    private void login(String login, String password) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        loginApi = retrofitClient.getApi();
        RegisterRequest request = new RegisterRequest(login, password);

        loginApi.login(request).enqueue(new Callback<ClientResponce>() {
            @Override
            public void onResponse(Call<ClientResponce> call, Response<ClientResponce> response) {
                // Проверяем: успешен ли ответ И есть ли тело ответа
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RETROFIT", "Успех!");
                    Toast.makeText(LoginActivity.this, "Вход успешен!", Toast.LENGTH_SHORT).show();

                    memory.saveClient(response.body());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Если ошибка (например, 401 или 500), response.body() будет null
                    // Пытаемся достать текст ошибки из errorBody безопасно
                    String errorMessage = "Ошибка сервера";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMessage = "Ошибка: " + response.code();
                    }

                    Log.e("RETROFIT", "Ошибка: " + errorMessage);
                    Toast.makeText(LoginActivity.this, "Ошибка входа: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ClientResponce> call, Throwable t) {
                // Проверяем t на null перед вызовом getMessage()
                String message = (t != null) ? t.getMessage() : "Неизвестная ошибка сети";
                Log.e("RETROFIT", "Ошибка сети: " + message);
                Toast.makeText(LoginActivity.this, "Проблема с соединением", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
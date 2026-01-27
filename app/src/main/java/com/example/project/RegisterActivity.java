package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.API.RegisterAPI;
import com.example.project.API.RegisterRequest;
import com.example.project.Responses.RegisterResponse;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etConfirm;
    Button btnRegister, btnToLogin;
    private RegisterAPI registerAPI;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        btnToLogin = findViewById(R.id.btnToLogin); // кнопка "Уже есть аккаунт"
Retrofit retrofit=new Retrofit.Builder()
        .baseUrl("http://192.168.0.106:5001/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
//        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
registerAPI = retrofit.create(RegisterAPI.class);

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
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("username", username);
//            editor.putString("password", password);
//            editor.apply();

        });

        // Кнопка "Уже есть аккаунт" → переход на LoginActivity
        btnToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
//регистрация
    private void registerUser(String username, String password) {
        RegisterRequest request = new RegisterRequest(username, password);

Call<Void> call = registerAPI.registerUser(request);
call.enqueue(new Callback<Void>() {
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
        Log.e("RETROFIT", "Ошибка сети: " + t.getMessage());
    }




}) ;




 }
}
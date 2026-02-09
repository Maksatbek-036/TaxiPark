package com.example.taxiclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taxiclient.API.Api;
import com.example.taxiclient.API.RegisterRequest;

import com.example.taxiclient.Response.ClientResponce;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    Api loginApi;
    Memory memory;

 ClientResponce clientResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://192.168.0.106:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        memory=new Memory(this);

        if (memory.getClient() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        loginApi = retrofit.create(Api.class);


//         sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);


        btnLogin.setOnClickListener(v -> {
            String login = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Все поля обязательны!", Toast.LENGTH_SHORT).show();
                return;
            }
//            String savedUsername = sharedPreferences.getString("username", "");
//            String savedPassword = sharedPreferences.getString("password", "");

//            if (username.equals(savedUsername) && password.equals(savedPassword)) {
//                Toast.makeText(this, "Вход успешен!", Toast.LENGTH_SHORT).show();
//                // После входа можно перейти обратно на MainActivity или другой экран
//startActivity(new Intent(this, MainActivity.class));
//finish();
//            } else {
//                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
//            }
            login(login,password);



        });
    }
    private ClientResponce login(String login, String password) {
        RegisterRequest request = new RegisterRequest(login, password);

        Call<ClientResponce> call = loginApi.login(request);
        call.enqueue(new Callback<ClientResponce>() {
                         @Override
                         public void onResponse(Call<ClientResponce> call, Response<ClientResponce> response) {
                             if(response.isSuccessful() && response.body()!=null){
                                 clientResponse=response.body();
                                 memory.saveClient(clientResponse);

                                 startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                 finish();
                                 Toast.makeText(LoginActivity.this, "Вход успешен!", Toast.LENGTH_SHORT).show();
                             }else if (response.code() == 401) {
                                 Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                             }

                         }

                         @Override
                         public void onFailure(Call<ClientResponce> call, Throwable t) {
                             Toast.makeText(LoginActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                             Log.e("RETROFIT", "Ошибка сети: " + t.getMessage());
                         }
                     }

        );
        return clientResponse;
    }


}

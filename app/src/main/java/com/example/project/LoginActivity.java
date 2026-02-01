package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.API.LoginApi;
import com.example.project.API.RegisterRequest;
import com.example.project.Responses.DriverResponce;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpRequest;

import java.net.HttpURLConnection;
import java.net.URI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    LoginApi loginApi;
    Memory memory;

    DriverResponce driverResponce;


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

        if (memory.getDriver() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
loginApi = retrofit.create(LoginApi.class);


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
    private DriverResponce login(String login, String password) {
        RegisterRequest request = new RegisterRequest(login, password);

Call<DriverResponce> call = loginApi.login(request);
call.enqueue(new Callback<DriverResponce>() {
            @Override
            public void onResponse(Call<DriverResponce> call, Response<DriverResponce> response) {
if(response.isSuccessful() && response.body()!=null){
 driverResponce=response.body();
   memory.saveDriver(driverResponce);

    startActivity(new Intent(LoginActivity.this, MainActivity.class));
    finish();
    Toast.makeText(LoginActivity.this, "Вход успешен!", Toast.LENGTH_SHORT).show();
}else if (response.code() == 401) {
   Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
}

            }

            @Override
            public void onFailure(Call<DriverResponce> call, Throwable t) {
Toast.makeText(LoginActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
Log.e("RETROFIT", "Ошибка сети: " + t.getMessage());
            }
        }

);
        return driverResponce;
    }


}

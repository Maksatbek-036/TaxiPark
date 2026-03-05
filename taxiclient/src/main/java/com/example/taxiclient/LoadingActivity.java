package com.example.taxiclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    private Button btnCancel;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvivity_loading);

        btnCancel = findViewById(R.id.btnCancel);


        task = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }
        };
        handler.postDelayed(task, 3000);

        // обработка нажатия "Отмена"
        btnCancel.setOnClickListener(v -> {
            handler.removeCallbacks(task); // отменяем задачу
            finish(); // закрываем экран ожидания
        });
    }
}

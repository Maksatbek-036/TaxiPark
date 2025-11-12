package com.example.taxiclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DestinationActivity extends AppCompatActivity {

    private EditText destinationEditText;
    private Button confirmButton;
    private LinearLayout cardLayout;
    private ImageView arrowIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        initViews();
        setupAnimations();
        setupListeners();
    }

    private void initViews() {
        destinationEditText = findViewById(R.id.destinationEditText);
        confirmButton = findViewById(R.id.confirmButton);
        cardLayout = findViewById(R.id.cardLayout);
        arrowIcon = findViewById(R.id.arrowIcon);
    }

    private void setupAnimations() {
        // Анимация появления карточки
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        cardLayout.startAnimation(slideUp);

        // Анимация пульсации иконки
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        arrowIcon.startAnimation(pulse);
    }

    private void setupListeners() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination = destinationEditText.getText().toString().trim();

                if (destination.isEmpty()) {
                    // Анимация тряски поля ввода при ошибке
                    Animation shake = AnimationUtils.loadAnimation(DestinationActivity.this, R.anim.shake);
                    destinationEditText.startAnimation(shake);

                    Toast.makeText(DestinationActivity.this,
                            "Введите пункт назначения", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Анимация нажатия кнопки
                Animation scale = AnimationUtils.loadAnimation(DestinationActivity.this, R.anim.scale);
                confirmButton.startAnimation(scale);

                // Передача данных обратно
                Intent resultIntent = new Intent();
                resultIntent.putExtra("DESTINATION", destination);
                setResult(RESULT_OK, resultIntent);

                // Задержка для завершения анимации перед закрытием
                new Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                finish();
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            }
                        }, 200);
            }
        });
    }
}
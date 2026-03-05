package com.example.taxiclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.taxiclient.R;

public class FormPay extends AppCompatActivity {

    // Объявляем переменные для контейнеров
    private CardView cardPaymentContainer;
    private CardView cashPaymentContainer;

    // Переменная для хранения текущего выбора
    private String selectedPaymentMethod = "NONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pay);

        // Инициализация View
        cardPaymentContainer = findViewById(R.id.cardPaymentContainer);
        cashPaymentContainer = findViewById(R.id.cashPaymentContainer);
        Button doneButton = findViewById(R.id.doneButton);

        // Установка слушателей
        cardPaymentContainer.setOnClickListener(v -> selectPaymentMethod("CARD"));
        cashPaymentContainer.setOnClickListener(v -> selectPaymentMethod("CASH"));

        doneButton.setOnClickListener(v -> {
            // Логика при нажатии "Готово"
            Toast.makeText(this, "Выбрано: " + selectedPaymentMethod, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(FormPay.this, LoadingActivity.class));
        });
    }

    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;

        // Сбрасываем стили обоих элементов
        resetStyles();

        // Применяем стиль к выбранному элементу
        if (method.equals("CARD")) {
            highlightView(cardPaymentContainer);
        } else if (method.equals("CASH")) {
            highlightView(cashPaymentContainer);
        }
    }

    private void resetStyles() {
        // Цвет по умолчанию из вашего XML (#F4F2F5)
        int defaultColor = Color.parseColor("#F4F2F5");
        cardPaymentContainer.setCardBackgroundColor(defaultColor);
        cashPaymentContainer.setCardBackgroundColor(defaultColor);

        // Если внутри CardView есть ConstraintLayout с фоном (как в вашем XML),
        // его тоже нужно сбросить или убрать фиксированный фон из XML
        cardPaymentContainer.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        cashPaymentContainer.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
    }

    private void highlightView(CardView view) {
        // Цвет выделения (например, светло-серый или синий акцент)
        view.setCardBackgroundColor(Color.parseColor("#008000"));
        // Можно также добавить программную смену Stroke (обводки), если используете MaterialCardView
    }
}
package com.example.taxiclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.taxiclient.API.Api;
import com.example.taxiclient.API.OrderRequest;
import com.example.taxiclient.API.RetrofitClient;
import com.example.taxiclient.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormPay extends AppCompatActivity {


    private CardView cardPaymentContainer;
    private CardView cashPaymentContainer;

Memory memory;
    private String selectedPaymentMethod = "NONE";
    RetrofitClient retrofitClient = RetrofitClient.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pay);

        cardPaymentContainer = findViewById(R.id.cardPaymentContainer);
        cashPaymentContainer = findViewById(R.id.cashPaymentContainer);
        Button doneButton = findViewById(R.id.doneButton);

        cardPaymentContainer.setOnClickListener(v -> selectPaymentMethod("CARD"));
        cashPaymentContainer.setOnClickListener(v -> selectPaymentMethod("CASH"));

        doneButton.setOnClickListener(v -> {
            if (selectedPaymentMethod.equals("NONE")) {
                Toast.makeText(this, "Выберите способ оплаты", Toast.LENGTH_SHORT).show();
                return;
            }
            // Вызываем метод отправки заказа на сервер
            sendOrderToServer();
        });
    }



    private void sendOrderToServer() {
        // 1. Извлекаем данные, которые прилетели из PointFragment
        String addrB = getIntent().getStringExtra("ARG_ADDR_B");
        String addrA = getIntent().getStringExtra("ARG_ADDR_A"); // Передай его тоже через Intent
        int tariffId = getIntent().getIntExtra("TARIFF_ID", 0);
        int totalPrice = getIntent().getIntExtra("TOTAL_PRICE", 0);


        int clientID = memory.getClient().getId();



        boolean payMethod = selectedPaymentMethod.equals("CARD");


       OrderRequest request=new OrderRequest(
                clientID,
                payMethod,
                addrA,
                addrB,
                tariffId,
                totalPrice
       );

Api api = retrofitClient.getApi();
api.createOrder(request).enqueue(new Callback<Void>(){
    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if(response.isSuccessful()) {
            Toast.makeText(FormPay.this, "Заказ успешно создан!", Toast.LENGTH_SHORT).show();
            Log.d("RETROFIT", "Успех!");
            startActivity(new Intent(FormPay.this, MainActivity.class));
            finish();
        } else {
            Log.e("RETROFIT", "Ошибка сервера: " + response.code());
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
Log.e("RETROFIT", "Ошибка сети: " + t.getMessage());
Toast.makeText(FormPay.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }
});

        findViewById(R.id.doneButton).setEnabled(false);


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


        cardPaymentContainer.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        cashPaymentContainer.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
    }

    private void highlightView(CardView view) {
        // Цвет выделения (например, светло-серый или синий акцент)
        view.setCardBackgroundColor(Color.parseColor("#008000"));
        // Можно также добавить программную смену Stroke (обводки), если используете MaterialCardView
    }
}
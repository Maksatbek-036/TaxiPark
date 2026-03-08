package com.example.taxiclient;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private ImageButton buttonNavGps,buttonMain;
    FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            buttonNavGps=findViewById(R.id.btnGps);
            buttonMain=findViewById(R.id.btnMainScreen);
            buttonMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout,new Point());
                    ft.commit();

 }
});
            buttonNavGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapsForge mapsForge=new MapsForge();
                    ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout,mapsForge);
                    ft.commit();
                }

            });

            return insets;
        });
        // 👉 По умолчанию открываем MapsForgeFragment при первом запуске
        if (savedInstanceState == null) {
            MapsForge mapsForge = new MapsForge();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, mapsForge);
            ft.commit();
        }
    }
    public void onRouteSelected(String addressA, String addressB) {
        // 1. Создаем фрагмент карты
        MapsForge mapFragment = new MapsForge();

        // 2. Передаем данные через Аргументы
        Bundle args = new Bundle();
        args.putString("ARG_ADDR_A", addressA);
        args.putString("ARG_ADDR_B", addressB);
        mapFragment.setArguments(args);

        // 3. Заменяем текущий фрагмент (Point) на новый (MapsForge)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, mapFragment) // ID контейнера из вашего layout
                .addToBackStack(null) // Позволяет вернуться назад к вводу адреса
                .commit();
    }


}
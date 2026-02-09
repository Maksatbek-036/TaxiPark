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

    }

}
package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity  {
private ImageButton buttonNavProfile;
private ImageButton buttonNavMain;
PageOrders pageOrders=new PageOrders();
private ImageButton buttonNavGps;
private ImageButton buttonNavBalance;
FragmentProfile fp=new FragmentProfile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            buttonNavGps=findViewById(R.id.btnGps);
            buttonNavMain=findViewById(R.id.btnMainScreen);
            buttonNavBalance=findViewById(R.id.btnBalance);
            buttonNavProfile=findViewById(R.id.btnProfile);


            buttonNavMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNewFragment(pageOrders);
                }
            });
            buttonNavBalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PayForm payForm=new PayForm();
                    setNewFragment(payForm);
                }
            });
            buttonNavGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapsFragment mapsFragment=new MapsFragment();
                    setNewFragment(mapsFragment);
                }
            });
            buttonNavProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentProfile fragmentProfile=new FragmentProfile();
                    setNewFragment(fragmentProfile);
                }
            });







            return insets;
        });

    }



    private void setNewFragment(Fragment fragment) {
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }



}
package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

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
    FirstFragment firstFragment=new FirstFragment();
private ImageButton buttonNavGps;
private ImageButton buttonNavBalance;
private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            buttonNavGps=findViewById(R.id.btnGps);
            buttonNavBalance=findViewById(R.id.btnBalance);
            buttonNavProfile=findViewById(R.id.btnProfile);
            frameLayout=findViewById(R.id.frameLayout);



            buttonNavGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setNewFragment(firstFragment);
                }
            });
buttonNavProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        MapsFragment mapsFragment=new MapsFragment();
        setNewFragment(mapsFragment);
    }
});

            return insets;
        });

    }



    private void setNewFragment(Fragment fragment) {
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,fragment);
        ft.commit();
    }


}
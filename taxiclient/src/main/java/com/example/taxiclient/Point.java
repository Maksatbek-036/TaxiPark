package com.example.taxiclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
ate an instance of this fragment.
 */
public class Point extends Fragment {
EditText editTextPointA;
EditText editTextPointB;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_point,container,false);
editTextPointA=root.findViewById(R.id.edit_text_a);
editTextPointB=root.findViewById(R.id.edit_text_b);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_point, container, false);

    }
}
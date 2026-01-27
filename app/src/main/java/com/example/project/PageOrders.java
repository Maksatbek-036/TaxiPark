package com.example.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;


public class PageOrders extends Fragment {
private RecyclerView recyclerView;


private ArrayList<Order> orderArrayList=new ArrayList<>();



    public PageOrders() {


    }




    private void loadData() {

        orderArrayList.add(new Order("1","Ljf","fasf"));
        orderArrayList.add(new Order("2","Ljf","fasf"));
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
        View root=inflater.inflate(R.layout.fragment_page_orders,container,false);

        recyclerView=root.findViewById(R.id.orders);
        loadData();
        MyAdapter myAdapter=new MyAdapter(orderArrayList);
        recyclerView.setAdapter(myAdapter);




        return root;

    }

}
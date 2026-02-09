package com.example.taxiclient.Layouttariff;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxiclient.R;

import java.util.List;

public class TarifAdatpter extends RecyclerView.Adapter<TarifHolder> {

List<Tariff> tarifList;

    public TarifAdatpter(List<Tariff> tarifList) {
        this.tarifList = tarifList;
    }

    @NonNull
    @Override
    public TarifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TarifHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TarifHolder holder, int position) {
holder.bind(tarifList.get(position));

    }

    @Override
    public int getItemCount() {
        return tarifList.size();
    }
}

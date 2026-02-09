package com.example.taxiclient.Layouttariff;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxiclient.R;

public class TarifHolder extends RecyclerView.ViewHolder {
TextView name;

TextView price;

    public TarifHolder(@NonNull View itemView) {
        super(itemView);
name=itemView.findViewById(R.id.tariffName);
price=itemView.findViewById(R.id.tariffPrice);


    }
    public void bind(Tariff tarif){
name.setText(tarif.getName());
price.setText(tarif.getPrice()+"coм");
    }
}

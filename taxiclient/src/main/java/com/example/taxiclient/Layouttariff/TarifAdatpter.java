package com.example.taxiclient.Layouttariff;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxiclient.R;

import java.util.List;

public class TarifAdatpter extends RecyclerView.Adapter<TarifHolder> {

    List<Tariff> tarifList;
    // Переменная для хранения позиции выбранного тарифа
    private int selectedPosition = -1;

    public TarifAdatpter(List<Tariff> tarifList) {
        this.tarifList = tarifList;
    }

    @NonNull
    @Override
    public TarifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TarifHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TarifHolder holder, int position) {
        Tariff currentTariff = tarifList.get(position);

        // Передаем в Holder информацию о том, выбран ли этот элемент
        boolean isSelected = (position == selectedPosition);
        holder.bind(currentTariff);

        // Обработка клика
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Обновляем предыдущий выбранный и новый выбранный элементы
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return tarifList.size();
    }

    // Метод для получения выбранного тарифа (вызывается в Fragment)
    public Tariff getSelectedTariff() {
        if (selectedPosition != -1) {
            return tarifList.get(selectedPosition);
        }
        return null;
    }
}
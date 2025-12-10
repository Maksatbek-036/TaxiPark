package com.example.project;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView orderId;
    TextView landingPoint;
    TextView dropOfPoint;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        orderId=itemView.findViewById(R.id.order_id);
        landingPoint=itemView.findViewById(R.id.landing_point);
        dropOfPoint=itemView.findViewById(R.id.dropoff_point);

    }


    public void bind(Order order) {
        orderId.setText("Заказ "+order.getId());
        this.landingPoint.setText(order.getPointA());
        this.dropOfPoint.setText(order.getPointB());
    }
}

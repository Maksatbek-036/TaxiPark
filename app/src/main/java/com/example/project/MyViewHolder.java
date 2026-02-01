package com.example.project;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView orderId;
    TextView landingPoint;
    TextView dropOfPoint;
    Button btnAccept;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        orderId=itemView.findViewById(R .id.order_id);
        landingPoint=itemView.findViewById(R.id.landing_point);
        dropOfPoint=itemView.findViewById(R.id.dropoff_point);
        btnAccept=itemView.findViewById(R.id.btnAccept);

    }


    public void bind(Order order) {
orderId.setText("Заказ: "+order.getId());
landingPoint.setText(order.getPointA());
dropOfPoint.setText(order.getPointB());
    }
}

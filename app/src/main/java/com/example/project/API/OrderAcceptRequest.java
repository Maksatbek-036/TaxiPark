package com.example.project.API;

public class OrderAcceptRequest {
    private int driverId;
    private int orderId;

    public OrderAcceptRequest(int driverId, int orderId) {
        this.driverId = driverId;
        this.orderId = orderId;
    }
}

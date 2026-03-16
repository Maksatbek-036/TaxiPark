package com.example.taxiclient;

import java.io.Serializable;

public class Order implements Serializable {
    public int id;
    public int clientId;
    public int driverId;

    public int tariffId ;
    public String PointA;

    public boolean payMethod;
    public int totalPrice;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getTariffId() {
        return tariffId;
    }

    public void setTariffId(int tariffId) {
        this.tariffId = tariffId;
    }

    public String getPointA() {
        return PointA;
    }

    public void setPointA(String pointA) {
        PointA = pointA;
    }

    public boolean isPayMethod() {
        return payMethod;
    }

    public void setPayMethod(boolean payMethod) {
        this.payMethod = payMethod;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

 ;
}

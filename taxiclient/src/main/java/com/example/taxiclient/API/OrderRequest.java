package com.example.taxiclient.API;

public class OrderRequest {
    private int clientID;
    private String pointA;
    private String pointB;
    private boolean payMethod;
    private int tariffId;

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getPointA() {
        return pointA;
    }

    public void setPointA(String pointA) {
        this.pointA = pointA;
    }

    public String getPointB() {
        return pointB;
    }

    public void setPointB(String pointB) {
        this.pointB = pointB;
    }

    public boolean isPayMethod() {
        return payMethod;
    }

    public void setPayMethod(boolean payMethod) {
        this.payMethod = payMethod;
    }

    public int getTariffId() {
        return tariffId;
    }

    public void setTariffId(int tariffId) {
        this.tariffId = tariffId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    private int totalPrice;

    public OrderRequest(int clientID, boolean payMethod, String pointA, String pointB, int tariffId, int totalPrice) {
        this.clientID = clientID;
        this.payMethod = payMethod;
        this.pointA = pointA;
        this.pointB = pointB;
        this.tariffId = tariffId;
        this.totalPrice = totalPrice;
    }
}

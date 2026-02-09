package com.example.taxiclient.API;

public class OrderRequest {
    private int clientID;
    private String pointA;
    private String pointB;
    private boolean payMethod;
    private int tariffId;
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

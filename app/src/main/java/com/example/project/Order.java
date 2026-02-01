package com.example.project;

public class Order {

private int id;
private int clientId;
private int tariffId;

private String pointA;
private String pointB;
private int totalPrice;

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTariffId() {
        return tariffId;
    }

    public void setTariffId(int tariffId) {
        this.tariffId = tariffId;
    }

    public String getPointB() {
        return pointB;
    }

    public void setPointB(String pointB) {
        this.pointB = pointB;
    }

    public String getPointA() {
        return pointA;
    }

    public void setPointA(String pointA) {
        this.pointA = pointA;
    }

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

    public Order(int clientId, int id, String pointA, String pointB, int tariffId, int totalPrice) {
        this.clientId = clientId;
        this.id = id;
        this.pointA = pointA;
        this.pointB = pointB;
        this.tariffId = tariffId;
        this.totalPrice = totalPrice;
    }
}







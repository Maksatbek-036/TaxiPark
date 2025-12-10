package com.example.project;

public class Order {

    private String id;
    private int price;
    private boolean isStatus=false;

    public Order( String id, String pointA, String pointB) {
        this.isStatus = isStatus;
        this.id = id;
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public String getPointA() {
        return pointA;
    }

    public String getPointB() {
        return pointB;
    }

    private String pointA;
    private String pointB;

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

package com.example.taxiclient.Layouttariff;

public class Tariff {
    private int id;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return basePrice;
    }

    private String name;
    private int basePrice;

    public Tariff(String name, int price) {
        this.name = name;
        this.basePrice = price;
    }
}

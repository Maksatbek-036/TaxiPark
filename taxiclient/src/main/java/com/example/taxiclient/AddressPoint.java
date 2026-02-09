package com.example.taxiclient;

public class AddressPoint {
    public double lat, lon;
    public String name, street, houseNumber, locality;

    public AddressPoint(double lat, double lon, String name, String street, String houseNumber, String locality) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.locality = locality;
    }
}

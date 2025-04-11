package com.example.hotelreservationapp;

public class Hotel {
    private int id;
    private String name;
    private double price;
    private String location;
    private boolean availability;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return availability; }
}

package com.example.apppet.models;

public class UbicacionCollar {
    private int id;
    private int collarId;
    private double lat;
    private double lng;
    private String timestamp;

    // Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCollarId() {
        return collarId;
    }
    public void setCollarId(int collarId) {
        this.collarId = collarId;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

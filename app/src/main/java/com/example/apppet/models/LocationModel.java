package com.example.apppet.models;

public class LocationModel {
    private int id;
    private int collar_id;
    private double latitud;
    private double longitud;
    private String timestamp;

    public int getCollar_id() {
        return collar_id;
    }

    public void setCollar_id(int collar_id) {
        this.collar_id = collar_id;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

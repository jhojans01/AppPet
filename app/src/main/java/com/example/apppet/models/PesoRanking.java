package com.example.apppet.models;

import com.google.gson.annotations.SerializedName;

public class PesoRanking {
    @SerializedName("nombreMascota")
    private String nombreMascota;

    @SerializedName("cantidad")
    private int cantidad;

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}

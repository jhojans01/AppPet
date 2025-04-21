package com.example.apppet.models;

import com.google.gson.annotations.SerializedName;

public class VeterinarioCount {
    @SerializedName("nombre")
    private String nombre;

    @SerializedName("cantidad")
    private int cantidad;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}

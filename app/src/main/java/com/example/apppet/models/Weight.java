package com.example.apppet.models;

import com.google.gson.annotations.SerializedName;

public class Weight {
    private int id;

    @SerializedName("petId")
    private int petId;

    private double peso;

    @SerializedName("fecha_registro")
    private String fechaRegistro;

    @SerializedName("petName")
    private String petName;

    @SerializedName("ownerName")
    private String ownerName;

    @SerializedName("tieneDieta")
    private boolean tieneDieta;

    public Weight() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public boolean getTieneDieta() { return tieneDieta; }
    public void setTieneDieta(boolean tieneDieta) { this.tieneDieta = tieneDieta; }
}

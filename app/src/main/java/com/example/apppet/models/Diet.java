package com.example.apppet.models;

import com.google.gson.annotations.SerializedName;

public class Diet {
    private int id;

    @SerializedName("pet_id")
    private int petId;

    @SerializedName("tipo_comida")
    private String tipoComida;

    @SerializedName("cantidad")
    private String cantidad;

    @SerializedName("hora")
    private String hora;

    @SerializedName("observaciones")
    private String observaciones;

    @SerializedName("fecha_registro")
    private String fechaRegistro;

    @SerializedName("vet_id")
    private int vetId;
    @SerializedName("peso_id")
    private int pesoId;
    @SerializedName("peso_valor")
    private float pesoValor; // este es opcional para mostrar el número exacto en kg

    // Constructor vacío
    public Diet() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getPetId() {
        return petId;
    }
    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getTipoComida() {
        return tipoComida;
    }
    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    public String getCantidad() {
        return cantidad;
    }
    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getVetId() {
        return vetId;
    }
    public void setVetId(int vetId) {
        this.vetId = vetId;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    public int getPesoId() {
        return pesoId;
    }
    public void setPesoId(int pesoId) {
        this.pesoId = pesoId;
    }
    public float getPesoValor() {
        return pesoValor;
    }

    public void setPesoValor(float pesoValor) {
        this.pesoValor = pesoValor;
    }
}

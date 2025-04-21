package com.example.apppet.models;

public class Behavior {
    private int id;
    private int petId;
    private String comportamiento;  // p.ej. "Tranquila", "Ansiosa", etc.
    private String fechaHora;       // p.ej. "2025-03-24 10:30"

    public Behavior() {
    }

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

    public String getComportamiento() {
        return comportamiento;
    }
    public void setComportamiento(String comportamiento) {
        this.comportamiento = comportamiento;
    }

    public String getFechaHora() {
        return fechaHora;
    }
    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}

package com.example.apppet.models;

public class Collar {
    private int id;              // <- ID del collar (clave primaria en la BD)
    private int petId;           // <- ID de la mascota asociada
    private String identificador;

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

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}

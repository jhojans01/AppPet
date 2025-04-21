package com.example.apppet.models;

public class Vaccine {
    private int id;
    private int petId;
    private String nombreVacuna;
    private String fechaAplicacion;
    private String estadoVacuna; // NUEVO: "Falta" o "Aplicada"

    public Vaccine() {}

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

    public String getNombreVacuna() {
        return nombreVacuna;
    }
    public void setNombreVacuna(String nombreVacuna) {
        this.nombreVacuna = nombreVacuna;
    }

    public String getFechaAplicacion() {
        return fechaAplicacion;
    }
    public void setFechaAplicacion(String fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getEstadoVacuna() {
        return estadoVacuna;
    }
    public void setEstadoVacuna(String estadoVacuna) {
        this.estadoVacuna = estadoVacuna;
    }
}


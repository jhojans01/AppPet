package com.example.apppet.models;

public class Visit {
    private int id;
    private int petId;
    private String fecha;         // Por ejemplo: "2025-03-24" o "2025-03-24 14:30:00"
    private String motivo;        // Ejemplo: "Consulta rutinaria"
    private String observaciones; // Ejemplo: "El veterinario recomendó..."

    public Visit() {
    }

    public Visit(int id, int petId, String fecha, String motivo, String observaciones) {
        this.id = id;
        this.petId = petId;
        this.fecha = fecha;
        this.motivo = motivo;
        this.observaciones = observaciones;
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

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}

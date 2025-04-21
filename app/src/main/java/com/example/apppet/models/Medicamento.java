package com.example.apppet.models;

public class Medicamento {
    private int id;
    private int petId;
    private String enfermedad;
    private String medicamento;
    private String via;
    private String hora;
    private String observaciones;
    public Medicamento(int id, int petId, String enfermedad, String medicamento, String via, String hora, String observaciones) {
        this.id = id;
        this.petId = petId;
        this.enfermedad = enfermedad;
        this.medicamento = medicamento;
        this.via = via;
        this.hora = hora;
        this.observaciones = observaciones;
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

    public String getEnfermedad() {
        return enfermedad;
    }
    public void setEnfermedad(String enfermedad) {
        this.enfermedad = enfermedad;
    }

    public String getMedicamento() {
        return medicamento;
    }
    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getVia() {
        return via;
    }
    public void setVia(String via) {
        this.via = via;
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
}

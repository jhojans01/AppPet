package com.example.apppet.models;

public class Actividad {
    private int mascota_id;
    private String tipo_actividad;
    private int duracion;
    private String fecha;

    public Actividad(int mascota_id, String tipo_actividad, int duracion, String fecha) {
        this.mascota_id = mascota_id;
        this.tipo_actividad = tipo_actividad;
        this.duracion = duracion;
        this.fecha = fecha;
    }

    public int getMascota_id() {
        return mascota_id;
    }

    public void setMascota_id(int mascota_id) {
        this.mascota_id = mascota_id;
    }

    public String getTipo_actividad() {
        return tipo_actividad;
    }

    public void setTipo_actividad(String tipo_actividad) {
        this.tipo_actividad = tipo_actividad;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

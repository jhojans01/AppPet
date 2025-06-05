package com.example.apppet.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Pet implements Serializable {
    private int id;

    @SerializedName("user_id")
    private int userId;

    private String nombre;
    private String especie;
    private String raza;
    private int edad;

    @SerializedName("image_base64")
    private String imageBase64;

    @SerializedName("vet_id")
    private int vetId;

    @SerializedName("cuidador_id")
    private int cuidadorId;
    @SerializedName("activo")
    private int activo;
    @SerializedName("estado_asignacion")
    private String estado_asignacion;

    private int pendiente;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public int getVetId() { return vetId; }
    public void setVetId(int vetId) { this.vetId = vetId; }

    public int getCuidadorId() { return cuidadorId; }
    public void setCuidadorId(int cuidadorId) { this.cuidadorId = cuidadorId; }

    public int getPendiente() { return pendiente; }
    public void setPendiente(int pendiente) { this.pendiente = pendiente; }
    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }
    public String getEstado_asignacion() {
        return estado_asignacion;
    }

    public void setEstado_asignacion(String estado_asignacion) {
        this.estado_asignacion = estado_asignacion;
    }
}

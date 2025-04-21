package com.example.apppet.network;

import com.example.apppet.models.Pet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface MascotaService {

    // ✅ Obtener todas las mascotas
    @GET("mascotas.php")
    Call<List<Pet>> getAllPets();

    // ✅ Obtener mascotas por usuario (propietario)
    @GET("mascotas.php")
    Call<List<Pet>> getPetsByUser(@Query("user_id") int userId);

    // ✅ Obtener mascotas por veterinario
    @GET("mascotas.php")
    Call<List<Pet>> getPetsByVet(@Query("vet_id") int vetId);

    // ✅ Obtener mascotas por cuidador
    @GET("mascotas.php")
    Call<List<Pet>> getPetsByCuidador(@Query("cuidador_id") int cuidadorId);

    // ✅ Obtener una sola mascota por ID (para detalle y edición)
    @GET("mascotas.php")
    Call<Pet> getPetById(@Query("id") int id);

    // ✅ Insertar nueva mascota
    @POST("mascotas.php")
    Call<Void> insertPet(@Body Pet pet);

    // ✅ Actualizar mascota (incluye cambios de vet/cuidador)
    @PUT("mascotas.php")  // antes tenías "updatePet.php", ya no es necesario si todo va en mascotas.php
    Call<Void> updatePet(@Body Pet pet);

    // ✅ Eliminar mascota
    @HTTP(method = "DELETE", path = "deletePet.php", hasBody = true)
    Call<Void> deletePet(@Body Pet pet);
}


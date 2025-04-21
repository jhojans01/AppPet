package com.example.apppet.network;

import com.example.apppet.models.Weight;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface WeightService {

    // Obtener lista de pesos por mascota
    @GET("pesos.php")
    Call<List<Weight>> getPesosByPet(@Query("pet_id") int petId);

    // Obtener todos los pesos (opcional)
    @GET("pesos.php")
    Call<List<Weight>> getAllPesos();

    // Insertar un nuevo registro de peso
    @POST("pesos.php")
    Call<Void> insertPeso(@Body Weight weight);

    // Actualizar un registro de peso existente
    @PUT("updatePeso.php")
    Call<Void> updatePeso(@Body Weight weight);

    // Eliminar un registro de peso
    @HTTP(method = "DELETE", path = "deletePeso.php", hasBody = true)
    Call<Void> deletePeso(@Body Weight weight);

    @GET("getPesosConInfoByVet.php")
    Call<List<Weight>> getPesosByVet(@Query("vet_id") int vetId);

}

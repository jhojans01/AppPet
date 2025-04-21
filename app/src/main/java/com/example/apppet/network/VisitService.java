package com.example.apppet.network;

import com.example.apppet.models.Visit;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface VisitService {

    // Obtener las visitas para una mascota (filtrado por pet_id)
    @GET("visitas.php")
    Call<List<Visit>> getVisitsByPet(@Query("pet_id") int petId);

    // Insertar una nueva visita
    @POST("visitas.php")
    Call<Void> insertVisit(@Body Visit visit);

    // Actualizar una visita existente
    @PUT("updateVisita.php")
    Call<Void> updateVisit(@Body Visit visit);

    // Eliminar una visita (usamos @HTTP para DELETE con cuerpo)
    @HTTP(method = "DELETE", path = "deleteVisita.php", hasBody = true)
    Call<Void> deleteVisit(@Body Visit visit);
}

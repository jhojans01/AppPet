package com.example.apppet.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ActividadService {

    @FormUrlEncoded
    @POST("insert_actividad_fisica.php")
    Call<Void> insertarActividad(
            @Field("mascota_id") int mascotaId,
            @Field("tipo_actividad") String tipoActividad,
            @Field("duracion") String duracion,
            @Field("fecha") String fecha
    );
}

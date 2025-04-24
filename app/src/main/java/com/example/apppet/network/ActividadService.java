package com.example.apppet.network;

import com.example.apppet.models.Actividad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ActividadService {

    @FormUrlEncoded
    @POST("insert_actividad_fisica.php")
    Call<Void> insertarActividad(
            @Field("mascota_id") int mascotaId,
            @Field("tipo_actividad") String tipoActividad,
            @Field("duracion") String duracion,
            @Field("fecha") String fecha
    );

    @GET("get_actividad_fisica.php")
    Call<List<Actividad>> getActividades(@Query("mascota_id") int mascotaId);
    @GET("get_ultima_actividad.php")
    Call<Actividad> getUltimaActividad(@Query("mascota_id") int mascotaId);
}

package com.example.apppet.network;

import com.example.apppet.models.Collar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CollarService {

    @POST("insertarcollar.php")
    Call<Void> insertarCollar(@Body Collar collar);



    @GET("obtener_collar_por_mascota.php")
    Call<Collar> obtenerCollarPorMascota(@Query("pet_id") int petId);



}

package com.example.apppet.network;

import com.example.apppet.models.Diet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DietService {

    @POST("insertDiet.php")
    Call<Void> insertarDieta(@Body Diet dieta);
    @GET("getAllDietasByPetId.php")
    Call<List<Diet>> getDietasByPetId(@Query("pet_id") int petId);

}

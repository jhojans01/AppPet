package com.example.apppet.network;

import com.example.apppet.models.Behavior;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface BehaviorService {

    @GET("comportamientos.php")
    Call<List<Behavior>> getComportamientosByPet(@Query("pet_id") int petId);

    @POST("comportamientos.php")
    Call<Void> insertBehavior(@Body Behavior behavior);

    @PUT("updateComportamiento.php")
    Call<Void> updateBehavior(@Body Behavior behavior);

    @HTTP(method = "DELETE", path = "deleteComportamiento.php", hasBody = true)
    Call<Void> deleteBehavior(@Body Behavior behavior);
}

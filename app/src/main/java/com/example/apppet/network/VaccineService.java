package com.example.apppet.network;

import com.example.apppet.models.Vaccine;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface VaccineService {

    @GET("vacunas.php")
    Call<List<Vaccine>> getVacunasByPet(@Query("pet_id") int petId);

    @POST("vacunas.php")
    Call<Void> insertVaccine(@Body Vaccine vaccine);

    @PUT("updateVacuna.php")
    Call<Void> updateVaccine(@Body Vaccine vaccine);

    @HTTP(method = "DELETE", path = "deleteVacuna.php", hasBody = true)
    Call<Void> deleteVaccine(@Body Vaccine vaccine);
}

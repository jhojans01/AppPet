package com.example.apppet.network;

import com.example.apppet.models.Medicamento;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MedicamentoService {
    @POST("insertarMedicamento.php")
    Call<Void> insertarMedicamento(@Body Medicamento medicamento);

    @GET("getMedicamentosByPetId.php")
    Call<List<Medicamento>> getMedicamentosByPetId(@Query("pet_id") int petId); // ✅




}


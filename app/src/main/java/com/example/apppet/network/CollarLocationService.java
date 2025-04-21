package com.example.apppet.network;

import com.example.apppet.models.LocationModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CollarLocationService {

    @GET("get_location.php")
    Call<List<LocationModel>> getUbicacionesCollar(@Query("collar_id") int collarId);
}

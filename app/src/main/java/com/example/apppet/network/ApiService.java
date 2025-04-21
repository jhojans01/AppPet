package com.example.apppet.network;

import com.example.apppet.models.LocationModel;
import com.example.apppet.models.Usuario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // GUARDAR ubicación manual o automática
    @POST("guardar_ubicacion.php")
    Call<Void> guardarUbicacion(@Body LocationModel ubicacion);

    // NUEVO ENDPOINT para guardar la ubicación del collar con timestamp generado en el servidor
    @POST("insertar_ubicacion_collar.php")
    Call<Void> insertarUbicacionCollar(@Body LocationModel ubicacion);


    // OBTENER última ubicación del collar
    @GET("obtener_ubicacion_por_collar.php")
    Call<LocationModel> obtenerUbicacionPorCollar(@Query("collar_id") int collarId);

    // OBTENER historial completo de ubicaciones del collar
    @GET("obtener_ruta_collar.php")
    Call<List<LocationModel>> obtenerRutaCollar(@Query("collar_id") int collarId);

    // RUTA desde usuario actual hasta la ubicación del collar
    @GET("directions/json")
    Call<ResponseBody> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String apiKey
    );
    @GET("get_cuidadores.php") // Ajusta el nombre según cómo lo hayas llamado
    Call<List<Usuario>> getCuidadores();

}

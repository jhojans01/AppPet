package com.example.apppet.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2/petapp/"; // Tu URL base para la app
    private static Retrofit retrofit;

    private static final String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // 🔥 Este es el que te falta: para llamar a la API de Google Directions
    public static Retrofit getDirectionsInstance() {
        return new Retrofit.Builder()
                .baseUrl(DIRECTIONS_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }
}

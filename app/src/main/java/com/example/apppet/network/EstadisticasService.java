package com.example.apppet.network;

import com.example.apppet.models.PesoRanking;
import com.example.apppet.models.EspecieCount;
import com.example.apppet.models.UserRoleCount;
import com.example.apppet.models.VeterinarioCount;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EstadisticasService {
    @GET("contarUsuariosPorRol.php")
    Call<List<UserRoleCount>> getCantidadUsuariosPorRol();
    @GET("contarMascotasPorEspecie.php")
    Call<List<EspecieCount>> contarMascotasPorEspecie();

    @GET("top_pesos.php")
    Call<List<PesoRanking>> getTopMascotasPeso();
    @GET("getTopVeterinarios.php")
    Call<List<VeterinarioCount>> getTopVeterinarios();

}

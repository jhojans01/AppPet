package com.example.apppet.network;

import com.example.apppet.models.EmailCheckResponse;
import com.example.apppet.models.Usuario;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UsuarioService {

    @GET("usuarios.php")
    Call<List<Usuario>> getAllUsers();

    @POST("usuarios.php")
    Call<Void> insertUser(@Body Usuario usuario);

    @POST("updateUser.php")
    Call<Void> updateUser(@Body Usuario usuario);

    @POST("update_perfil_usuario.php")
    Call<Void> updatePerfil(@Body Usuario usuario);

    @HTTP(method = "DELETE", path = "deleteUser.php", hasBody = true)
    Call<Void> deleteUser(@Body Usuario usuario);

    @POST("login.php")
    Call<Usuario> loginUser(@Body Usuario usuario);

    @GET("checkEmail.php")
    Call<EmailCheckResponse> checkEmail(@Query("email") String email);

    @GET("getUser.php")
    Call<Usuario> getUserById(@Query("user_id") int userId);

    @GET("get_veterinarios.php")
    Call<List<Usuario>> getVeterinarios();

    @GET("get_cuidadores.php")
    Call<List<Usuario>> getCuidadores();
}


package com.example.apppet.network;

import com.example.apppet.models.Conversation;
import com.example.apppet.models.Message;
import com.example.apppet.models.MessageCheckResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatService {

    // Método para enviar un mensaje al endpoint guardarMensaje.php
    @POST("guardarMensaje.php")
    Call<Void> sendMessage(@Body Message message);

    // Método para obtener el historial de mensajes entre dos usuarios
    @GET("obtenerMensajes.php")
    Call<List<Message>> getMessages(@Query("sender_id") int senderId,
                                    @Query("receiver_id") int receiverId);
    @GET("obtenerConversaciones.php")
    Call<List<Conversation>> getConversations(@Query("vet_id") int vetId);

    @GET("obtenerConversacionesCuidador.php")
    Call<List<Conversation>> getConversationsCuidador(@Query("cuidador_id") int cuidadorId);

    @GET("obtenerMensajesRecientes.php")
    Call<List<Message>> getLatestMessages(@Query("receiver_id") int receiverId);
    @GET("verificarMensajesNuevos.php")
    Call<Integer> verificarMensajesNuevos(@Query("receiver_id") int receiverId);
    @GET("marcarMensajesComoLeidos.php")
    Call<Void> marcarMensajesComoLeidos(@Query("receiver_id") int receiverId);





}

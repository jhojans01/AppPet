package com.example.apppet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.ChatListAdapter;
import com.example.apppet.models.Conversation;
import com.example.apppet.network.ChatService;
import com.example.apppet.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListCuidadorActivity extends AppCompatActivity {

    private RecyclerView rvChatList;
    private ChatListAdapter chatListAdapter;
    private List<Conversation> conversationList = new ArrayList<>();

    private int cuidadorId; // ID del cuidador logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list); // ¡Mismo layout que ya tienes!

        // Obtenemos el ID del cuidador desde el Intent
        cuidadorId = getIntent().getIntExtra("loggedUserId", 0);

        rvChatList = findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));

        // Usamos el mismo adaptador de conversación
        chatListAdapter = new ChatListAdapter(conversationList, conversation -> {
            // Cuando seleccione una conversación, abre ChatActivity
            Intent intent = new Intent(ChatListCuidadorActivity.this, ChatActivity.class);
            intent.putExtra("loggedUserId", cuidadorId);  // El cuidador es el logueado
            intent.putExtra("otherUserId", conversation.getOwnerId()); // El propietario es el otro
            startActivity(intent);
        });
        rvChatList.setAdapter(chatListAdapter);

        loadConversations();
    }

    private void loadConversations() {
        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);

        Call<List<Conversation>> call = chatService.getConversationsCuidador(cuidadorId); // ⚡ Cambiado aquí

        call.enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    conversationList.clear();
                    conversationList.addAll(response.body());
                    chatListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChatListCuidadorActivity.this, "Error al cargar conversaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                Toast.makeText(ChatListCuidadorActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

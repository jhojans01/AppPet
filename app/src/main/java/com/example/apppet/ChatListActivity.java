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

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView rvChatList;
    private ChatListAdapter chatListAdapter;
    private List<Conversation> conversationList = new ArrayList<>();

    // El ID del veterinario logueado (pasado en el Intent)
    private int vetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        vetId = getIntent().getIntExtra("loggedUserId", 0);

        rvChatList = findViewById(R.id.rvChatList);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));
        chatListAdapter = new ChatListAdapter(conversationList, conversation -> {
            // Al seleccionar una conversación, abrir ChatActivity para esa conversación individual
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("loggedUserId", vetId); // Veterinario
            intent.putExtra("otherUserId", conversation.getOwnerId()); // ID del propietario de esta conversación
            startActivity(intent);
        });
        rvChatList.setAdapter(chatListAdapter);

        loadConversations();
    }

    private void loadConversations() {
        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);
        Call<List<Conversation>> call = chatService.getConversations(vetId);
        call.enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    conversationList.clear();
                    conversationList.addAll(response.body());
                    chatListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChatListActivity.this, "Error al cargar conversaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                Toast.makeText(ChatListActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}


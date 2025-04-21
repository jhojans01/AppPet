package com.example.apppet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apppet.adapters.ChatAdapter;
import com.example.apppet.models.Message;
import com.example.apppet.network.ChatService;
import com.example.apppet.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private Button btnSend, btnRefresh;
    private ChatAdapter chatAdapter;
    private List<Message> messages = new ArrayList<>();

    // Los IDs para la conversación individual:
    // loggedUserId: el que abrió la Activity (puede ser propietario o veterinario)
    // otherUserId: el otro participante
    private int loggedUserId;
    private int otherUserId;

    // Constantes para notificaciones locales
    private static final int NOTIFICATION_ID = 2001;
    private static final String CHANNEL_ID = "CHAT_CHANNEL";

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnRefresh = findViewById(R.id.btnRefresh);

        // Recuperar los IDs del Intent
        loggedUserId = getIntent().getIntExtra("loggedUserId", 0);
        otherUserId = getIntent().getIntExtra("otherUserId", 0);
        Log.d("ChatActivity", "loggedUserId: " + loggedUserId + ", otherUserId: " + otherUserId);

        chatAdapter = new ChatAdapter(messages, loggedUserId);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = etMessage.getText().toString().trim();
                if (msgText.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(msgText);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMessages();
            }
        });

        loadMessages();
    }

    private void sendMessage(String messageText) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Message message = new Message(loggedUserId, otherUserId, messageText, timestamp);

        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);
        Call<Void> call = chatService.sendMessage(message);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    etMessage.setText(""); // limpia el input
                    loadMessages(); // recarga el chat
                } else {
                    Toast.makeText(ChatActivity.this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);
        Call<List<Message>> call = chatService.getMessages(loggedUserId, otherUserId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messages = response.body();
                    chatAdapter.setMessages(messages); // <- Esta línea ACTUALIZA el adaptador
                    rvChat.scrollToPosition(messages.size() - 1); // <- Baja al final automáticamente
                } else {
                    Toast.makeText(ChatActivity.this, "No se pudieron cargar mensajes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void showChatNotification(String title, String messageText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.chat)
                .setContentTitle(title)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}



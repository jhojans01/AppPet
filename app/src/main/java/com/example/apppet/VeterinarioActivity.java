package com.example.apppet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.PetAdapterForVet;
import com.example.apppet.models.Pet;
import com.example.apppet.network.ChatService;
import com.example.apppet.network.MascotaService;
import com.example.apppet.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VeterinarioActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVet;
    private PetAdapterForVet adapter;
    private List<Pet> petList = new ArrayList<>();
    private MascotaService mascotaService;
    private int vetId;

    private FloatingActionButton fabChatVet;
    private Handler pollingHandler = new Handler();
    private Runnable pollingRunnable;
    private static final int POLLING_INTERVAL_MS = 8000; // Cada 8 segundos

    private static final int NOTIFICATION_ID = 3001;
    private static final String CHANNEL_ID = "CHAT_VET_CHANNEL";

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veterinario);

        // Recuperar ID del veterinario
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        vetId = prefs.getInt("user_id", 0);

        recyclerViewVet = findViewById(R.id.recyclerViewVet);
        recyclerViewVet.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PetAdapterForVet(petList, this::abrirDetalleMascota);
        recyclerViewVet.setAdapter(adapter);

        mascotaService = RetrofitClient.getRetrofitInstance().create(MascotaService.class);
        obtenerMascotasDelVeterinario();

        fabChatVet = findViewById(R.id.fabChatVet);
        fabChatVet.setOnClickListener(v -> {
            Intent intent = new Intent(VeterinarioActivity.this, ChatListActivity.class);
            intent.putExtra("loggedUserId", vetId);
            startActivity(intent);
        });

        crearCanalNotificaciones();
        startPollingForMessages();
    }

    private void obtenerMascotasDelVeterinario() {
        mascotaService.getPetsByVet(vetId).enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    petList.clear();
                    petList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VeterinarioActivity.this, "No se pudieron cargar las mascotas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {
                Toast.makeText(VeterinarioActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDetalleMascota(Pet pet) {
        Intent intent = new Intent(this, DetalleMascotaVetActivity.class);
        intent.putExtra("nombre", pet.getNombre());
        intent.putExtra("especie", pet.getEspecie());
        intent.putExtra("raza", pet.getRaza());
        intent.putExtra("edad", pet.getEdad());
        intent.putExtra("imagenBase64", pet.getImageBase64());
        intent.putExtra("pet_id", pet.getId());
        startActivity(intent);
    }

    private void startPollingForMessages() {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewMessages();
                pollingHandler.postDelayed(this, POLLING_INTERVAL_MS);
            }
        };
        pollingHandler.post(pollingRunnable);
    }

    private void checkForNewMessages() {
        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);
        Call<Integer> call = chatService.verificarMensajesNuevos(vetId);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int mensajesNuevos = response.body();
                    if (mensajesNuevos > 0) {
                        showNewMessageNotification("Nuevo mensaje", "Tienes " + mensajesNuevos + " mensajes nuevos");
                        marcarMensajesComoLeidos();
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                // Nada, ignoramos fallos
            }
        });
    }

    private void marcarMensajesComoLeidos() {
        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);
        Call<Void> call = chatService.marcarMensajesComoLeidos(vetId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // OK
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error al marcar como leídos
            }
        });
    }

    private void showNewMessageNotification(String title, String messageText) {
        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra("loggedUserId", vetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.chat)
                .setContentTitle(title)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Chat Veterinario",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de nuevos mensajes para veterinario");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pollingHandler.removeCallbacks(pollingRunnable);
    }
}

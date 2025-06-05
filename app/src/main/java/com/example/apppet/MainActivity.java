package com.example.apppet;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.PetAdapter;
import com.example.apppet.laboratorio.TopLevelActivity;
import com.example.apppet.models.Message;
import com.example.apppet.models.MessageCheckResponse;
import com.example.apppet.models.Pet;
import com.example.apppet.network.ChatService;
import com.example.apppet.network.MascotaService;
import com.example.apppet.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 1001;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1002;

    private Button btnProfile;
    private FloatingActionButton fabAddPet, fabChat;
    private FloatingActionButton fabMascotasPendientes;
    private RecyclerView recyclerViewPets;
    private PetAdapter petAdapter;
    private List<Pet> petList = new ArrayList<>();

    private MascotaService mascotaService;
    private SharedPreferences prefs;
    private String userRole;
    private int userId;

    private String encodedImage = null;
    private Handler pollingHandler = new Handler();
    private Runnable pollingRunnable;
    private static final int POLLING_INTERVAL = 8000; // 8 segundos

    private int loggedUserId;  // Ya debes tenerlo al loguearte
    private String loggedUserRole; // Ya debes tenerlo también (propietario, veterinario, etc.)
    private int lastMessageId = -1; // ID del último mensaje notificado
    private int ultimoMensajeNotificadoId = -1; // <- Guardamos el último mensaje que notificamos

    private int mensajesPendientes = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crearCanalNotificaciones();
        pedirPermisoNotificaciones();
        Log.d("MainActivity", "userId: " + userId + ", userRole: " + userRole);

        prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        userRole = prefs.getString("role", "");
        userId = prefs.getInt("user_id", 0);
        Log.d("MainActivity", "userId: " + userId + ", userRole: " + userRole);

        if ("propietario".equalsIgnoreCase(userRole) || "cuidador".equalsIgnoreCase(userRole)) {
            startPollingForMessages();
        }


        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setVisibility(userRole.equalsIgnoreCase("propietario") ? View.VISIBLE : View.GONE);
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        FloatingActionButton fabLaboratorio = findViewById(R.id.fabLaboratorio);
        fabLaboratorio.setVisibility(View.VISIBLE);
        fabLaboratorio.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TopLevelActivity.class);
            startActivity(intent);
        });

        mascotaService = RetrofitClient.getRetrofitInstance().create(MascotaService.class);

        recyclerViewPets = findViewById(R.id.recyclerViewPets);
        recyclerViewPets.setLayoutManager(new LinearLayoutManager(this));

        petAdapter = new PetAdapter(petList, userRole, new PetAdapter.OnPetActionListener() {
            @Override
            public void onEditPet(Pet pet) {
                editarMascota(pet);
            }

            @Override
            public void onDeletePet(Pet pet) {
                confirmarEliminacionPet(pet);
            }

            @Override
            public void onViewPetDetail(Pet pet) {
                if (userRole.equalsIgnoreCase("cuidador")) {
                    Intent intent = new Intent(MainActivity.this, CuidadoMascotaActivity.class);
                    intent.putExtra("pet", pet);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, PetDetailActivity.class);
                    intent.putExtra("pet_id", pet.getId());
                    startActivity(intent);
                }
            }

        });
        recyclerViewPets.setAdapter(petAdapter);

        fabAddPet = findViewById(R.id.fabAddPet);
        if (userRole.equalsIgnoreCase("propietario")) {
            fabAddPet.setVisibility(View.VISIBLE);
            fabAddPet.setOnClickListener(v -> mostrarDialogoAgregarMascota());
            cargarMascotasPropietario(userId);
        } else {
            fabAddPet.setVisibility(View.GONE);
            cargarMascotasDelCuidador(userId);
        }

        fabChat = findViewById(R.id.fabChat);
        if (userRole.equalsIgnoreCase("cuidador")) {
            fabChat.setVisibility(View.VISIBLE);
            fabChat.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChatListCuidadorActivity.class);
                intent.putExtra("loggedUserId", userId);
                startActivity(intent);
            });
        } else {
            fabChat.setVisibility(View.GONE); // Propietario y otros roles no lo ven
        }
        fabMascotasPendientes = findViewById(R.id.fabMascotasPendientes);
        if (userRole.equalsIgnoreCase("cuidador")) {
            fabMascotasPendientes.setVisibility(View.VISIBLE);
            fabMascotasPendientes.setOnClickListener(v -> {
                Intent intent = new Intent(this, MascotasPendientesActivity.class);
                intent.putExtra("cuidador_id", userId);
                startActivity(intent);
            });
        } else {
            fabMascotasPendientes.setVisibility(View.GONE);
        }

    }
    private void startPollingForMessages() {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewMessages();
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        };
        pollingHandler.post(pollingRunnable);
    }

    private void checkForNewMessages() {
        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);
        Call<Integer> call = chatService.verificarMensajesNuevos(userId);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int mensajesNuevos = response.body();
                    Log.d("Polling", "Mensajes nuevos: " + mensajesNuevos);

                    if (mensajesNuevos > 0) {
                        if ("propietario".equalsIgnoreCase(userRole)) {
                            showNewMessageNotification("Nuevo mensaje de tu veterinario", "Tienes " + mensajesNuevos + " mensajes nuevos");
                        } else if ("cuidador".equalsIgnoreCase(userRole)) {
                            showNewMessageNotification("Nuevo mensaje de tu propietario", "Tienes " + mensajesNuevos + " mensajes nuevos");
                        }
                        marcarMensajesComoLeidos();
                    }
                } else {
                    Log.e("Polling", "Error en la respuesta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("Polling", "Fallo: " + t.getMessage());
            }
        });
    }


    private void marcarMensajesComoLeidos() {
        ChatService chatService = RetrofitClient.getRetrofitInstance().create(ChatService.class);
        Call<Void> call = chatService.marcarMensajesComoLeidos(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Polling", "Mensajes marcados como leídos");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Polling", "Error al marcar como leídos: " + t.getMessage());
            }
        });
    }






    // Para casos simples (sin abrir chat)
    private void showNewMessageNotification(String title, String body) {
        showNewMessageNotification(title, body, -1);
    }

    // Para casos donde sí sabemos el senderId
    private void showNewMessageNotification(String senderName, String messageText, int senderId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "medicamento_channel")
                .setSmallIcon(R.drawable.chat)
                .setContentTitle(senderName)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent intent;
        if (senderId != -1) {
            // Si tenemos senderId válido, abrimos el chat directamente
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("otherUserId", senderId);
        } else {
            // Si no, solo abre MainActivity
            intent = new Intent(this, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(2001, builder.build());
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPollingForMessages();
    }

    private void stopPollingForMessages() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }




    private void crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "medicamento_channel",
                    "Alertas de Medicamentos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "✅ Permiso de notificación concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "❌ No se podrán mostrar notificaciones", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void cargarMascotasPropietario(int userId) {
        mascotaService.getPetsByUser(userId).enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    petList.clear();
                    petList.addAll(response.body());
                    petAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener mascotas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoAgregarMascota() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_pet, null);
        TextInputEditText etNombre = view.findViewById(R.id.etPetNombre);
        TextInputEditText etEspecie = view.findViewById(R.id.etEspecie);
        TextInputEditText etRaza = view.findViewById(R.id.etRaza);
        TextInputEditText etEdad = view.findViewById(R.id.etEdad);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);

        btnSelectImage.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, REQUEST_PICK_IMAGE);
        });

        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(view)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    Pet nueva = new Pet();
                    nueva.setUserId(userId);
                    nueva.setNombre(etNombre.getText().toString().trim());
                    nueva.setEspecie(etEspecie.getText().toString().trim());
                    nueva.setRaza(etRaza.getText().toString().trim());
                    nueva.setEdad(Integer.parseInt(etEdad.getText().toString().trim()));
                    nueva.setImageBase64(encodedImage);
                    nueva.setVetId(-1);
                    nueva.setCuidadorId(-1);
                    //nueva.setCuidadorId(39);

                    mascotaService.insertPet(nueva).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Mascota agregada", Toast.LENGTH_SHORT).show();
                                cargarMascotasPropietario(userId);
                            } else {
                                Toast.makeText(MainActivity.this, "Error al agregar mascota", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    encodedImage = null;
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void editarMascota(Pet pet) {
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_pet, null);
        TextInputEditText etNombre = view.findViewById(R.id.etEditPetName);
        TextInputEditText etEspecie = view.findViewById(R.id.etEditSpecies);
        TextInputEditText etRaza = view.findViewById(R.id.etEditBreed);
        TextInputEditText etEdad = view.findViewById(R.id.etEditAge);

        etNombre.setText(pet.getNombre());
        etEspecie.setText(pet.getEspecie());
        etRaza.setText(pet.getRaza());
        etEdad.setText(String.valueOf(pet.getEdad()));

        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    pet.setNombre(etNombre.getText().toString().trim());
                    pet.setEspecie(etEspecie.getText().toString().trim());
                    pet.setRaza(etRaza.getText().toString().trim());
                    pet.setEdad(Integer.parseInt(etEdad.getText().toString().trim()));

                    mascotaService.updatePet(pet).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(MainActivity.this, "Mascota actualizada", Toast.LENGTH_SHORT).show();
                            cargarMascotasPropietario(userId);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminacionPet(Pet pet) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Mascota")
                .setMessage("¿Seguro de eliminar a " + pet.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarMascota(pet))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarMascota(Pet pet) {
        mascotaService.deletePet(pet).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this, "Mascota eliminada", Toast.LENGTH_SHORT).show();
                cargarMascotasPropietario(userId);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMascotasDelCuidador(int cuidadorId) {
        mascotaService.getMascotasAceptadasCuidador(cuidadorId, "aceptada").enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    petList.clear();
                    petList.addAll(response.body());
                    petAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Error al obtener mascotas del cuidador", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                Toast.makeText(this, "Imagen cargada", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if ("cuidador".equalsIgnoreCase(userRole)) {
            cargarMascotasDelCuidador(userId); // 🔄 Recarga la lista al volver
        }
    }

}
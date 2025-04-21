package com.example.apppet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.PetAdapterForVet;
import com.example.apppet.models.Pet;
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

    private FloatingActionButton fabChatVet, fabDiet;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veterinario);

        // Recuperar ID del veterinario
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        vetId = prefs.getInt("user_id", 0);

        // Inicializar RecyclerView
        recyclerViewVet = findViewById(R.id.recyclerViewVet);
        recyclerViewVet.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PetAdapterForVet(petList, this::abrirDetalleMascota);
        recyclerViewVet.setAdapter(adapter);

        // Inicializar Retrofit
        mascotaService = RetrofitClient.getRetrofitInstance().create(MascotaService.class);

        // Obtener mascotas asociadas al veterinario
        obtenerMascotasDelVeterinario();

        // Botones flotantes
        fabChatVet = findViewById(R.id.fabChatVet);

        fabChatVet.setOnClickListener(v -> {
            Intent intent = new Intent(VeterinarioActivity.this, ChatListActivity.class);
            intent.putExtra("loggedUserId", vetId);
            startActivity(intent);
        });

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
        intent.putExtra("pet_id", pet.getId()); // ✅ AGREGA ESTO
        startActivity(intent);
    }



}

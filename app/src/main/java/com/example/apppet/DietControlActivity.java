package com.example.apppet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.DietAdapter;
import com.example.apppet.adapters.WeightAdapterDiet;
import com.example.apppet.models.Diet;
import com.example.apppet.models.Weight;
import com.example.apppet.network.DietService;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.WeightService;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DietControlActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WeightAdapterDiet adapter;
    private List<Weight> weightList = new ArrayList<>();

    private WeightService weightService;
    private DietService dietService;
    private int vetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_control);

        recyclerView = findViewById(R.id.recyclerViewDiet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        weightService = RetrofitClient.getRetrofitInstance().create(WeightService.class);
        dietService = RetrofitClient.getRetrofitInstance().create(DietService.class);

        vetId = getIntent().getIntExtra("loggedUserId", 0);

        adapter = new WeightAdapterDiet(weightList, new WeightAdapterDiet.OnWeightItemClickListener() {
            @Override
            public void onAssignDietClick(Weight weight) {
                if (weight.getTieneDieta()) {
                    mostrarDietaAsignada(weight.getPetId());
                } else {
                    mostrarDialogoAsignarDieta(weight);
                }
            }
        });

        recyclerView.setAdapter(adapter);
        cargarPesosDelVet();
    }

    private void cargarPesosDelVet() {
        weightService.getPesosByVet(vetId).enqueue(new Callback<List<Weight>>() {
            @Override
            public void onResponse(Call<List<Weight>> call, Response<List<Weight>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weightList.clear();
                    weightList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DietControlActivity.this, "Error al cargar pesos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Weight>> call, Throwable t) {
                Toast.makeText(DietControlActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoAsignarDieta(Weight weight) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_diet, null);

        TextInputEditText etTipo = view.findViewById(R.id.etTipoComida);
        TextInputEditText etCant = view.findViewById(R.id.etCantidad);
        TextInputEditText etHora = view.findViewById(R.id.etHora);
        TextInputEditText etObs = view.findViewById(R.id.etObservaciones);

        new AlertDialog.Builder(this)
                .setTitle("Asignar dieta a " + weight.getPetName())
                .setView(view)
                .setPositiveButton("Asignar", (dialog, which) -> {
                    Diet dieta = new Diet();
                    dieta.setPetId(weight.getPetId());
                    dieta.setTipoComida(etTipo.getText().toString().trim());
                    dieta.setCantidad(etCant.getText().toString().trim());
                    dieta.setHora(etHora.getText().toString().trim());
                    dieta.setObservaciones(etObs.getText().toString().trim());
                    dieta.setVetId(vetId);
                    dieta.setPesoId(weight.getId());  // <--- Aquí pasas el ID del peso

                    String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    dieta.setFechaRegistro(fechaActual);

                    Log.d("DIETA_DEBUG", "ID mascota (petId): " + dieta.getPetId());
                    Log.d("DIETA_DEBUG", "JSON enviado: " + new com.google.gson.Gson().toJson(dieta));

                    dietService.insertarDieta(dieta).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                weight.setTieneDieta(true);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(DietControlActivity.this, "Dieta asignada", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    String error = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                                    Log.e("DIETA_INSERT", "Error del servidor: " + error);
                                    Toast.makeText(DietControlActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("DIETA_INSERT", "Fallo en conexión: " + t.getMessage(), t);
                            Toast.makeText(DietControlActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDietaAsignada(int petId) {
        dietService.getDietasByPetId(petId).enqueue(new Callback<List<Diet>>() {
            @Override
            public void onResponse(Call<List<Diet>> call, Response<List<Diet>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Diet> listaDietas = response.body();

                    View view = LayoutInflater.from(DietControlActivity.this)
                            .inflate(R.layout.dialog_all_diets, null);

                    RecyclerView recyclerViewDietas = view.findViewById(R.id.recyclerViewDietas);
                    recyclerViewDietas.setLayoutManager(new LinearLayoutManager(DietControlActivity.this));
                    DietAdapter adapter = new DietAdapter(listaDietas);
                    recyclerViewDietas.setAdapter(adapter);

                    new AlertDialog.Builder(DietControlActivity.this)
                            .setTitle("Dietas asignadas")
                            .setView(view)
                            .setPositiveButton("Cerrar", null)
                            .show();
                } else {
                    Toast.makeText(DietControlActivity.this, "No hay dietas registradas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Diet>> call, Throwable t) {
                Toast.makeText(DietControlActivity.this, "Error al obtener dietas", Toast.LENGTH_SHORT).show();
            }
        });
    }





}

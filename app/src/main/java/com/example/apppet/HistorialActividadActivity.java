package com.example.apppet;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.ActividadAdapter;
import com.example.apppet.models.Actividad;
import com.example.apppet.network.ActividadService;
import com.example.apppet.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialActividadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ActividadAdapter adapter;
    private List<Actividad> actividadList = new ArrayList<>();
    private ActividadService actividadService;
    private int petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_actividad);

        petId = getIntent().getIntExtra("pet_id", -1);
        recyclerView = findViewById(R.id.recyclerHistorialActividades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActividadAdapter(actividadList);
        recyclerView.setAdapter(adapter);

        actividadService = RetrofitClient.getRetrofitInstance().create(ActividadService.class);

        cargarHistorial();
    }

    private void cargarHistorial() {
        actividadService.getActividades(petId).enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    actividadList.clear();
                    actividadList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HistorialActividadActivity.this, "No hay actividades registradas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Toast.makeText(HistorialActividadActivity.this, "Error al cargar historial", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

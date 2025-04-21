package com.example.apppet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.ActividadAdapter;
import com.example.apppet.models.Actividad;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ActividadFisicaActivity extends AppCompatActivity {

    private int petId;
    private TextView tvNombreMascota;
    private EditText etTipoActividad, etDuracion, etFecha;
    private Button btnAgregar;
    private RecyclerView rvActividades;
    private ActividadAdapter adapter;
    private List<Actividad> actividades = new ArrayList<>();

    // 🔁 Retrofit Interface Interna
    interface ActividadService {
        @GET("get_actividad_fisica.php")
        Call<List<Actividad>> getActividades(@Query("mascota_id") int mascotaId);

        @FormUrlEncoded
        @POST("insert_actividad_fisica.php")
        Call<Void> insertarActividad(
                @Field("mascota_id") int mascotaId,
                @Field("tipo_actividad") String tipo,
                @Field("duracion") String duracion,
                @Field("fecha") String fecha
        );
    }

    private ActividadService actividadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_fisica);

        petId = getIntent().getIntExtra("pet_id", -1);
        String petName = getIntent().getStringExtra("pet_name");

        tvNombreMascota = findViewById(R.id.tvNombreMascota);
        tvNombreMascota.setText("Actividad física de: " + petName);

        etTipoActividad = findViewById(R.id.etTipoActividad);
        etDuracion = findViewById(R.id.etDuracion);
        etFecha = findViewById(R.id.etFecha);
        btnAgregar = findViewById(R.id.btnAgregarActividad);
        rvActividades = findViewById(R.id.rvActividades);

        adapter = new ActividadAdapter(actividades);
        rvActividades.setLayoutManager(new LinearLayoutManager(this));
        rvActividades.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tuservidor.com/") // 🔁 Reemplázalo con tu URL real
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        actividadService = retrofit.create(ActividadService.class);

        cargarActividades();

        btnAgregar.setOnClickListener(v -> registrarActividad());
    }

    private void cargarActividades() {
        actividadService.getActividades(petId).enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    actividades.clear();
                    actividades.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Toast.makeText(ActividadFisicaActivity.this, "Error al cargar actividades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarActividad() {
        String tipo = etTipoActividad.getText().toString().trim();
        String duracion = etDuracion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (tipo.isEmpty() || duracion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        actividadService.insertarActividad(petId, tipo, duracion, fecha).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ActividadFisicaActivity.this, "✅ Actividad registrada", Toast.LENGTH_SHORT).show();
                cargarActividades();
                etTipoActividad.setText("");
                etDuracion.setText("");
                etFecha.setText("");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ActividadFisicaActivity.this, "❌ Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



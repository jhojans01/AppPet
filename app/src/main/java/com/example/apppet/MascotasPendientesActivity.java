package com.example.apppet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.PetPendienteAdapter;
import com.example.apppet.models.Pet;
import com.example.apppet.network.MascotaService;
import com.example.apppet.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MascotasPendientesActivity extends AppCompatActivity {

    private RecyclerView rvMascotasPendientes;
    private PetPendienteAdapter adapter;
    private List<Pet> mascotasPendientes = new ArrayList<>();
    private MascotaService mascotaService;
    private int cuidadorId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas_pendientes);

        // Inicializa RecyclerView
        rvMascotasPendientes = findViewById(R.id.rvMascotasPendientes);
        rvMascotasPendientes.setLayoutManager(new LinearLayoutManager(this));

        // Obtiene el ID del cuidador desde el intent
        cuidadorId = getIntent().getIntExtra("cuidador_id", -1);
        mascotaService = RetrofitClient.getRetrofitInstance().create(MascotaService.class);

        // Configura el adaptador con las acciones de Aceptar/Rechazar
        adapter = new PetPendienteAdapter(mascotasPendientes, new PetPendienteAdapter.OnPetDecisionListener() {
            @Override
            public void onAceptar(Pet pet) {

                // ⚠️ 1) Modificamos el MISMO objeto que recibimos
                pet.setPendiente(0);                    // ✔ deja de estar pendiente
                pet.setEstado_asignacion("aceptada");   // ✔ pasa a aceptada

                // 2) Llamamos al backend
                mascotaService.updatePet(pet).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MascotasPendientesActivity.this,
                                    "Mascota aceptada", Toast.LENGTH_SHORT).show();
                            cargarMascotasPendientes();          // refresca lista
                        } else {
                            Toast.makeText(MascotasPendientesActivity.this,
                                    "Error al aceptar ("+response.code()+")",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("UpdateMascota", "Error body: "+response.errorBody());
                        }
                    }
                    @Override public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MascotasPendientesActivity.this,
                                "Fallo al aceptar: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRechazar(Pet pet) {

                Pet rechazado = new Pet();

                // ─────────── IDs clave ───────────
                rechazado.setId(pet.getId());          // ID para WHERE
                rechazado.setUserId(pet.getUserId());  // requerido por el backend

                // ─────────── Estado de asignación ───────────
                rechazado.setPendiente(0);                   // ya no está pendiente
                rechazado.setEstado_asignacion("rechazado"); // <-- ¡este es el valor que faltaba!
                rechazado.setCuidadorId(-1);                 // liberamos al cuidador

                // ─────────── Campos obligatorios que NO cambiamos ───────────
                rechazado.setNombre(pet.getNombre());        // mantenemos el nombre original
                rechazado.setEspecie(pet.getEspecie());
                rechazado.setRaza(pet.getRaza());
                rechazado.setEdad(pet.getEdad());
                rechazado.setVetId(pet.getVetId());
                rechazado.setImageBase64(pet.getImageBase64());

                mascotaService.updatePet(rechazado).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MascotasPendientesActivity.this,
                                    "Mascota rechazada", Toast.LENGTH_SHORT).show();
                            cargarMascotasPendientes();          // refresca la lista
                        } else {
                            Toast.makeText(MascotasPendientesActivity.this,
                                    "Error al rechazar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MascotasPendientesActivity.this,
                                "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }



        });

        rvMascotasPendientes.setAdapter(adapter);
        cargarMascotasPendientes();
    }

    // Carga todas las mascotas pendientes del cuidador
    private void cargarMascotasPendientes() {
        mascotaService.getMascotasPendientes(cuidadorId, "pendiente").enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mascotasPendientes.clear();
                    for (Pet pet : response.body()) {
                        if (pet.getPendiente() == 1) { // Filtro doble para asegurar que sea realmente pendiente
                            mascotasPendientes.add(pet);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MascotasPendientesActivity.this, "Error al obtener mascotas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {
                Log.e("MascotasPendientes", "Fallo: " + t.getMessage());
                Toast.makeText(MascotasPendientesActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

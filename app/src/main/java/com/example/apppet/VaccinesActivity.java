package com.example.apppet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.VaccineAdapter;
import com.example.apppet.models.Vaccine;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.VaccineService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VaccinesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVaccines;
    private VaccineAdapter vaccineAdapter;
    private List<Vaccine> vaccineList = new ArrayList<>();
    private VaccineService vaccineService;

    private int petId;
    private FloatingActionButton fabAddVaccine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccines);

        // Obtener pet_id
        petId = getIntent().getIntExtra("pet_id", 0);

        // Instanciar servicio
        vaccineService = RetrofitClient.getRetrofitInstance().create(VaccineService.class);

        // Configurar RecyclerView
        recyclerViewVaccines = findViewById(R.id.recyclerViewVaccines);
        recyclerViewVaccines.setLayoutManager(new LinearLayoutManager(this));
        vaccineAdapter = new VaccineAdapter(vaccineList, new VaccineAdapter.OnVaccineActionListener() {
            @Override
            public void onEditVaccine(Vaccine vaccine) {
                mostrarDialogoEditarVaccine(vaccine);
            }

            @Override
            public void onDeleteVaccine(Vaccine vaccine) {
                confirmarEliminarVaccine(vaccine);
            }
        });
        recyclerViewVaccines.setAdapter(vaccineAdapter);

        // FAB para agregar vacuna
        fabAddVaccine = findViewById(R.id.fabAddVaccine);
        fabAddVaccine.setOnClickListener(v -> mostrarDialogoAgregarVaccine());

        // Cargar vacunas de la mascota
        cargarVacunas();
    }

    private void cargarVacunas() {
        vaccineService.getVacunasByPet(petId).enqueue(new Callback<List<Vaccine>>() {
            @Override
            public void onResponse(Call<List<Vaccine>> call, Response<List<Vaccine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vaccineList.clear();
                    vaccineList.addAll(response.body());
                    vaccineAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VaccinesActivity.this, "Error al obtener vacunas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Vaccine>> call, Throwable t) {
                Toast.makeText(VaccinesActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoAgregarVaccine() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_vaccine, null);

        Spinner spinnerVacunas = dialogView.findViewById(R.id.spinnerVacunas);
        TextInputEditText etFechaAplicacion = dialogView.findViewById(R.id.etFechaAplicacion);
        Spinner spinnerEstado = dialogView.findViewById(R.id.spinnerEstadoVacuna);

        // Lista de vacunas
        String[] listaVacunas = {"Rabia", "Moquillo", "Parvovirus", "Leptospirosis"};
        ArrayAdapter<String> adapterVacunas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaVacunas);
        adapterVacunas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVacunas.setAdapter(adapterVacunas);

        // Lista de estado (Falta / Aplicada)
        String[] listaEstado = {"Falta", "Aplicada"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaEstado);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        new AlertDialog.Builder(this)
                .setTitle("Agregar Vacuna")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String vacunaSeleccionada = (String) spinnerVacunas.getSelectedItem();
                    String fecha = etFechaAplicacion.getText().toString().trim();
                    String estadoSeleccionado = (String) spinnerEstado.getSelectedItem();

                    Vaccine nueva = new Vaccine();
                    nueva.setPetId(petId);
                    nueva.setNombreVacuna(vacunaSeleccionada);
                    nueva.setFechaAplicacion(fecha);
                    nueva.setEstadoVacuna(estadoSeleccionado);

                    vaccineService.insertVaccine(nueva).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(VaccinesActivity.this, "Vacuna agregada", Toast.LENGTH_SHORT).show();
                                cargarVacunas();
                            } else {
                                try {
                                    String error = response.errorBody().string();
                                    Toast.makeText(VaccinesActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(VaccinesActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarVaccine(Vaccine vaccine) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_vaccine, null);

        Spinner spinnerVacunas = dialogView.findViewById(R.id.spinnerVacunas);
        TextInputEditText etFechaAplicacion = dialogView.findViewById(R.id.etFechaAplicacion);
        Spinner spinnerEstado = dialogView.findViewById(R.id.spinnerEstadoVacuna);

        String[] listaVacunas = {"Rabia", "Moquillo", "Parvovirus", "Leptospirosis"};
        ArrayAdapter<String> adapterVacunas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaVacunas);
        adapterVacunas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVacunas.setAdapter(adapterVacunas);

        String[] listaEstado = {"Falta", "Aplicada"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaEstado);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        // Seleccionar la vacuna actual en el Spinner
        int posVacuna = 0;
        for (int i = 0; i < listaVacunas.length; i++) {
            if (listaVacunas[i].equalsIgnoreCase(vaccine.getNombreVacuna())) {
                posVacuna = i;
                break;
            }
        }
        spinnerVacunas.setSelection(posVacuna);

        // Fecha
        etFechaAplicacion.setText(vaccine.getFechaAplicacion());

        // Estado actual
        int posEstado = vaccine.getEstadoVacuna().equalsIgnoreCase("Aplicada") ? 1 : 0;
        spinnerEstado.setSelection(posEstado);

        new AlertDialog.Builder(this)
                .setTitle("Editar Vacuna")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String vacunaSeleccionada = (String) spinnerVacunas.getSelectedItem();
                    String fecha = etFechaAplicacion.getText().toString().trim();
                    String estadoSeleccionado = (String) spinnerEstado.getSelectedItem();

                    vaccine.setNombreVacuna(vacunaSeleccionada);
                    vaccine.setFechaAplicacion(fecha);
                    vaccine.setEstadoVacuna(estadoSeleccionado);

                    vaccineService.updateVaccine(vaccine).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(VaccinesActivity.this, "Vacuna actualizada", Toast.LENGTH_SHORT).show();
                                cargarVacunas();
                            } else {
                                Toast.makeText(VaccinesActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(VaccinesActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarVaccine(Vaccine vaccine) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Vacuna")
                .setMessage("¿Deseas eliminar la vacuna " + vaccine.getNombreVacuna() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    vaccineService.deleteVaccine(vaccine).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(VaccinesActivity.this, "Vacuna eliminada", Toast.LENGTH_SHORT).show();
                                cargarVacunas();
                            } else {
                                Toast.makeText(VaccinesActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(VaccinesActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

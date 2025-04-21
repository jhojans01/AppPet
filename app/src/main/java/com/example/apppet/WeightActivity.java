package com.example.apppet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.WeightAdapter;
import com.example.apppet.models.Weight;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.WeightService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeightActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPesos;
    private WeightAdapter weightAdapter;
    private List<Weight> weightList = new ArrayList<>();
    private WeightService weightService;

    // Este es el ID de la mascota que recibes por intent
    private int petId;

    // Botón flotante para agregar peso
    private FloatingActionButton fabAddPeso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        // 1. Obtener el pet_id desde el intent
        petId = getIntent().getIntExtra("pet_id", 0);
        Log.d("WeightActivity", "petId recibido: " + petId);

        // 2. Crear la instancia de WeightService con Retrofit
        weightService = RetrofitClient.getRetrofitInstance().create(WeightService.class);

        // 3. Configurar el RecyclerView
        recyclerViewPesos = findViewById(R.id.recyclerViewPesos);
        recyclerViewPesos.setLayoutManager(new LinearLayoutManager(this));

        // 4. Crear y asignar el adaptador
        weightAdapter = new WeightAdapter(weightList, new WeightAdapter.OnWeightActionListener() {
            @Override
            public void onEditWeight(Weight weight) {
                mostrarDialogoEditarPeso(weight);
            }

            @Override
            public void onDeleteWeight(Weight weight) {
                confirmarEliminarPeso(weight);
            }
        });
        recyclerViewPesos.setAdapter(weightAdapter);

        // 5. Botón flotante para agregar un nuevo peso
        fabAddPeso = findViewById(R.id.fabAddPeso);
        fabAddPeso.setOnClickListener(v -> mostrarDialogoAgregarPeso());

        // 6. Cargar la lista de pesos al iniciar
        cargarPesos();
    }

    /**
     * Llama al servicio para obtener la lista de pesos de la mascota.
     */
    private void cargarPesos() {
        // Llamamos a getPesosByPet(petId)
        weightService.getPesosByPet(petId).enqueue(new Callback<List<Weight>>() {
            @Override
            public void onResponse(Call<List<Weight>> call, Response<List<Weight>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weightList.clear();
                    weightList.addAll(response.body());
                    weightAdapter.notifyDataSetChanged();
                } else {
                    // p.ej. 404, 500, etc.
                    Toast.makeText(WeightActivity.this,
                            "Error al obtener pesos (HTTP " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Weight>> call, Throwable t) {
                // Fallo de red u otra excepción
                Toast.makeText(WeightActivity.this,
                        "Fallo al cargar pesos: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra un diálogo para que el usuario ingrese un nuevo peso y fecha_registro.
     */
    private void mostrarDialogoAgregarPeso() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_weight, null);

        TextInputEditText etPeso = dialogView.findViewById(R.id.etPeso);
        TextInputEditText etFecha = dialogView.findViewById(R.id.etFechaPeso);

        new AlertDialog.Builder(this)
                .setTitle("Registrar Peso")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {

                    String pesoStr = etPeso.getText().toString().trim();
                    String fechaStr = etFecha.getText().toString().trim();

                    double pesoVal = 0;
                    try {
                        pesoVal = Double.parseDouble(pesoStr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    // Crear el objeto Weight
                    Weight nuevoPeso = new Weight();
                    nuevoPeso.setPetId(petId);
                    nuevoPeso.setPeso(pesoVal);
                    // Gracias a @SerializedName("fecha_registro"), al enviar el JSON
                    // será "fecha_registro": "lo que ponga el usuario"
                    nuevoPeso.setFechaRegistro(fechaStr);

                    // Insertar llamando al endpoint POST de pesos.php
                    weightService.insertPeso(nuevoPeso).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Insert OK
                                Toast.makeText(WeightActivity.this,
                                        "Peso registrado",
                                        Toast.LENGTH_SHORT).show();
                                // Refrescar lista
                                cargarPesos();

                            } else {
                                // Error HTTP: leer errorBody
                                try {
                                    if (response.errorBody() != null) {
                                        String error = response.errorBody().string();
                                        Toast.makeText(WeightActivity.this,
                                                "Error al insertar: " + error,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(WeightActivity.this,
                                                "Error desconocido al insertar",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Fallo de red u otra excepción
                            Toast.makeText(WeightActivity.this,
                                    "Fallo al insertar: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Muestra un diálogo para editar un peso existente.
     */
    private void mostrarDialogoEditarPeso(Weight weight) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_weight, null);

        TextInputEditText etPeso = dialogView.findViewById(R.id.etPeso);
        TextInputEditText etFecha = dialogView.findViewById(R.id.etFechaPeso);

        // Rellenar campos con los valores actuales
        etPeso.setText(String.valueOf(weight.getPeso()));
        etFecha.setText(weight.getFechaRegistro());

        new AlertDialog.Builder(this)
                .setTitle("Editar Peso")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {

                    String pesoStr = etPeso.getText().toString().trim();
                    String fechaStr = etFecha.getText().toString().trim();

                    double pesoVal = 0;
                    try {
                        pesoVal = Double.parseDouble(pesoStr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    weight.setPeso(pesoVal);
                    weight.setFechaRegistro(fechaStr);

                    // Llamar al endpoint PUT (updatePeso.php)
                    weightService.updatePeso(weight).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(WeightActivity.this,
                                        "Peso actualizado",
                                        Toast.LENGTH_SHORT).show();
                                cargarPesos();
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        String error = response.errorBody().string();
                                        Toast.makeText(WeightActivity.this,
                                                "Error al actualizar: " + error,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(WeightActivity.this,
                                                "Error desconocido al actualizar",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(WeightActivity.this,
                                    "Fallo al actualizar: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Confirma la eliminación de un peso y llama al endpoint DELETE.
     */
    private void confirmarEliminarPeso(Weight weight) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Registro de Peso")
                .setMessage("¿Deseas eliminar este registro?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    weightService.deletePeso(weight).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(WeightActivity.this,
                                        "Registro eliminado",
                                        Toast.LENGTH_SHORT).show();
                                cargarPesos();
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        String error = response.errorBody().string();
                                        Toast.makeText(WeightActivity.this,
                                                "Error al eliminar registro: " + error,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(WeightActivity.this,
                                                "Error desconocido al eliminar registro",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(WeightActivity.this,
                                    "Fallo al eliminar: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

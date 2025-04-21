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

import com.example.apppet.R;
import com.example.apppet.adapters.BehaviorAdapter;
import com.example.apppet.models.Behavior;
import com.example.apppet.network.BehaviorService;
import com.example.apppet.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BehaviorActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBehavior;
    private BehaviorAdapter behaviorAdapter;
    private List<Behavior> behaviorList = new ArrayList<>();
    private BehaviorService behaviorService;

    private int petId;
    private FloatingActionButton fabAddBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior);

        // Obtener el pet_id del Intent
        petId = getIntent().getIntExtra("pet_id", 0);

        // Instanciar Retrofit
        behaviorService = RetrofitClient.getRetrofitInstance().create(BehaviorService.class);

        // Configurar RecyclerView
        recyclerViewBehavior = findViewById(R.id.recyclerViewBehavior);
        recyclerViewBehavior.setLayoutManager(new LinearLayoutManager(this));
        behaviorAdapter = new BehaviorAdapter(behaviorList, new BehaviorAdapter.OnBehaviorActionListener() {
            @Override
            public void onEditBehavior(Behavior behavior) {
                mostrarDialogoEditarBehavior(behavior);
            }

            @Override
            public void onDeleteBehavior(Behavior behavior) {
                confirmarEliminarBehavior(behavior);
            }
        });
        recyclerViewBehavior.setAdapter(behaviorAdapter);

        // FAB para agregar comportamiento
        fabAddBehavior = findViewById(R.id.fabAddBehavior);
        fabAddBehavior.setOnClickListener(v -> mostrarDialogoAgregarBehavior());

        // Cargar comportamientos al iniciar
        cargarComportamientos();
    }

    private void cargarComportamientos() {
        behaviorService.getComportamientosByPet(petId).enqueue(new Callback<List<Behavior>>() {
            @Override
            public void onResponse(Call<List<Behavior>> call, Response<List<Behavior>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    behaviorList.clear();
                    behaviorList.addAll(response.body());
                    behaviorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(BehaviorActivity.this, "Error al obtener comportamientos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Behavior>> call, Throwable t) {
                Toast.makeText(BehaviorActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoAgregarBehavior() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_behavior, null);

        // Referencias a los campos
        Spinner spinnerComportamiento = dialogView.findViewById(R.id.spinnerComportamiento);
        TextInputEditText etFecha = dialogView.findViewById(R.id.etFecha);
        TextInputEditText etHora = dialogView.findViewById(R.id.etHora);

        // Llenar el Spinner con comportamientos predefinidos
        String[] listaComportamientos = {"Tranquila", "Ansiosa", "Juguetona", "Agresiva"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listaComportamientos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComportamiento.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle("Registrar Comportamiento")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    // Obtener valores de los campos
                    String comportamientoSel = (String) spinnerComportamiento.getSelectedItem();
                    String fecha = etFecha.getText().toString().trim(); // p.ej. "2025-03-24"
                    String hora = etHora.getText().toString().trim();   // p.ej. "14:30:00"

                    // Combinar ambos en "YYYY-MM-DD HH:mm:ss"
                    String fechaHora = fecha + " " + hora;

                    Behavior nuevo = new Behavior();
                    nuevo.setPetId(petId);
                    nuevo.setComportamiento(comportamientoSel);
                    nuevo.setFechaHora(fechaHora);

                    // Llamada para insertar
                    behaviorService.insertBehavior(nuevo).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BehaviorActivity.this, "Comportamiento agregado", Toast.LENGTH_SHORT).show();
                                cargarComportamientos();
                            } else {
                                try {
                                    String error = response.errorBody().string();
                                    Toast.makeText(BehaviorActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(BehaviorActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarBehavior(Behavior behavior) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_behavior, null);

        Spinner spinnerComportamiento = dialogView.findViewById(R.id.spinnerComportamiento);
        TextInputEditText etFecha = dialogView.findViewById(R.id.etFecha);
        TextInputEditText etHora = dialogView.findViewById(R.id.etHora);

        String[] listaComportamientos = {"Tranquila", "Ansiosa", "Juguetona", "Agresiva"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listaComportamientos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComportamiento.setAdapter(adapter);

        // Seleccionar comportamiento actual
        int pos = 0;
        for (int i = 0; i < listaComportamientos.length; i++) {
            if (listaComportamientos[i].equalsIgnoreCase(behavior.getComportamiento())) {
                pos = i;
                break;
            }
        }
        spinnerComportamiento.setSelection(pos);

        // Dividir la fechaHora si está en formato "YYYY-MM-DD HH:mm:ss"
        String[] partes = behavior.getFechaHora().split(" ");
        if (partes.length == 2) {
            etFecha.setText(partes[0]); // "2025-03-24"
            etHora.setText(partes[1]);  // "14:30:00"
        }

        new AlertDialog.Builder(this)
                .setTitle("Editar Comportamiento")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String comportamientoSel = (String) spinnerComportamiento.getSelectedItem();
                    String fecha = etFecha.getText().toString().trim();
                    String hora = etHora.getText().toString().trim();

                    String fechaHora = fecha + " " + hora; // Combinar

                    behavior.setComportamiento(comportamientoSel);
                    behavior.setFechaHora(fechaHora);

                    // Actualizar en BD
                    behaviorService.updateBehavior(behavior).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BehaviorActivity.this, "Comportamiento actualizado", Toast.LENGTH_SHORT).show();
                                cargarComportamientos();
                            } else {
                                Toast.makeText(BehaviorActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(BehaviorActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarBehavior(Behavior behavior) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Registro")
                .setMessage("¿Deseas eliminar este comportamiento?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    behaviorService.deleteBehavior(behavior).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BehaviorActivity.this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                                cargarComportamientos();
                            } else {
                                Toast.makeText(BehaviorActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(BehaviorActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

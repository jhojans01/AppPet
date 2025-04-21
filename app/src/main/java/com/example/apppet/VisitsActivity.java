package com.example.apppet;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.adapters.VisitAdapter;
import com.example.apppet.models.Visit;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.VisitService;
import com.example.apppet.notifications.VisitReminderReceiver;
import com.example.apppet.sqlite.VisitLocalRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.AutoCompleteTextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.apppet.sqlite.VisitDbHelper;


public class VisitsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVisits;
    private VisitAdapter visitAdapter;
    private List<Visit> visitList = new ArrayList<>();
    private VisitService visitService;

    private int petId;
    private FloatingActionButton fabAddVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);

        // Obtener pet_id del Intent
        petId = getIntent().getIntExtra("pet_id", 0);

        // Instanciar Retrofit
        visitService = RetrofitClient.getRetrofitInstance().create(VisitService.class);

        // Configurar RecyclerView
        recyclerViewVisits = findViewById(R.id.recyclerViewVisits);
        recyclerViewVisits.setLayoutManager(new LinearLayoutManager(this));
        visitAdapter = new VisitAdapter(visitList, new VisitAdapter.OnVisitActionListener() {
            @Override
            public void onEditVisit(Visit visit) {
                mostrarDialogoEditarVisit(visit);
            }

            @Override
            public void onDeleteVisit(Visit visit) {
                confirmarEliminarVisit(visit);
            }
        });
        recyclerViewVisits.setAdapter(visitAdapter);

        // FAB para agregar visita
        fabAddVisit = findViewById(R.id.fabAddVisit);
        fabAddVisit.setOnClickListener(v -> mostrarDialogoAgregarVisit());

        // Cargar visitas
        cargarVisitas();
        mostrarVisitasEnLog();
    }

    private void cargarVisitas() {
        visitService.getVisitsByPet(petId).enqueue(new Callback<List<Visit>>() {
            @Override
            public void onResponse(Call<List<Visit>> call, Response<List<Visit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    visitList.clear();
                    visitList.addAll(response.body());
                    visitAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VisitsActivity.this, "Error al obtener visitas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Visit>> call, Throwable t) {
                Toast.makeText(VisitsActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void cargarVisitasLocales() {
        VisitLocalRepository localRepo = new VisitLocalRepository(this);
        List<Visit> visitasLocales = localRepo.obtenerVisitasPorMascota(petId);

        visitList.clear();
        visitList.addAll(visitasLocales);
        visitAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Cargadas desde SQLite (" + visitasLocales.size() + " visitas)", Toast.LENGTH_SHORT).show();
    }


    private void mostrarDialogoAgregarVisit() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_visit, null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etFecha = dialogView.findViewById(R.id.etFecha);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) AutoCompleteTextView etMotivo = dialogView.findViewById(R.id.etMotivo);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etObservaciones = dialogView.findViewById(R.id.etObservaciones);

        // Sugerencias para el motivo
        String[] motivosSugeridos = {
                "Chequeo rutinario",
                "Consulta por dolor",
                "Vacunación",
                "Desparasitación",
                "Emergencia"
        };
        ArrayAdapter<String> adapterMotivos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                motivosSugeridos
        );
        etMotivo.setAdapter(adapterMotivos);

        new AlertDialog.Builder(this)
                .setTitle("Agregar Visita al Veterinario")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String fecha = etFecha.getText().toString().trim();
                    String motivo = etMotivo.getText().toString().trim();
                    String observaciones = etObservaciones.getText().toString().trim();

                    if (fecha.isEmpty() || motivo.isEmpty()) {
                        Toast.makeText(VisitsActivity.this, "Fecha y motivo son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Visit nuevaVisita = new Visit();
                    nuevaVisita.setPetId(petId);
                    nuevaVisita.setFecha(fecha);
                    nuevaVisita.setMotivo(motivo);
                    nuevaVisita.setObservaciones(observaciones);

                    visitService.insertVisit(nuevaVisita).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                VisitLocalRepository localRepo = new VisitLocalRepository(VisitsActivity.this);
                                localRepo.insertarVisita(nuevaVisita);

                                Toast.makeText(VisitsActivity.this, "Visita registrada", Toast.LENGTH_SHORT).show();
                                cargarVisitas();
                                agendarRecordatorio(nuevaVisita);

                            } else {
                                try {
                                    String error = response.errorBody().string();
                                    Toast.makeText(VisitsActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(VisitsActivity.this, "No hay conexión. Cargando visitas locales...", Toast.LENGTH_LONG).show();
                            cargarVisitasLocales(); // 🧠 Cargamos desde SQLite si no se pudo conectar al servidor
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void agendarRecordatorio(Visit visita) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            // 👉 Concatenamos la hora fija 09:00:00
            String fechaConHora = visita.getFecha() + " 09:00:00";

            Date fechaVisita = sdf.parse(fechaConHora);

            // 📆 Un día antes
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaVisita);
            calendar.add(Calendar.MINUTE, 1); // Restar 1 día

            Intent intent = new Intent(this, VisitReminderReceiver.class);
            intent.putExtra("motivo", visita.getMotivo());
            intent.putExtra("fecha", visita.getFecha()); // solo la fecha visible

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    private void mostrarDialogoEditarVisit(Visit visit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_visit, null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etFecha = dialogView.findViewById(R.id.etFecha);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) AutoCompleteTextView etMotivo = dialogView.findViewById(R.id.etMotivo);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etObservaciones = dialogView.findViewById(R.id.etObservaciones);

        // Sugerencias para el motivo
        String[] motivosSugeridos = {
                "Chequeo rutinario",
                "Consulta por dolor",
                "Vacunación",
                "Desparasitación",
                "Emergencia"
        };
        ArrayAdapter<String> adapterMotivos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                motivosSugeridos
        );
        etMotivo.setAdapter(adapterMotivos);

        // Rellenar campos
        etFecha.setText(visit.getFecha());
        etMotivo.setText(visit.getMotivo());
        etObservaciones.setText(visit.getObservaciones());

        new AlertDialog.Builder(this)
                .setTitle("Editar Visita")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String fecha = etFecha.getText().toString().trim();
                    String motivo = etMotivo.getText().toString().trim();
                    String obs = etObservaciones.getText().toString().trim();

                    if (fecha.isEmpty() || motivo.isEmpty()) {
                        Toast.makeText(VisitsActivity.this, "Fecha y motivo son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    visit.setFecha(fecha);
                    visit.setMotivo(motivo);
                    visit.setObservaciones(obs);

                    visitService.updateVisit(visit).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(VisitsActivity.this, "Visita actualizada", Toast.LENGTH_SHORT).show();
                                cargarVisitas();

                            } else {
                                Toast.makeText(VisitsActivity.this, "Error al actualizar visita", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(VisitsActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarVisit(Visit visit) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Visita")
                .setMessage("¿Desea eliminar esta visita?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    visitService.deleteVisit(visit).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(VisitsActivity.this, "Visita eliminada", Toast.LENGTH_SHORT).show();
                                cargarVisitas();
                            } else {
                                Toast.makeText(VisitsActivity.this, "Error al eliminar visita", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(VisitsActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    public void mostrarVisitasEnLog() {
        SQLiteDatabase db = new VisitDbHelper(this).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + VisitDbHelper.TABLE_VISITS, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int petId = cursor.getInt(cursor.getColumnIndexOrThrow("pet_id"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
            String motivo = cursor.getString(cursor.getColumnIndexOrThrow("motivo"));
            String obs = cursor.getString(cursor.getColumnIndexOrThrow("observaciones"));

            Log.d("SQLiteVisita", "ID: " + id + ", PET: " + petId + ", Fecha: " + fecha +
                    ", Motivo: " + motivo + ", Obs: " + obs);
        }

        cursor.close();
        db.close();
    }

}

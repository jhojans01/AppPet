package com.example.apppet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apppet.adapters.VisitAdapter;
import com.example.apppet.models.Visit;
import com.example.apppet.sqlite.VisitLocalRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class VisitsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVisits;
    private VisitAdapter visitAdapter;
    private List<Visit> visitList = new ArrayList<>();
    private int petId;
    private FloatingActionButton fabAddVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);

        petId = getIntent().getIntExtra("pet_id", 0);

        recyclerViewVisits = findViewById(R.id.recyclerViewVisits);
        recyclerViewVisits.setLayoutManager(new LinearLayoutManager(this));
        visitAdapter = new VisitAdapter(visitList, new VisitAdapter.OnVisitActionListener() {
            @Override
            public void onEditVisit(Visit visit) {
                mostrarDialogoEditarVisit(visit);
            }

            @Override
            public void onDeleteVisit(Visit visit) {
                VisitLocalRepository repo = new VisitLocalRepository(VisitsActivity.this);
                repo.eliminarVisita(visit.getId());
                cargarVisitasLocales();
            }
        });
        recyclerViewVisits.setAdapter(visitAdapter);

        fabAddVisit = findViewById(R.id.fabAddVisit);
        fabAddVisit.setOnClickListener(v -> mostrarDialogoAgregarVisit());

        cargarVisitasLocales();
    }

    private void cargarVisitasLocales() {
        VisitLocalRepository localRepo = new VisitLocalRepository(this);
        List<Visit> visitasLocales = localRepo.obtenerVisitasPorMascota(petId);
        visitList.clear();
        visitList.addAll(visitasLocales);
        visitAdapter.notifyDataSetChanged();
    }


    private void mostrarDialogoAgregarVisit() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_visit, null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etFecha = dialogView.findViewById(R.id.etFecha);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) AutoCompleteTextView etMotivo = dialogView.findViewById(R.id.etMotivo);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etObservaciones = dialogView.findViewById(R.id.etObservaciones);

        String[] motivosSugeridos = {
                "Chequeo rutinario", "Consulta por dolor", "Vacunación", "Desparasitación", "Emergencia"
        };
        etMotivo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, motivosSugeridos));

        new AlertDialog.Builder(this)
                .setTitle("Agregar Visita")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String fecha = etFecha.getText().toString().trim();
                    String motivo = etMotivo.getText().toString().trim();
                    String observaciones = etObservaciones.getText().toString().trim();

                    if (fecha.isEmpty() || motivo.isEmpty()) {
                        Toast.makeText(this, "Fecha y motivo son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Visit nuevaVisita = new Visit();
                    nuevaVisita.setPetId(petId);
                    nuevaVisita.setFecha(fecha);
                    nuevaVisita.setMotivo(motivo);
                    nuevaVisita.setObservaciones(observaciones);

                    VisitLocalRepository repo = new VisitLocalRepository(this);
                    repo.insertarVisita(nuevaVisita);

                    cargarVisitasLocales();
                    Toast.makeText(this, "Visita guardada en SQLite", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarVisit(Visit visit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_visit, null);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etFecha = dialogView.findViewById(R.id.etFecha);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) AutoCompleteTextView etMotivo = dialogView.findViewById(R.id.etMotivo);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText etObservaciones = dialogView.findViewById(R.id.etObservaciones);

        etFecha.setText(visit.getFecha());
        etMotivo.setText(visit.getMotivo());
        etObservaciones.setText(visit.getObservaciones());

        String[] motivosSugeridos = {
                "Chequeo rutinario", "Consulta por dolor", "Vacunación", "Desparasitación", "Emergencia"
        };
        etMotivo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, motivosSugeridos));

        new AlertDialog.Builder(this)
                .setTitle("Editar Visita")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String fecha = etFecha.getText().toString().trim();
                    String motivo = etMotivo.getText().toString().trim();
                    String observaciones = etObservaciones.getText().toString().trim();

                    if (fecha.isEmpty() || motivo.isEmpty()) {
                        Toast.makeText(this, "Fecha y motivo son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    visit.setFecha(fecha);
                    visit.setMotivo(motivo);
                    visit.setObservaciones(observaciones);

                    VisitLocalRepository repo = new VisitLocalRepository(this);
                    repo.actualizarVisita(visit);

                    cargarVisitasLocales();
                    Toast.makeText(this, "Visita actualizada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

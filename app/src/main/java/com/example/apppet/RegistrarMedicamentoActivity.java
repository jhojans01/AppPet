package com.example.apppet;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apppet.models.Medicamento;
import com.example.apppet.network.MedicamentoService;
import com.example.apppet.network.RetrofitClient;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarMedicamentoActivity extends AppCompatActivity {

    private EditText etEnfermedad, etMedicamento, etObservaciones;
    private Spinner spinnerVia;
    private TimePicker timePicker;
    private Button btnGuardar;

    private int petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_medicamento);

        etEnfermedad = findViewById(R.id.etEnfermedad);
        etMedicamento = findViewById(R.id.etMedicamento);
        etObservaciones = findViewById(R.id.etObservaciones);
        spinnerVia = findViewById(R.id.spinnerVia);
        timePicker = findViewById(R.id.timePicker);
        btnGuardar = findViewById(R.id.btnGuardar);
        timePicker.setIs24HourView(true);


        // Recibe el ID de la mascota
        petId = getIntent().getIntExtra("pet_id", 0);

        btnGuardar.setOnClickListener(v -> guardarMedicamento());
        Spinner spinnerVia = findViewById(R.id.spinnerVia);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.vias_medicamento,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVia.setAdapter(adapter);

    }

    private void guardarMedicamento() {
        String enfermedad = etEnfermedad.getText().toString().trim();
        String nombreMed = etMedicamento.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();
        String via = spinnerVia.getSelectedItem().toString();

        int hora = timePicker.getHour();
        int minuto = timePicker.getMinute();
        String horaFormateada = String.format("%02d:%02d", hora, minuto);

        Medicamento medicamento = new Medicamento(petId, 0, enfermedad, nombreMed, via, horaFormateada, observaciones);

        medicamento.setPetId(petId);
        medicamento.setEnfermedad(enfermedad);
        medicamento.setMedicamento(nombreMed);
        medicamento.setHora(horaFormateada);
        medicamento.setVia(via);
        medicamento.setObservaciones(observaciones);

        MedicamentoService service = RetrofitClient.getRetrofitInstance().create(MedicamentoService.class);
        service.insertarMedicamento(medicamento).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrarMedicamentoActivity.this, "Medicamento registrado", Toast.LENGTH_SHORT).show();
                    programarAlarma(hora, minuto, nombreMed, enfermedad);
                    finish(); // Cierra la actividad
                } else {
                    Toast.makeText(RegistrarMedicamentoActivity.this, "Error al guardar medicamento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegistrarMedicamentoActivity.this, "Fallo al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void programarAlarma(int hora, int minuto, String medicamento, String enfermedad) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "❌ El dispositivo no permite alarmas exactas", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, AlarmaReceiver.class);
        intent.putExtra("medicamento", medicamento);
        intent.putExtra("enfermedad", enfermedad);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}

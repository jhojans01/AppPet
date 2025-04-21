package com.example.apppet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalleMascotaVetActivity extends AppCompatActivity {

    private TextView tvNombre, tvEspecie, tvRaza, tvEdad;
    private ImageView ivMascota;
    private Button btnRegistrarMedicamento, btnAsignarDieta;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota_vet);

        tvNombre = findViewById(R.id.tvNombreDetalle);
        tvEspecie = findViewById(R.id.tvEspecieDetalle);
        tvRaza = findViewById(R.id.tvRazaDetalle);
        tvEdad = findViewById(R.id.tvEdadDetalle);
        ivMascota = findViewById(R.id.ivMascotaDetalle);

        btnRegistrarMedicamento = findViewById(R.id.btnRegistrarMedicamento);
        btnAsignarDieta = findViewById(R.id.btnAsignarDieta); // ✅ NUEVO BOTÓN

        // Obtener datos del intent
        String nombre = getIntent().getStringExtra("nombre");
        String especie = getIntent().getStringExtra("especie");
        String raza = getIntent().getStringExtra("raza");
        int edad = getIntent().getIntExtra("edad", 0);
        String imagenBase64 = getIntent().getStringExtra("imagenBase64");
        int petId = getIntent().getIntExtra("pet_id", -1);
        int vetId = getSharedPreferences("USER_DATA", MODE_PRIVATE).getInt("user_id", 0);

        // Mostrar info
        tvNombre.setText(nombre);
        tvEspecie.setText("Especie: " + especie);
        tvRaza.setText("Raza: " + raza);
        tvEdad.setText("Edad: " + edad);

        if (imagenBase64 != null && !imagenBase64.equalsIgnoreCase("null")) {
            byte[] imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivMascota.setImageBitmap(decodedImage);
        } else {
            ivMascota.setImageResource(R.drawable.perritico);
        }

        btnRegistrarMedicamento.setOnClickListener(v -> {
            Intent i = new Intent(DetalleMascotaVetActivity.this, RegistrarMedicamentoActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });

        Button btnAsignarDieta = findViewById(R.id.btnAsignarDieta);
        btnAsignarDieta.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleMascotaVetActivity.this, DietControlActivity.class);
            intent.putExtra("loggedUserId", vetId); // Usa el mismo ID del veterinario
            startActivity(intent);
        });

    }
}

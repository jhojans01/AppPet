package com.example.apppet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apppet.models.Pet;

import androidx.appcompat.app.AppCompatActivity;

public class CuidadoMascotaActivity extends AppCompatActivity {

    private ImageView ivPetImage;
    private TextView tvNombre, tvEspecie, tvRaza, tvEdad;
    private Button btnAgregarActividad;

    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuidado_mascota);

        ivPetImage = findViewById(R.id.ivPetImage);
        tvNombre = findViewById(R.id.tvNombre);
        tvEspecie = findViewById(R.id.tvEspecie);
        tvRaza = findViewById(R.id.tvRaza);
        tvEdad = findViewById(R.id.tvEdad);
        btnAgregarActividad = findViewById(R.id.btnAgregarActividad);

        if (getIntent() != null && getIntent().hasExtra("pet")) {
            pet = (Pet) getIntent().getSerializableExtra("pet");
            mostrarDatosMascota();
        }

        btnAgregarActividad.setOnClickListener(v -> {
            Intent intent = new Intent(CuidadoMascotaActivity.this, ActividadFisicaActivity.class);
            intent.putExtra("pet_id", pet.getId());
            intent.putExtra("pet_name", pet.getNombre());
            startActivity(intent);
        });

    }

    private void mostrarDatosMascota() {
        tvNombre.setText(pet.getNombre());
        tvEspecie.setText("Especie: " + pet.getEspecie());
        tvRaza.setText("Raza: " + pet.getRaza());
        tvEdad.setText("Edad: " + pet.getEdad());

        if (pet.getImageBase64() != null && !pet.getImageBase64().isEmpty()) {
            byte[] imageBytes = Base64.decode(pet.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivPetImage.setImageBitmap(bitmap);
        } else {
            ivPetImage.setImageResource(R.drawable.ic_pet_placeholder); // placeholder por defecto
        }
    }
}

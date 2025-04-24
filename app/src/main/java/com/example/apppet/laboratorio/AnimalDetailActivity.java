package com.example.apppet.laboratorio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apppet.R;

public class AnimalDetailActivity extends AppCompatActivity {

    ImageView imageViewAnimal;
    TextView textViewAnimalName, textViewAnimalDescription;
    String category;
    int animalIndex;
    Animal[] animals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        imageViewAnimal = findViewById(R.id.imageViewAnimal);
        textViewAnimalName = findViewById(R.id.textViewAnimalName);
        textViewAnimalDescription = findViewById(R.id.textViewAnimalDescription);

        // Recupera la categoría y el índice del animal seleccionado
        Intent intent = getIntent();
        category = intent.getStringExtra("CATEGORY");
        animalIndex = intent.getIntExtra("ANIMAL_INDEX", 0);

        // Cargar datos nuevamente según la categoría (en una aplicación real podrías centralizar estos datos en una clase de utilidad)
        if ("Perros".equals(category)) {
            animals = new Animal[]{
                    new Animal("Labrador", R.drawable.labrador, "Un perro amigable y activo."),
                    new Animal("Bulldog", R.drawable.bulldog, "Perro robusto y tierno.")
            };
        } else if ("Gatos".equals(category)) {
            animals = new Animal[]{
                    new Animal("Persa", R.drawable.persa, "Gato de pelo largo, tranquilo y elegante."),
                    new Animal("Siames", R.drawable.siames, "Gato activo, curioso y comunicativo.")
            };
        } else {
            animals = new Animal[]{
                    new Animal("Conejo", R.drawable.conejo, "Pequeño, suave y juguetón."),
                    new Animal("Hamster", R.drawable.hamster, "Pequeño, ágil y de fácil cuidado.")
            };
        }

        // Obtiene el animal seleccionado y actualiza la vista
        Animal selectedAnimal = animals[animalIndex];
        textViewAnimalName.setText(selectedAnimal.getName());
        textViewAnimalDescription.setText(selectedAnimal.getDescription());
        imageViewAnimal.setImageResource(selectedAnimal.getImageResourceId());
    }
}

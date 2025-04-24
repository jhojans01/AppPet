package com.example.apppet.laboratorio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apppet.R;

public class AnimalCategoryActivity extends AppCompatActivity {

    ListView listViewAnimals;
    Animal[] animals;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_category);

        listViewAnimals = findViewById(R.id.listViewAnimals);
        // Recupera la categoría seleccionada de la actividad principal
        category = getIntent().getStringExtra("CATEGORY");

        // Cargar datos según la categoría
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
            // Categoría "Otros"
            animals = new Animal[]{
                    new Animal("Conejo", R.drawable.conejo, "Pequeño, suave y juguetón."),
                    new Animal("Hamster", R.drawable.hamster, "Pequeño, ágil y de fácil cuidado.")
            };
        }

        ArrayAdapter<Animal> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, animals);
        listViewAnimals.setAdapter(adapter);

        // Listener para detectar clic en un animal y pasar a la pantalla de detalle
        listViewAnimals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AnimalCategoryActivity.this, AnimalDetailActivity.class);
                intent.putExtra("ANIMAL_INDEX", position);
                intent.putExtra("CATEGORY", category);
                startActivity(intent);
            }
        });
    }
}
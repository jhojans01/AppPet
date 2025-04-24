package com.example.apppet.laboratorio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apppet.R;

public class TopLevelActivity extends AppCompatActivity {

    ListView listViewCategories;
    String[] categories = {"Perros", "Gatos", "Otros"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);

        listViewCategories = findViewById(R.id.listViewCategories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories);
        listViewCategories.setAdapter(adapter);

        // Listener para detectar el clic en una categoría
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                Intent intent = new Intent(TopLevelActivity.this, AnimalCategoryActivity.class);
                intent.putExtra("CATEGORY", selectedCategory);
                startActivity(intent);
            }
        });
    }
}

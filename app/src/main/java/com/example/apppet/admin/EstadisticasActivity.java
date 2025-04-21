package com.example.apppet.admin;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.apppet.R;
import com.example.apppet.adapters.EstadisticasPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EstadisticasActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private EstadisticasPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        tabLayout = findViewById(R.id.tabLayoutEstadisticas);
        viewPager = findViewById(R.id.viewPagerEstadisticas);

        pagerAdapter = new EstadisticasPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Usuarios por Rol");
                    break;
                case 1:
                    tab.setText("Mascotas por Especie");
                    break;
                case 2:
                    tab.setText("Más registros de peso");
                    break;
                case 3:
                    tab.setText("Top Veterinarios");
                    break;
            }
        }).attach();
    }
}

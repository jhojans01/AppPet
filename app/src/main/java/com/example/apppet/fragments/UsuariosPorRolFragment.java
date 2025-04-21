package com.example.apppet.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.apppet.R;
import com.example.apppet.models.UserRoleCount;
import com.example.apppet.network.EstadisticasService;
import com.example.apppet.network.RetrofitClient;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosPorRolFragment extends Fragment {

    private PieChart pieChartUsuarios;
    private EstadisticasService estadisticasService;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios_por_rol, container, false);
        pieChartUsuarios = view.findViewById(R.id.pieChartUsuarios);
        estadisticasService = RetrofitClient.getRetrofitInstance().create(EstadisticasService.class);
        obtenerDatosUsuariosPorRol();
        return view;
    }

    private void obtenerDatosUsuariosPorRol() {
        estadisticasService.getCantidadUsuariosPorRol().enqueue(new Callback<List<UserRoleCount>>() {
            @Override
            public void onResponse(Call<List<UserRoleCount>> call, Response<List<UserRoleCount>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    configurarPieChart(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserRoleCount>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarPieChart(List<UserRoleCount> datos) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colores = new ArrayList<>();

        for (UserRoleCount item : datos) {
            entries.add(new PieEntry(item.getCantidad(), capitalizar(item.getRol())));
            colores.add(colorPorRol(item.getRol()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colores);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Muestra solo números enteros
            }
        });

        pieChartUsuarios.setData(pieData);
        pieChartUsuarios.setUsePercentValues(false);
        pieChartUsuarios.getDescription().setEnabled(false);
        pieChartUsuarios.setCenterText("Usuarios por rol");
        pieChartUsuarios.setCenterTextSize(14f);
        pieChartUsuarios.setDrawHoleEnabled(true);
        pieChartUsuarios.setHoleRadius(60f);
        pieChartUsuarios.setTransparentCircleRadius(65f);

        Legend legend = pieChartUsuarios.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        pieChartUsuarios.invalidate(); // Refrescar
    }

    private int colorPorRol(String rol) {
        switch (rol.toLowerCase()) {
            case "propietario":
                return Color.parseColor("#64B5F6");
            case "veterinario":
                return Color.parseColor("#81C784");
            case "admin":
                return Color.parseColor("#F48FB1");
            case "cuidador":
                return Color.parseColor("#FFD54F");
            default:
                return Color.GRAY;
        }
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}

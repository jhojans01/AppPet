package com.example.apppet.fragments;

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
import com.example.apppet.models.VeterinarioCount;
import com.example.apppet.network.EstadisticasService;
import com.example.apppet.network.RetrofitClient;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopVeterinariosFragment extends Fragment {

    private HorizontalBarChart barChartVet;
    private EstadisticasService statsService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_veterinarios, container, false);
        barChartVet = view.findViewById(R.id.barChartVeterinarios);
        statsService = RetrofitClient.getRetrofitInstance().create(EstadisticasService.class);
        cargarTopVeterinarios();
        return view;
    }

    private void cargarTopVeterinarios() {
        statsService.getTopVeterinarios().enqueue(new Callback<List<VeterinarioCount>>() {
            @Override
            public void onResponse(Call<List<VeterinarioCount>> call, Response<List<VeterinarioCount>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    configurarBarChart(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<VeterinarioCount>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarBarChart(List<VeterinarioCount> datos) {
        List<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();

        for (int i = 0; i < datos.size(); i++) {
            // La Y es el número de mascotas, X la posición
            entries.add(new BarEntry(i, datos.get(i).getCantidad()));
            labels.add(datos.get(i).getNombre());
        }

        BarDataSet set = new BarDataSet(entries, "Mascotas por Veterinario");
        set.setColor(Color.parseColor("#81C784"));      // Verde suave
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(12f);
        set.setDrawValues(true);
        // Formatear como entero
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        BarData data = new BarData(set);
        data.setBarWidth(0.6f);  // Grosor de las barras
        barChartVet.setData(data);

        // Eje X con etiquetas de veterinarios
        XAxis xAxis = barChartVet.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                return idx >= 0 && idx < labels.size() ? labels.get(idx) : "";
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setDrawGridLines(false);

        // Ejes Y
        YAxis left = barChartVet.getAxisLeft();
        left.setAxisMinimum(0);
        left.setDrawGridLines(false);
        barChartVet.getAxisRight().setEnabled(false);

        // Descripciones y ajustes
        barChartVet.getDescription().setEnabled(false);
        barChartVet.setFitBars(true);
        barChartVet.invalidate(); // repaint
    }
}

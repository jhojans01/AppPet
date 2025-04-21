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
import com.example.apppet.models.EspecieCount;
import com.example.apppet.network.EstadisticasService;
import com.example.apppet.network.RetrofitClient;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MascotasPorEspecieFragment extends Fragment {

    private HorizontalBarChart barChartEspecies;
    private EstadisticasService estadisticasService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mascotas_por_especie, container, false);

        barChartEspecies = view.findViewById(R.id.barChartEspecies);
        estadisticasService = RetrofitClient.getRetrofitInstance().create(EstadisticasService.class);

        obtenerMascotasPorEspecie();

        return view;
    }

    private void obtenerMascotasPorEspecie() {
        estadisticasService.contarMascotasPorEspecie().enqueue(new Callback<List<EspecieCount>>() {
            @Override
            public void onResponse(Call<List<EspecieCount>> call, Response<List<EspecieCount>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    configurarBarChartEspecies(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al obtener especies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EspecieCount>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarBarChartEspecies(List<EspecieCount> datos) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < datos.size(); i++) {
            entries.add(new BarEntry(i, datos.get(i).getCantidad()));
            labels.add(datos.get(i).getEspecie());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Especies");
        dataSet.setColor(Color.parseColor("#4DD0E1")); // Color Cyan claro
        dataSet.setValueTextSize(12f);
        BarData barData = new BarData(dataSet);

        barChartEspecies.setData(barData);

        XAxis xAxis = barChartEspecies.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        barChartEspecies.getAxisLeft().setAxisMinimum(0);
        barChartEspecies.getAxisRight().setEnabled(false);
        barChartEspecies.getDescription().setEnabled(false);
        barChartEspecies.setFitBars(true);
        barChartEspecies.invalidate(); // Refrescar
    }
}

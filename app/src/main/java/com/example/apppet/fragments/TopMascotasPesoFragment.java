package com.example.apppet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.adapters.TopPesoMascotaAdapter;
import com.example.apppet.models.PesoRanking;
import com.example.apppet.network.EstadisticasService;
import com.example.apppet.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopMascotasPesoFragment extends Fragment {

    private RecyclerView recyclerView;
    private TopPesoMascotaAdapter adapter;
    private EstadisticasService estadisticasService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_peso, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewTopPeso);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        estadisticasService = RetrofitClient
                .getRetrofitInstance()
                .create(EstadisticasService.class);

        cargarTopMascotasPeso();
        return view;
    }

    private void cargarTopMascotasPeso() {
        estadisticasService.getTopMascotasPeso().enqueue(new Callback<List<PesoRanking>>() {
            @Override
            public void onResponse(@NonNull Call<List<PesoRanking>> call,
                                   @NonNull Response<List<PesoRanking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new TopPesoMascotaAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(),
                            "Error al cargar top peso", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<PesoRanking>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),
                        "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

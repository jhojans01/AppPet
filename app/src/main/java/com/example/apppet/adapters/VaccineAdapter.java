package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Vaccine;

import java.util.List;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.ViewHolder> {

    public interface OnVaccineActionListener {
        void onEditVaccine(Vaccine vaccine);
        void onDeleteVaccine(Vaccine vaccine);
    }

    private List<Vaccine> vaccineList;
    private OnVaccineActionListener listener;

    public VaccineAdapter(List<Vaccine> vaccineList, OnVaccineActionListener listener) {
        this.vaccineList = vaccineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VaccineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vaccine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineAdapter.ViewHolder holder, int position) {
        Vaccine vaccine = vaccineList.get(position);

        // Ej: "Rabia (Falta)" o "Moquillo (Aplicada)"
        holder.tvVaccineName.setText(vaccine.getNombreVacuna() + " (" + vaccine.getEstadoVacuna() + ")");
        holder.tvVaccineDate.setText("Fecha: " + vaccine.getFechaAplicacion());

        holder.btnEditVaccine.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditVaccine(vaccine);
            }
        });

        holder.btnDeleteVaccine.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteVaccine(vaccine);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vaccineList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineName, tvVaccineDate;
        Button btnEditVaccine, btnDeleteVaccine;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVaccineName = itemView.findViewById(R.id.tvVaccineName);
            tvVaccineDate = itemView.findViewById(R.id.tvVaccineDate);
            btnEditVaccine = itemView.findViewById(R.id.btnEditVaccine);
            btnDeleteVaccine = itemView.findViewById(R.id.btnDeleteVaccine);
        }
    }
}


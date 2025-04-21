package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Weight;

import java.util.List;

public class WeightAdapterDiet extends RecyclerView.Adapter<WeightAdapterDiet.ViewHolder> {

    public interface OnWeightItemClickListener {
        void onAssignDietClick(Weight weight);
    }

    private List<Weight> weightList;
    private OnWeightItemClickListener listener;

    public WeightAdapterDiet(List<Weight> weightList, OnWeightItemClickListener listener) {
        this.weightList = weightList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight_for_diet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weight w = weightList.get(position);

        holder.tvPeso.setText("Peso: " + w.getPeso());
        holder.tvFecha.setText("Fecha: " + w.getFechaRegistro());
        holder.tvMascota.setText("Mascota: " + w.getPetName());
        holder.tvPropietario.setText("Propietario: " + w.getOwnerName());

        if (w.getTieneDieta()) {
            holder.btnAsignar.setVisibility(View.GONE);
            holder.tvEstadoDieta.setVisibility(View.VISIBLE);
        } else {
            holder.btnAsignar.setVisibility(View.VISIBLE);
            holder.tvEstadoDieta.setVisibility(View.GONE);
            holder.btnAsignar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAssignDietClick(w);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return weightList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPeso, tvFecha, tvMascota, tvPropietario, tvEstadoDieta;
        Button btnAsignar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPeso = itemView.findViewById(R.id.tvPesoForDiet);
            tvFecha = itemView.findViewById(R.id.tvFechaForDiet);
            tvMascota = itemView.findViewById(R.id.tvMascota);
            tvPropietario = itemView.findViewById(R.id.tvPropietario);
            tvEstadoDieta = itemView.findViewById(R.id.tvEstadoDieta);
            btnAsignar = itemView.findViewById(R.id.btnAsignarDieta);
        }
    }
}

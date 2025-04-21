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

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.ViewHolder> {

    public interface OnWeightActionListener {
        // Puedes agregar opciones de edición o eliminación si se requieren.
        void onEditWeight(Weight weight);
        void onDeleteWeight(Weight weight);
    }

    private List<Weight> weightList;
    private OnWeightActionListener listener;

    public WeightAdapter(List<Weight> weightList, OnWeightActionListener listener) {
        this.weightList = weightList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightAdapter.ViewHolder holder, int position) {
        Weight weight = weightList.get(position);
        holder.tvPesoItem.setText("Peso: " + weight.getPeso() + " kg");
        holder.tvFechaItem.setText("Fecha: " + weight.getFechaRegistro());

        // Escuchar clic en "Editar"
        holder.btnEditWeight.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditWeight(weight);
            }
        });

        // Escuchar clic en "Eliminar"
        holder.btnDeleteWeight.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteWeight(weight);
            }
        });
    }


    @Override
    public int getItemCount() {
        return weightList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPesoItem, tvFechaItem;
        Button btnEditWeight, btnDeleteWeight;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPesoItem = itemView.findViewById(R.id.tvPesoItem);
            tvFechaItem = itemView.findViewById(R.id.tvFechaItem);
            btnEditWeight = itemView.findViewById(R.id.btnEditWeight);
            btnDeleteWeight = itemView.findViewById(R.id.btnDeleteWeight);
        }
    }

}

package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Behavior;

import java.util.List;

public class BehaviorAdapter extends RecyclerView.Adapter<BehaviorAdapter.ViewHolder> {

    public interface OnBehaviorActionListener {
        void onEditBehavior(Behavior behavior);
        void onDeleteBehavior(Behavior behavior);
    }

    private List<Behavior> behaviorList;
    private OnBehaviorActionListener listener;

    public BehaviorAdapter(List<Behavior> behaviorList, OnBehaviorActionListener listener) {
        this.behaviorList = behaviorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BehaviorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_behavior, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BehaviorAdapter.ViewHolder holder, int position) {
        Behavior behavior = behaviorList.get(position);
        holder.tvComportamiento.setText("Comportamiento: " + behavior.getComportamiento());
        holder.tvFechaHora.setText("Fecha/Hora: " + behavior.getFechaHora());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditBehavior(behavior);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteBehavior(behavior);
            }
        });
    }

    @Override
    public int getItemCount() {
        return behaviorList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComportamiento, tvFechaHora;
        Button btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComportamiento = itemView.findViewById(R.id.tvComportamientoItem);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHoraItem);
            btnEdit = itemView.findViewById(R.id.btnEditBehavior);
            btnDelete = itemView.findViewById(R.id.btnDeleteBehavior);
        }
    }
}


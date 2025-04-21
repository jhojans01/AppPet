package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Visit;

import java.util.List;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.ViewHolder> {

    public interface OnVisitActionListener {
        void onEditVisit(Visit visit);
        void onDeleteVisit(Visit visit);
    }

    private List<Visit> visitList;
    private OnVisitActionListener listener;

    public VisitAdapter(List<Visit> visitList, OnVisitActionListener listener) {
        this.visitList = visitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VisitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitAdapter.ViewHolder holder, int position) {
        Visit visit = visitList.get(position);
        holder.tvFecha.setText("Fecha: " + visit.getFecha());
        holder.tvMotivo.setText("Motivo: " + visit.getMotivo());
        holder.tvObservaciones.setText("Observaciones: " + visit.getObservaciones());

        holder.btnEditVisit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditVisit(visit);
            }
        });

        holder.btnDeleteVisit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteVisit(visit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvMotivo, tvObservaciones;
        Button btnEditVisit, btnDeleteVisit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaVisit);
            tvMotivo = itemView.findViewById(R.id.tvMotivoVisit);
            tvObservaciones = itemView.findViewById(R.id.tvObservacionesVisit);
            btnEditVisit = itemView.findViewById(R.id.btnEditVisit);
            btnDeleteVisit = itemView.findViewById(R.id.btnDeleteVisit);
        }
    }
}

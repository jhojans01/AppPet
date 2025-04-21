package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Actividad;

import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<Actividad> listaActividades;

    public ActividadAdapter(List<Actividad> listaActividades) {
        this.listaActividades = listaActividades;
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad_fisica, parent, false);
        return new ActividadViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        Actividad actividad = listaActividades.get(position);
        holder.tvTipo.setText("🏃 Actividad: " + actividad.getTipo_actividad());
        holder.tvDuracion.setText("⏱ Duración: " + actividad.getDuracion() + " min");
        holder.tvFecha.setText("📅 Fecha: " + actividad.getFecha());
    }

    @Override
    public int getItemCount() {
        return listaActividades.size();
    }

    static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvDuracion, tvFecha;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tvTipoActividad);
            tvDuracion = itemView.findViewById(R.id.tvDuracionActividad);
            tvFecha = itemView.findViewById(R.id.tvFechaActividad);
        }
    }
}

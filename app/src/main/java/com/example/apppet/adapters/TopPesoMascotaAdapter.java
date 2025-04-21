package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.PesoRanking;

import java.util.List;

public class TopPesoMascotaAdapter extends RecyclerView.Adapter<TopPesoMascotaAdapter.ViewHolder> {

    private final List<PesoRanking> lista;

    public TopPesoMascotaAdapter(List<PesoRanking> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public TopPesoMascotaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_peso, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPesoMascotaAdapter.ViewHolder holder, int position) {
        PesoRanking item = lista.get(position);
        holder.tvNombre.setText(item.getNombreMascota());
        holder.tvCantidad.setText("Registros de peso: " + item.getCantidad());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMascota);
            tvCantidad = itemView.findViewById(R.id.tvCantidadPesos);
        }
    }
}

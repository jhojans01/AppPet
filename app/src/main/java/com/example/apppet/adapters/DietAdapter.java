package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Diet;

import java.util.List;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.DietViewHolder> {

    private List<Diet> lista;

    public DietAdapter(List<Diet> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public DietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet, parent, false);
        return new DietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DietViewHolder holder, int position) {
        Diet dieta = lista.get(position);

        holder.tvPetId.setText("ID Mascota: " + dieta.getPetId());
        holder.tvDieta.setText("📅 Fecha: " + dieta.getFechaRegistro());
        holder.tvTipoComida.setText("🍖 Tipo: " + dieta.getTipoComida());
        holder.tvCantidad.setText("🍽️ Cantidad: " + dieta.getCantidad());
        holder.tvHora.setText("⏰ Hora: " + dieta.getHora());
        holder.tvObservaciones.setText("📝 Obs: " + dieta.getObservaciones());
        holder.tvPesoRelacionado.setText("⚖ Peso relacionado: " + dieta.getPesoValor() + " kg");

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class DietViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetId, tvDieta, tvTipoComida, tvCantidad, tvHora, tvObservaciones, tvPesoRelacionado;

        public DietViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPetId = itemView.findViewById(R.id.tvPetId);
            tvDieta = itemView.findViewById(R.id.tvDieta);
            tvTipoComida = itemView.findViewById(R.id.tvTipoComida);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones);
            tvPesoRelacionado = itemView.findViewById(R.id.tvPesoRelacionado);
        }
    }
}

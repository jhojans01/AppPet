package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Medicamento;

import java.util.List;

public class DiagnosticoAdapter extends RecyclerView.Adapter<DiagnosticoAdapter.ViewHolder> {
    private List<Medicamento> diagnosticos;

    public DiagnosticoAdapter(List<Medicamento> diagnosticos) {
        this.diagnosticos = diagnosticos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diagnostico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicamento m = diagnosticos.get(position);
        holder.tvEnfermedad.setText("🦠 Enfermedad: " + m.getEnfermedad());
        holder.tvMedicamento.setText("💊 Medicamento: " + m.getMedicamento());
        holder.tvVia.setText("🧪 Vía: " + m.getVia());
        holder.tvHora.setText("⏰ Hora: " + m.getHora());
        holder.tvObservaciones.setText("📝 Observaciones: " + m.getObservaciones());
    }

    @Override
    public int getItemCount() {
        return diagnosticos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEnfermedad, tvMedicamento, tvVia, tvHora, tvObservaciones;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEnfermedad = itemView.findViewById(R.id.tvEnfermedad);
            tvMedicamento = itemView.findViewById(R.id.tvMedicamento);
            tvVia = itemView.findViewById(R.id.tvVia);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones);
        }
    }
}


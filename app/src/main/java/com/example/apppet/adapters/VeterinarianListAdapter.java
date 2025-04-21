package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Usuario;

import java.util.List;

public class VeterinarianListAdapter extends RecyclerView.Adapter<VeterinarianListAdapter.ViewHolder> {

    public interface OnVeterinarioClickListener {
        void onVeterinarioClick(Usuario vet);
    }

    private List<Usuario> vets;
    private OnVeterinarioClickListener listener;

    public VeterinarianListAdapter(List<Usuario> vets, OnVeterinarioClickListener listener) {
        this.vets = vets;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_veterinario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Usuario vet = vets.get(position);
        holder.tvNombre.setText(vet.getNombre());
        holder.itemView.setOnClickListener(v -> listener.onVeterinarioClick(vet));
    }

    @Override
    public int getItemCount() {
        return vets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreVet);
        }
    }
}

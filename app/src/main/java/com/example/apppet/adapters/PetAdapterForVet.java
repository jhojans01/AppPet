package com.example.apppet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Pet;

import java.util.List;

public class PetAdapterForVet extends RecyclerView.Adapter<PetAdapterForVet.PetViewHolder> {

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    private List<Pet> petList;
    private OnPetClickListener listener;

    public PetAdapterForVet(List<Pet> petList, OnPetClickListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_for_vet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.tvNombre.setText(pet.getNombre());
        holder.tvRaza.setText("Raza: " + pet.getRaza());
        holder.tvEspecie.setText("Especie: " + pet.getEspecie());
        holder.tvEdad.setText("Edad: " + pet.getEdad());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPetClick(pet);
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRaza, tvEspecie, tvEdad;

        PetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMascota);
            tvRaza = itemView.findViewById(R.id.tvRazaMascota);
            tvEspecie = itemView.findViewById(R.id.tvEspecieMascota);
            tvEdad = itemView.findViewById(R.id.tvEdadMascota);
        }
    }
}

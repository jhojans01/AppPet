package com.example.apppet.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Pet;

import java.util.List;

public class PetPendienteAdapter extends RecyclerView.Adapter<PetPendienteAdapter.ViewHolder> {

    public interface OnPetDecisionListener {
        void onAceptar(Pet pet);
        void onRechazar(Pet pet);
    }

    private List<Pet> petList;
    private final OnPetDecisionListener listener;

    public PetPendienteAdapter(List<Pet> petList, OnPetDecisionListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet_pendiente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pet pet = petList.get(position);

        // Asigna los datos al layout
        holder.tvNombre.setText(pet.getNombre());
        holder.tvEspecie.setText("Especie: " + pet.getEspecie());
        holder.tvRaza.setText("Raza: " + pet.getRaza());
        holder.tvEdad.setText("Edad: " + pet.getEdad());

        // Decodifica la imagen si existe
        if (pet.getImageBase64() != null && !pet.getImageBase64().isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(pet.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.ivPet.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                holder.ivPet.setImageResource(R.drawable.ic_pet_placeholder); // Por si hay error al decodificar
            }
        } else {
            holder.ivPet.setImageResource(R.drawable.ic_pet_placeholder);
        }

        // Botones de acción
        holder.btnAceptar.setOnClickListener(v -> listener.onAceptar(pet));
        holder.btnRechazar.setOnClickListener(v -> listener.onRechazar(pet));
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPet;
        TextView tvNombre, tvEspecie, tvRaza, tvEdad;
        Button btnAceptar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPet = itemView.findViewById(R.id.ivPetPendiente);
            tvNombre = itemView.findViewById(R.id.tvNombrePendiente);
            tvEspecie = itemView.findViewById(R.id.tvEspeciePendiente);
            tvRaza = itemView.findViewById(R.id.tvRazaPendiente);
            tvEdad = itemView.findViewById(R.id.tvEdadPendiente);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}

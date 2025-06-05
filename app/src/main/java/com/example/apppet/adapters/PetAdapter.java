package com.example.apppet.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.R;
import com.example.apppet.models.Pet;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    public interface OnPetActionListener {
        void onEditPet(Pet pet);
        void onDeletePet(Pet pet);
        void onViewPetDetail(Pet pet);
    }

    private List<Pet> petList;
    private OnPetActionListener listener;
    private String userRole;

    public PetAdapter(List<Pet> petList, String userRole, OnPetActionListener listener) {
        this.petList = petList;
        this.userRole = userRole;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);

        holder.tvName.setText(pet.getNombre());
        holder.tvSpecies.setText("Especie: " + pet.getEspecie());
        holder.tvBreed.setText("Raza: " + pet.getRaza());
        holder.tvAge.setText("Edad: " + pet.getEdad());

        // Decodificar imagen en Base64
        String base64 = pet.getImageBase64();
        if (base64 != null && !base64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.ivPetImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                holder.ivPetImage.setImageResource(R.drawable.ic_pet_placeholder);
            }
        } else {
            holder.ivPetImage.setImageResource(R.drawable.ic_pet_placeholder);
        }

        // Mostrar u ocultar botones según el rol
        if (userRole.equalsIgnoreCase("cuidador")) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        // Listeners
        holder.btnEdit.setOnClickListener(v -> listener.onEditPet(pet));
        holder.btnDelete.setOnClickListener(v -> listener.onDeletePet(pet));
        holder.itemView.setOnClickListener(v -> listener.onViewPetDetail(pet));
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public void setPetList(List<Pet> newPetList) {
        this.petList = newPetList;
        notifyDataSetChanged();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPetImage;
        TextView tvName, tvSpecies, tvBreed, tvAge;
        ImageButton btnEdit, btnDelete;


        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPetImage = itemView.findViewById(R.id.ivPetImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvSpecies = itemView.findViewById(R.id.tvSpecies);
            tvBreed = itemView.findViewById(R.id.tvBreed);
            tvAge = itemView.findViewById(R.id.tvAge);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}



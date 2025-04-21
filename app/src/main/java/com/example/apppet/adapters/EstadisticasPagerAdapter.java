package com.example.apppet.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.apppet.fragments.TopMascotasPesoFragment;
import com.example.apppet.fragments.MascotasPorEspecieFragment;
import com.example.apppet.fragments.TopVeterinariosFragment;
import com.example.apppet.fragments.UsuariosPorRolFragment;

public class EstadisticasPagerAdapter extends FragmentStateAdapter {

    public EstadisticasPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UsuariosPorRolFragment();
            case 1:
                return new MascotasPorEspecieFragment();
            case 2:
                return new TopMascotasPesoFragment();
            case 3:
                return new TopVeterinariosFragment();
            default:
                return new UsuariosPorRolFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Total de tabs
    }
}

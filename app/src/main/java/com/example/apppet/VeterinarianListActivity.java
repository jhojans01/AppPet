package com.example.apppet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.VeterinarianListAdapter;
import com.example.apppet.models.Usuario;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VeterinarianListActivity extends AppCompatActivity {

    private RecyclerView rvVeterinarios;
    private VeterinarianListAdapter adapter;
    private List<Usuario> veterinarios = new ArrayList<>();
    private int propietarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veterinario_list);

        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        propietarioId = prefs.getInt("user_id", 0);

        rvVeterinarios = findViewById(R.id.rvVeterinarios);
        rvVeterinarios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VeterinarianListAdapter(veterinarios, vet -> {
            Intent intent = new Intent(VeterinarianListActivity.this, ChatActivity.class);
            intent.putExtra("loggedUserId", propietarioId);
            intent.putExtra("otherUserId", vet.getId());
            startActivity(intent);
        });
        rvVeterinarios.setAdapter(adapter);

        cargarVeterinarios();
    }


    private void cargarVeterinarios() {
        UsuarioService service = RetrofitClient.getRetrofitInstance().create(UsuarioService.class);
        service.getVeterinarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    veterinarios.clear();
                    veterinarios.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VeterinarianListActivity.this, "Error al cargar veterinarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(VeterinarianListActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.apppet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import android.util.Log;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.adapters.DiagnosticoAdapter;
import com.example.apppet.adapters.DietAdapter;
import com.example.apppet.models.Collar;
import com.example.apppet.models.Diet;
import com.example.apppet.models.Medicamento;
import com.example.apppet.models.Pet;
import com.example.apppet.models.Usuario;
import com.example.apppet.network.CollarService;
import com.example.apppet.network.DietService;
import com.example.apppet.network.MascotaService;
import com.example.apppet.network.MedicamentoService;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.UsuarioService;

import com.example.apppet.models.Actividad;
import com.example.apppet.network.ActividadService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.Nullable;


public class PetDetailActivity extends AppCompatActivity {

    private ImageView ivPetImage;
    private TextView tvDetailName, tvDetailSpecies, tvDetailBreed, tvDetailAge, tvCollarId;
    private Button btnViewVaccines, btnControlPeso, btnBehavior, btnVisitasVeterinario,
            btnVerDieta, btnAsignarCollar, btnVerCollar, btnElegirVet, btnElegirCuidador, btnVerDiagnostico,btnVerUltimaActividad;

    private int petId;
    private TextView tvUltimaActividad;
    private ActividadService actividadService;

    private Pet pet;

    private int collarIdNumerico = 0;
    private String identificadorCollar = null;

    private DietService dietService;
    private CollarService collarService;
    private UsuarioService usuarioService;
    private MascotaService mascotaService;
    private MedicamentoService medicamentoService;


    private int vetIdActual = 0;
    private int cuidadorIdActual = 0;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        ivPetImage = findViewById(R.id.ivPetImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailSpecies = findViewById(R.id.tvDetailSpecies);
        tvDetailBreed = findViewById(R.id.tvDetailBreed);
        tvDetailAge = findViewById(R.id.tvDetailAge);
        tvCollarId = findViewById(R.id.tvCollarId);
        TextView tvVeterinarioAsignado = findViewById(R.id.tvVeterinarioAsignado);
        TextView tvCuidadorAsignado = findViewById(R.id.tvCuidadorAsignado);



        btnViewVaccines = findViewById(R.id.btnViewVaccines);
        btnControlPeso = findViewById(R.id.btnControlPeso);
        btnBehavior = findViewById(R.id.btnBehavior);
        btnVerUltimaActividad = findViewById(R.id.btnVerUltimaActividad);
        btnVisitasVeterinario = findViewById(R.id.btnVisitasVeterinario);
        btnVerDieta = findViewById(R.id.btnVerDieta);
        btnAsignarCollar = findViewById(R.id.btnAsignarCollar);
        btnVerCollar = findViewById(R.id.btnVerCollar);
        btnElegirVet = findViewById(R.id.btnElegirVet);
        btnVerDiagnostico = findViewById(R.id.btnVerDiagnostico);
        btnElegirCuidador = findViewById(R.id.btnElegirCuidador);
        Button btnChatVet = findViewById(R.id.btnChatVet);
        Button btnChatCuidador = findViewById(R.id.btnChatCuidador);

        dietService = RetrofitClient.getRetrofitInstance().create(DietService.class);
        collarService = RetrofitClient.getRetrofitInstance().create(CollarService.class);
        usuarioService = RetrofitClient.getRetrofitInstance().create(UsuarioService.class);
        mascotaService = RetrofitClient.getRetrofitInstance().create(MascotaService.class);
        medicamentoService = RetrofitClient.getRetrofitInstance().create(MedicamentoService.class);
        actividadService = RetrofitClient.getRetrofitInstance().create(ActividadService.class);


        if (getIntent() != null) {
            petId = getIntent().getIntExtra("pet_id", 0);
            cargarMascotaDesdeServidor();
        }

        obtenerCollar();

        btnViewVaccines.setOnClickListener(v -> abrir(VaccinesActivity.class));
        btnControlPeso.setOnClickListener(v -> abrir(WeightActivity.class));
        btnBehavior.setOnClickListener(v -> abrir(BehaviorActivity.class));
        btnVisitasVeterinario.setOnClickListener(v -> abrir(VisitsActivity.class));
        btnVerDieta.setOnClickListener(v -> dietService.getDietasByPetId(petId).enqueue(new Callback<List<Diet>>() {

            @Override
            public void onResponse(Call<List<Diet>> call, Response<List<Diet>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_all_diets, null);
                    RecyclerView rv = dialogView.findViewById(R.id.recyclerViewDietas);
                    rv.setLayoutManager(new LinearLayoutManager(PetDetailActivity.this));
                    rv.setAdapter(new DietAdapter(response.body()));
                    new AlertDialog.Builder(PetDetailActivity.this).setTitle("\uD83C\uDF7D️ Dietas asignadas").setView(dialogView).setPositiveButton("OK", null).show();
                } else {
                    Toast.makeText(PetDetailActivity.this, "No hay dietas registradas", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Diet>> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Error al obtener dietas", Toast.LENGTH_SHORT).show();
            }
        }));

        btnChatVet.setOnClickListener(v -> {
            if (vetIdActual > 0) {
                Intent i = new Intent(PetDetailActivity.this, ChatActivity.class);
                i.putExtra("loggedUserId", pet.getUserId());  // propietario
                i.putExtra("otherUserId", vetIdActual);       // veterinario asignado
                startActivity(i);
            } else {
                Toast.makeText(this, "No hay veterinario asignado", Toast.LENGTH_SHORT).show();
            }
        });

        btnChatCuidador.setOnClickListener(v -> {
            if (cuidadorIdActual > 0) {
                Intent i = new Intent(PetDetailActivity.this, ChatActivity.class);
                i.putExtra("loggedUserId", pet.getUserId());      // propietario
                i.putExtra("otherUserId", cuidadorIdActual);      // cuidador asignado
                startActivity(i);
            } else {
                Toast.makeText(this, "No hay cuidador asignado", Toast.LENGTH_SHORT).show();
            }
        });

        btnVerCollar.setOnClickListener(v -> {
            if (collarIdNumerico > 0) {
                Intent intent = new Intent(PetDetailActivity.this, MapActivity.class);
                intent.putExtra("collar_id", collarIdNumerico);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Esta mascota no tiene un collar asignado.", Toast.LENGTH_SHORT).show();
            }
        });

        btnAsignarCollar.setOnClickListener(v -> mostrarDialogoAsignarCollar());
        btnVerUltimaActividad.setOnClickListener(v -> mostrarUltimaActividad());
        btnElegirVet.setOnClickListener(v -> mostrarDialogoElegirVet());
        btnElegirCuidador.setOnClickListener(v -> mostrarDialogoElegirCuidador());
        btnVerDiagnostico.setOnClickListener(v -> medicamentoService.getMedicamentosByPetId(petId).enqueue(new Callback<List<Medicamento>>() {



            @Override
            public void onResponse(Call<List<Medicamento>> call, Response<List<Medicamento>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    View view = getLayoutInflater().inflate(R.layout.dialog_diagnostico, null);
                    RecyclerView rv = view.findViewById(R.id.rvDiagnosticos);
                    rv.setLayoutManager(new LinearLayoutManager(PetDetailActivity.this));
                    rv.setAdapter(new DiagnosticoAdapter(response.body()));
                    new AlertDialog.Builder(PetDetailActivity.this).setTitle("\uD83E\uDDEA Diagnósticos").setView(view).setPositiveButton("Cerrar", null).show();
                } else {
                    Toast.makeText(PetDetailActivity.this, "No hay diagnósticos registrados", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Medicamento>> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Error al obtener diagnóstico", Toast.LENGTH_SHORT).show();
            }
        }));
    }


    private void cargarMascotaDesdeServidor() {
        mascotaService.getPetById(petId).enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pet = response.body();

                    vetIdActual = pet.getVetId();
                    cuidadorIdActual = pet.getCuidadorId();

                    tvDetailName.setText(pet.getNombre());
                    tvDetailSpecies.setText("Especie: " + pet.getEspecie());
                    tvDetailBreed.setText("Raza: " + pet.getRaza());
                    tvDetailAge.setText("Edad: " + pet.getEdad());

                    if (pet.getImageBase64() != null && !pet.getImageBase64().equalsIgnoreCase("null")) {
                        byte[] imageBytes = Base64.decode(pet.getImageBase64(), Base64.DEFAULT);
                        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        ivPetImage.setImageBitmap(decodedImage);
                    } else {
                        ivPetImage.setImageResource(R.drawable.perritico);
                    }

                    // Veterinario asignado
                    TextView tvVeterinarioAsignado = findViewById(R.id.tvVeterinarioAsignado);
                    if (vetIdActual > 0) {
                        usuarioService.getUserById(vetIdActual).enqueue(new Callback<Usuario>() {
                            @Override
                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    tvVeterinarioAsignado.setText("Veterinario: " + response.body().getNombre());
                                } else {
                                    tvVeterinarioAsignado.setText("Veterinario: No asignado");
                                }
                            }

                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                tvVeterinarioAsignado.setText("Veterinario: Error");
                            }
                        });
                    } else {
                        tvVeterinarioAsignado.setText("Veterinario: No asignado");
                    }

                    // Cuidador asignado
                    TextView tvCuidadorAsignado = findViewById(R.id.tvCuidadorAsignado);
                    if (cuidadorIdActual > 0) {
                        usuarioService.getUserById(cuidadorIdActual).enqueue(new Callback<Usuario>() {
                            @Override
                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    tvCuidadorAsignado.setText("Cuidador: " + response.body().getNombre());
                                } else {
                                    tvCuidadorAsignado.setText("Cuidador: No asignado");
                                }
                            }

                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                tvCuidadorAsignado.setText("Cuidador: Error");
                            }
                        });
                    } else {
                        tvCuidadorAsignado.setText("Cuidador: No asignado");
                    }

                } else {
                    Toast.makeText(PetDetailActivity.this, "❌ Error al cargar datos de mascota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "❌ Fallo de conexión al cargar mascota", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void mostrarDialogoElegirCuidador() {
        usuarioService.getCuidadores().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> cuidadores = response.body();
                    String[] nombres = new String[cuidadores.size()];
                    for (int i = 0; i < cuidadores.size(); i++) {
                        nombres[i] = cuidadores.get(i).getNombre();
                    }

                    new AlertDialog.Builder(PetDetailActivity.this)
                            .setTitle("Selecciona un cuidador")
                            .setItems(nombres, (dialog, which) -> {
                                int cuidadorSeleccionado = cuidadores.get(which).getId();
                                // Aquí enviamos también el vet actual (si tiene)
                                actualizarCamposPet(cuidadorSeleccionado, null);
                            })
                            .show();
                } else {
                    Toast.makeText(PetDetailActivity.this, "Error al cargar cuidadores", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void completarCamposObligatorios(Pet pet) {
        if (pet.getNombre() == null) pet.setNombre("Sin nombre");
        if (pet.getEspecie() == null) pet.setEspecie("Sin especie");
        if (pet.getRaza() == null) pet.setRaza("Sin raza");
        if (pet.getEdad() == 0) pet.setEdad(1);
        if (pet.getImageBase64() == null) pet.setImageBase64("");
        if (pet.getUserId() == 0) pet.setUserId(1); // Cambia por el ID real si lo tienes
    }

    private void mostrarDialogoElegirVet() {
        usuarioService.getVeterinarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> vets = response.body();
                    String[] nombres = new String[vets.size()];
                    for (int i = 0; i < vets.size(); i++) {
                        nombres[i] = vets.get(i).getNombre();
                    }

                    new AlertDialog.Builder(PetDetailActivity.this)
                            .setTitle("Elige un veterinario")
                            .setItems(nombres, (dialog, which) -> {
                                int vetSeleccionado = vets.get(which).getId();
                                // Aquí enviamos también el cuidador actual (si tiene)
                                actualizarCamposPet(null, vetSeleccionado);
                            })
                            .show();
                } else {
                    Toast.makeText(PetDetailActivity.this, "Error al cargar veterinarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void actualizarCamposPet(@Nullable Integer nuevoCuidadorId,
                                     @Nullable Integer nuevoVetId) {

        mascotaService.getPetById(petId).enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PetDetailActivity.this,
                            "❌ Error al obtener mascota", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pet p = response.body();   // copia fresca del servidor

                /* ---- 1. Asignaciones solicitadas por el propietario ---- */
                if (nuevoCuidadorId != null) {
                    p.setCuidadorId(nuevoCuidadorId);

                    // CUANDO el dueño “propone” un cuidador la mascota pasa a pendiente
                    p.setEstado_asignacion("pendiente");
                    p.setPendiente(1);
                }

                if (nuevoVetId != null) {
                    p.setVetId(nuevoVetId);
                    // Aquí no cambiamos estado_asignacion: el veto del veterinario
                    // no influye en el flujo de “aceptación de cuidador”.
                }

                /* ---- 2. Rellenar campos que tu backend exige (no null) ---- */
                completarCamposObligatorios(p);

                Log.d("DEBUG_JSON_PET", new Gson().toJson(p));

                /* ---- 3. Enviar PUT al servidor ---- */
                mascotaService.updatePet(p).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> res) {
                        if (res.isSuccessful()) {
                            Toast.makeText(PetDetailActivity.this,
                                    "✅ Mascota actualizada", Toast.LENGTH_SHORT).show();
                            cargarMascotaDesdeServidor();     // refrescar datos en pantalla
                        } else {
                            Toast.makeText(PetDetailActivity.this,
                                    "❌ Error (código " + res.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(PetDetailActivity.this,
                                "❌ Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this,
                        "❌ Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void abrir(Class<?> activity) {
        Intent intent = new Intent(PetDetailActivity.this, activity);
        intent.putExtra("pet_id", petId);
        startActivity(intent);
    }

    private void mostrarDialogoAsignarCollar() {
        final EditText input = new EditText(this);
        input.setHint("Ingrese ID del collar");
        new AlertDialog.Builder(this)
                .setTitle("Asignar Collar")
                .setView(input)
                .setPositiveButton("Asignar", (dialog, which) -> {
                    String id = input.getText().toString().trim();
                    if (!id.isEmpty()) {
                        Collar c = new Collar();
                        c.setPetId(petId);
                        c.setIdentificador(id);
                        collarService.insertarCollar(c).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast.makeText(PetDetailActivity.this, "Collar asignado", Toast.LENGTH_SHORT).show();
                                obtenerCollar();
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(PetDetailActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void obtenerCollar() {
        collarService.obtenerCollarPorMascota(petId).enqueue(new Callback<Collar>() {
            @Override
            public void onResponse(Call<Collar> call, Response<Collar> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Collar c = response.body();
                    collarIdNumerico = c.getId();
                    identificadorCollar = c.getIdentificador();
                    tvCollarId.setText("Collar: " + identificadorCollar);
                } else {
                    tvCollarId.setText("Sin collar asignado");
                }
            }
            @Override
            public void onFailure(Call<Collar> call, Throwable t) {
                tvCollarId.setText("Error al consultar collar");
            }
        });
    }
    private void mostrarUltimaActividad() {
        actividadService.getUltimaActividad(petId).enqueue(new Callback<Actividad>() {
            @Override
            public void onResponse(Call<Actividad> call, Response<Actividad> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Actividad act = response.body();
                    String mensaje = "🏃 Actividad: " + act.getTipo_actividad()
                            + "\n⏱ Duración: " + act.getDuracion() + " min"
                            + "\n📅 Fecha: " + act.getFecha();

                    new AlertDialog.Builder(PetDetailActivity.this)
                            .setTitle("Última Actividad Física")
                            .setMessage(mensaje)
                            .setPositiveButton("Ver Historial Completo", (dialog, which) -> {
                                Intent i = new Intent(PetDetailActivity.this, HistorialActividadActivity.class);
                                i.putExtra("pet_id", petId);
                                startActivity(i);
                            })
                            .setNegativeButton("Cerrar", null)
                            .show();
                } else {
                    Toast.makeText(PetDetailActivity.this, "❌ Sin actividad registrada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actividad> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "❌ Error al obtener actividad", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

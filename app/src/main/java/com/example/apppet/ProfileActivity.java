package com.example.apppet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppet.models.EmailCheckResponse;
import com.example.apppet.models.Usuario;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.UsuarioService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etEmail, etPassword;
    private Button btnEditProfile;
    private SharedPreferences prefs;
    private UsuarioService usuarioService;
    private int userId;
    private boolean editMode = false; // false => modo lectura

    private static final String TAG = "ProfileActivity";
    private String currentEmail = ""; // Para comparar si el usuario cambió el correo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Referencias a las vistas
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // Obtener SharedPreferences y userId
        prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);

        // Instanciar el servicio
        usuarioService = RetrofitClient.getRetrofitInstance().create(UsuarioService.class);

        // Cargar perfil desde la BD
        loadUserProfile();

        // Listener del botón (Editar / Guardar)
        btnEditProfile.setOnClickListener(v -> {
            if (!editMode) {
                // Modo lectura => habilitar edición
                etNombre.setEnabled(true);
                etEmail.setEnabled(true);
                etPassword.setEnabled(true);
                btnEditProfile.setText("Guardar Cambios");
                editMode = true;
            } else {
                // Modo edición => validar y guardar
                String nuevoNombre = etNombre.getText().toString().trim();
                String nuevoEmail = etEmail.getText().toString().trim();
                String nuevoPassword = etPassword.getText().toString().trim();

                // Validar campos vacíos
                if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty() || nuevoPassword.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar formato de correo
                if (!Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                    Toast.makeText(ProfileActivity.this, "El correo no es válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar contraseña (8 chars, 1 mayús, 1 minús, 1 dígito)
                String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
                if (!nuevoPassword.matches(passwordPattern)) {
                    Toast.makeText(ProfileActivity.this,
                            "La contraseña debe tener al menos 8 caracteres, " +
                                    "una mayúscula, una minúscula y un dígito",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Verificar si se cambió el correo
                if (!nuevoEmail.equalsIgnoreCase(currentEmail)) {
                    // Revisar si el correo ya existe
                    usuarioService.checkEmail(nuevoEmail).enqueue(new Callback<EmailCheckResponse>() {
                        @Override
                        public void onResponse(Call<EmailCheckResponse> call, Response<EmailCheckResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                // Si isExists() == true => correo en uso
                                if (response.body().isExists()) {
                                    Toast.makeText(ProfileActivity.this, "El correo ya está en uso", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Correo no existe => actualizar
                                    updateProfile(nuevoNombre, nuevoEmail, nuevoPassword);
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error al verificar el correo", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<EmailCheckResponse> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // El correo no cambió => actualizar sin verificar
                    updateProfile(nuevoNombre, nuevoEmail, nuevoPassword);
                }
            }
        });
    }

    private void loadUserProfile() {
        usuarioService.getUserById(userId).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    // Mostrar datos
                    etNombre.setText(usuario.getNombre());
                    etEmail.setText(usuario.getEmail());
                    etPassword.setText(usuario.getPassword());
                    // Guardar el email actual para comparación
                    currentEmail = usuario.getEmail();
                    // Deshabilitar campos (modo lectura)
                    etNombre.setEnabled(false);
                    etEmail.setEnabled(false);
                    etPassword.setEnabled(false);
                    btnEditProfile.setText("Editar");
                    editMode = false;
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProfile(String nuevoNombre, String nuevoEmail, String nuevoPassword) {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(userId);
        usuarioActualizado.setNombre(nuevoNombre);
        usuarioActualizado.setEmail(nuevoEmail);
        usuarioActualizado.setPassword(nuevoPassword);

        usuarioService.updatePerfil(usuarioActualizado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    // Regresar a la actividad anterior
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

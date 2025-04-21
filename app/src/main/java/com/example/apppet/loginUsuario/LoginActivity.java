package com.example.apppet.loginUsuario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppet.ChatActivity;
import com.example.apppet.ChatListActivity;
import com.example.apppet.MainActivity;
import com.example.apppet.R;
import com.example.apppet.admin.AdminDashboardActivity;
import com.example.apppet.models.Usuario;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.UsuarioService;
import com.example.apppet.registroUsuario.SignupActivity;
import com.example.apppet.VeterinarioActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ImageView imgUserIcon;
    private TextInputEditText etUsuarioLogin;
    private TextInputEditText etPasswordLogin;
    private Button btnLogin;
    private Button btnIrRegistro;

    private UsuarioService usuarioService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias a las vistas
        imgUserIcon = findViewById(R.id.imgUserIcon);
        etUsuarioLogin = findViewById(R.id.etUsuarioLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        // Instancia del servicio Retrofit
        usuarioService = RetrofitClient.getRetrofitInstance().create(UsuarioService.class);

        // Botón Login
        btnLogin.setOnClickListener(view -> {
            String email = Objects.requireNonNull(etUsuarioLogin.getText()).toString().trim();
            String password = Objects.requireNonNull(etPasswordLogin.getText()).toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Botón para ir a la pantalla de Registro
        btnIrRegistro.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    /**
     * Lógica para validar credenciales en el servidor
     */
    private void loginUser(String email, String password) {
        Log.d(TAG, "Intentando iniciar sesión con: " + email);

        Usuario loginRequest = new Usuario();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        usuarioService.loginUser(loginRequest).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario userResponse = response.body();

                    if (userResponse.getId() == 0) {
                        Toast.makeText(LoginActivity.this, "Usuario no encontrado. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userRole = userResponse.getRol();
                    Log.d(TAG, "Rol obtenido: " + userRole);

                    SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("role", userRole);
                    editor.putInt("user_id", userResponse.getId());
                    editor.putString("nombre", userResponse.getNombre());
                    editor.putString("email", userResponse.getEmail());
                    editor.apply();

                    if ("admin".equalsIgnoreCase(userRole)) {
                        Toast.makeText(LoginActivity.this, "Bienvenido Admin", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                    } else if ("veterinario".equalsIgnoreCase(userRole)) {
                        Toast.makeText(LoginActivity.this, "Bienvenido Veterinario", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, VeterinarioActivity.class);
                        intent.putExtra("loggedUserId", userResponse.getId()); // Opcional, por si lo usas
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }


                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario no encontrado. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al iniciar sesión: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
            }
        });
    }
}

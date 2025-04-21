package com.example.apppet.admin;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppet.admin.EstadisticasActivity;
import com.example.apppet.R;
import com.example.apppet.models.EmailCheckResponse;
import com.example.apppet.models.Usuario;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.UsuarioService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Usuario> userList = new ArrayList<>();
    private UsuarioService usuarioService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        usuarioService = RetrofitClient.getRetrofitInstance().create(UsuarioService.class);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEditUser(Usuario user) {
                editarUsuario(user);
            }

            @Override
            public void onDeleteUser(Usuario user) {
                confirmarEliminacion(user);
            }
        });
        recyclerView.setAdapter(userAdapter);

        cargarUsuarios();

        FloatingActionButton fabAddUser = findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(v -> mostrarDialogoAgregarUsuario());
        FloatingActionButton fabEstadisticas = findViewById(R.id.fabEstadisticas);
        fabEstadisticas.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, EstadisticasActivity.class));
        });

    }

    private void cargarUsuarios() {
        usuarioService.getAllUsers().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(@NonNull Call<List<Usuario>> call, @NonNull Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Usuario>> call, @NonNull Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoAgregarUsuario() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user, null);

        // Referencias a los campos del diálogo
        TextInputEditText etNombre = dialogView.findViewById(R.id.etAddNombre);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etAddEmail);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etAddPassword);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinnerAddRole);

        // Configurar el Spinner con roles (incluyendo "cuidador", etc.)
        String[] roles = {"admin", "veterinario", "propietario", "cuidador"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Agregar Usuario")
                .setView(dialogView)
                .setPositiveButton("Agregar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String rol = spinnerRole.getSelectedItem().toString();

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(AdminDashboardActivity.this,
                        "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar formato del correo
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(AdminDashboardActivity.this,
                        "El correo ingresado no es válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que la contraseña cumpla el patrón:
            // Mínimo 8 caracteres, al menos una mayúscula, una minúscula y un dígito.
            String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(AdminDashboardActivity.this,
                        "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un dígito",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Si todo es correcto, insertar el usuario y cerrar el diálogo.
            insertarNuevoUsuario(nombre, email, password, rol);
            alertDialog.dismiss();
        });
    }


    private void insertarNuevoUsuario(String nombre, String email, String password, String rol) {
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setEmail(email);
        nuevo.setPassword(password);
        nuevo.setRol(rol);

        usuarioService.insertUser(nuevo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Usuario agregado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(AdminDashboardActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AdminDashboardActivity.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void editarUsuario(Usuario user) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);

        // Referencias a las vistas del diálogo
        TextInputEditText etNombre = dialogView.findViewById(R.id.etEditName);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEditEmail);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etEditPassword);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinnerEditRole);

        // Rellenar los campos con la información actual del usuario; dejamos la contraseña en blanco
        etNombre.setText(user.getNombre());
        etEmail.setText(user.getEmail());
        etPassword.setText("");  // Contraseña se deja vacía para actualizarla solo si se ingresa un valor

        // Configurar el Spinner con los roles disponibles
        String[] roles = {"admin", "veterinario", "propietario", "cuidador"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        int pos = adapter.getPosition(user.getRol());
        spinnerRole.setSelection(pos);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Editar Usuario")
                .setView(dialogView)
                .setPositiveButton("Actualizar", null) // Se sobrescribe luego
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevoEmail = etEmail.getText().toString().trim();
            String nuevaContraseña = etPassword.getText().toString().trim();
            String nuevoRol = spinnerRole.getSelectedItem().toString();

            // Validar que nombre y correo no estén vacíos
            if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty()) {
                Toast.makeText(AdminDashboardActivity.this, "Nombre y correo son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar formato del correo
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                Toast.makeText(AdminDashboardActivity.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si se ingresó nueva contraseña, validar que cumpla el patrón
            if (!nuevaContraseña.isEmpty()) {
                String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
                if (!nuevaContraseña.matches(passwordPattern)) {
                    Toast.makeText(AdminDashboardActivity.this,
                            "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un dígito",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setPassword(nuevaContraseña);
            }

            user.setNombre(nuevoNombre);
            user.setRol(nuevoRol);

            // Si el correo se modificó, verificar que no exista ya
            if (!nuevoEmail.equals(user.getEmail())) {
                usuarioService.checkEmail(nuevoEmail).enqueue(new Callback<EmailCheckResponse>() {
                    @Override
                    public void onResponse(Call<EmailCheckResponse> call, Response<EmailCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isExists()) {
                            Toast.makeText(AdminDashboardActivity.this,
                                    "El correo ya está en uso. Por favor, ingresa otro correo.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // El correo es válido, actualizar el objeto y proceder a actualizar
                            user.setEmail(nuevoEmail);
                            actualizarUsuario(user, alertDialog);
                        }
                    }
                    @Override
                    public void onFailure(Call<EmailCheckResponse> call, Throwable t) {
                        Toast.makeText(AdminDashboardActivity.this,
                                "Error al verificar el correo: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // El correo no cambió, proceder directamente
                actualizarUsuario(user, alertDialog);
            }
        });
    }

    /**
     * Método auxiliar para actualizar el usuario llamando al endpoint y cerrando el diálogo.
     */
    private void actualizarUsuario(Usuario user, AlertDialog alertDialog) {
        usuarioService.updateUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private void confirmarEliminacion(Usuario user) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Deseas eliminar al usuario " + user.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarUsuario(user))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(Usuario user) {
        usuarioService.deleteUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

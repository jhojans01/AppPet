package com.example.apppet.registroUsuario;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.apppet.R;
import com.example.apppet.models.Usuario;
import com.example.apppet.network.RetrofitClient;
import com.example.apppet.network.UsuarioService;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    // Vistas del formulario
    private TextInputEditText etNombreCompleto, etEmail, etDireccion, etPassword, etConfirmPassword;
    private android.widget.EditText etFechaNacimiento;

    // RadioGroup para Tipo de Usuario: Propietario o Veterinario
    private android.widget.RadioGroup rgTipoUsuario;
    private android.widget.RadioButton rbPropietario, rbVeterinario;

    private Button btnObtenerUbicacion, btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Inicializar vistas del formulario
        etNombreCompleto = findViewById(R.id.etNombreCompleto);
        etEmail = findViewById(R.id.etEmail);
        etDireccion = findViewById(R.id.etDireccion);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);

        rgTipoUsuario = findViewById(R.id.rgTipoUsuario);
        rbPropietario = findViewById(R.id.rbPropietario);
        rbVeterinario = findViewById(R.id.rbVeterinario);

        btnObtenerUbicacion = findViewById(R.id.btnObtenerUbicacion);
        btnRegistro = findViewById(R.id.btnRegistro);

        // Configurar DatePicker para la fecha de nacimiento
        etFechaNacimiento.setOnClickListener(view -> mostrarDatePicker());

        // Botón para obtener ubicación (pendiente)
        btnObtenerUbicacion.setOnClickListener(view -> {
            Toast.makeText(SignupActivity.this, "Funcionalidad de geolocalización pendiente", Toast.LENGTH_SHORT).show();
        });

        // Botón Registrar
        btnRegistro.setOnClickListener(view -> {
            if (validarCampos()) {
                // Obtener datos del formulario
                String nombre = Objects.requireNonNull(etNombreCompleto.getText()).toString().trim();
                String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

                // Determinar el rol usando el RadioGroup
                int selectedId = rgTipoUsuario.getCheckedRadioButtonId();
                String rolSeleccionado;
                if (selectedId == R.id.rbVeterinario) {
                    rolSeleccionado = "veterinario";
                } else if (selectedId == R.id.rbCuidador) {
                    rolSeleccionado = "cuidador";
                } else {
                    // Por defecto, propietario
                    rolSeleccionado = "propietario";
                }
                Log.d(TAG, "Valor del RadioGroup: " + rolSeleccionado);

                // Guardar el rol en SharedPreferences
                SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                prefs.edit().putString("role", rolSeleccionado).apply();
                Log.d(TAG, "Rol registrado: " + rolSeleccionado);

                // Insertar el usuario en la base de datos (simulado con Retrofit)
                insertarUsuarioEnBD(nombre, email, password, rolSeleccionado);
            }
        });
    }

    private void mostrarDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, anio, mes, dia) -> {
                    String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + anio;
                    etFechaNacimiento.setText(fechaSeleccionada);
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private boolean validarCampos() {
        String nombre = Objects.requireNonNull(etNombreCompleto.getText()).toString().trim();
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String direccion = Objects.requireNonNull(etDireccion.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        // Verificar que no estén vacíos
        if (nombre.isEmpty() || email.isEmpty() || direccion.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar formato de contraseña:
        // Ejemplo: mínimo 8 caracteres, al menos una letra mayúscula, una minúscula y un dígito
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        if (!password.matches(passwordPattern)) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, " +
                    "una mayúscula, una minúscula y un dígito", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void insertarUsuarioEnBD(String nombre, String email, String password, String rol) {
        // Crear objeto Usuario con los datos ingresados
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setRol(rol);

        UsuarioService service = RetrofitClient.getRetrofitInstance().create(UsuarioService.class);

        service.insertUser(nuevoUsuario).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    // Si se inserta correctamente
                    Toast.makeText(SignupActivity.this, "Usuario insertado en la BD", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
                    prefs.edit().putInt("user_id", 1).apply(); // Simulación: se guarda user_id=1
                    Log.d(TAG, "Se guardó user_id=1 en SharedPreferences");
                    finish(); // Cierra la actividad
                } else {
                    // Se llegó aquí cuando el servidor responde con código 409 u otro error
                    try {
                        String errorResponse = response.errorBody().string();
                        if (errorResponse.contains("El correo ya está registrado")) {
                            Toast.makeText(SignupActivity.this, "El correo ya está registrado. Por favor, usa otro correo.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Error al insertar usuario: " + errorResponse, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SignupActivity.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(SignupActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

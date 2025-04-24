package com.example.apppet.laboratorio;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppet.R;

public class LaboratorioFormActivity extends AppCompatActivity {

    EditText etMascotaNombre, etEspecie, etRaza, etEdad;
    EditText etPropietarioNombre, etTelefono, etEmail;
    Button btnGuardar, btnConsultar;

    MascotasDatabaseHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratorio_form);

        etMascotaNombre = findViewById(R.id.editTextMascotaNombre);
        etEspecie = findViewById(R.id.editTextEspecie);
        etRaza = findViewById(R.id.editTextRaza);
        etEdad = findViewById(R.id.editTextEdad);
        etPropietarioNombre = findViewById(R.id.editTextPropietarioNombre);
        etTelefono = findViewById(R.id.editTextTelefono);
        etEmail = findViewById(R.id.editTextEmail);
        btnGuardar = findViewById(R.id.buttonGuardar);
        btnConsultar = findViewById(R.id.buttonConsultar);

        dbHelper = new MascotasDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mascotaNombre = etMascotaNombre.getText().toString();
                String especie = etEspecie.getText().toString();
                String raza = etRaza.getText().toString();
                int edad = etEdad.getText().toString().isEmpty() ? 0 : Integer.parseInt(etEdad.getText().toString());
                String propietarioNombre = etPropietarioNombre.getText().toString();
                String telefono = etTelefono.getText().toString();
                String email = etEmail.getText().toString();

                if(mascotaNombre.isEmpty() || especie.isEmpty() || propietarioNombre.isEmpty()){
                    showMessage("Error", "Por favor, complete los campos obligatorios");
                    return;
                }

                android.content.ContentValues values = new android.content.ContentValues();
                values.put(MascotasDatabaseHelper.COLUMN_MASCOTA_NOMBRE, mascotaNombre);
                values.put(MascotasDatabaseHelper.COLUMN_ESPECIE, especie);
                values.put(MascotasDatabaseHelper.COLUMN_RAZA, raza);
                values.put(MascotasDatabaseHelper.COLUMN_EDAD, edad);
                values.put(MascotasDatabaseHelper.COLUMN_PROPIETARIO_NOMBRE, propietarioNombre);
                values.put(MascotasDatabaseHelper.COLUMN_TELEFONO, telefono);
                values.put(MascotasDatabaseHelper.COLUMN_EMAIL, email);

                long newRowId = database.insert(MascotasDatabaseHelper.TABLE_MASCOTAS, null, values);
                if (newRowId != -1) {
                    showMessage("Éxito", "Datos guardados correctamente con ID: " + newRowId);
                    limpiarCampos();
                } else {
                    showMessage("Error", "No se pudo guardar los datos");
                }
            }
        });

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = database.query(MascotasDatabaseHelper.TABLE_MASCOTAS,
                        null, null, null, null, null, null);

                if (cursor.getCount() == 0) {
                    showMessage("Info", "No hay datos disponibles");
                    return;
                }

                StringBuilder buffer = new StringBuilder();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_ID));
                    String mascota = cursor.getString(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_MASCOTA_NOMBRE));
                    String especie = cursor.getString(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_ESPECIE));
                    String raza = cursor.getString(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_RAZA));
                    int edad = cursor.getInt(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_EDAD));
                    String propietario = cursor.getString(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_PROPIETARIO_NOMBRE));
                    String telefono = cursor.getString(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_TELEFONO));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(MascotasDatabaseHelper.COLUMN_EMAIL));

                    buffer.append("ID: ").append(id).append("\n")
                            .append("Mascota: ").append(mascota).append("\n")
                            .append("Especie: ").append(especie).append("\n")
                            .append("Raza: ").append(raza).append("\n")
                            .append("Edad: ").append(edad).append("\n")
                            .append("Propietario: ").append(propietario).append("\n")
                            .append("Teléfono: ").append(telefono).append("\n")
                            .append("Email: ").append(email).append("\n\n");
                }
                cursor.close();
                showMessage("Datos Almacenados", buffer.toString());
            }
        });
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    private void limpiarCampos() {
        etMascotaNombre.setText("");
        etEspecie.setText("");
        etRaza.setText("");
        etEdad.setText("");
        etPropietarioNombre.setText("");
        etTelefono.setText("");
        etEmail.setText("");
    }
}

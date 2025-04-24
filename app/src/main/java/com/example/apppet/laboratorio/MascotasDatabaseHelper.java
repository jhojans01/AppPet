package com.example.apppet.laboratorio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MascotasDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mascotas.db";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    public static final String TABLE_MASCOTAS = "mascotas";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MASCOTA_NOMBRE = "mascota_nombre";
    public static final String COLUMN_ESPECIE = "especie";
    public static final String COLUMN_RAZA = "raza";
    public static final String COLUMN_EDAD = "edad";
    public static final String COLUMN_PROPIETARIO_NOMBRE = "propietario_nombre";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_EMAIL = "email";

    // Instrucción SQL para crear la tabla
    private static final String DATABASE_CREATE = "create table " + TABLE_MASCOTAS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_MASCOTA_NOMBRE + " text not null, " +
            COLUMN_ESPECIE + " text not null, " +
            COLUMN_RAZA + " text, " +
            COLUMN_EDAD + " integer, " +
            COLUMN_PROPIETARIO_NOMBRE + " text not null, " +
            COLUMN_TELEFONO + " text, " +
            COLUMN_EMAIL + " text);";

    public MascotasDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Se llama al crear la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d("DBHelper", "Tabla " + TABLE_MASCOTAS + " creada");
    }

    // Se llama cuando se actualiza la versión de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En este ejemplo, simplemente eliminamos la tabla y la volvemos a crear
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASCOTAS);
        onCreate(db);
    }
}

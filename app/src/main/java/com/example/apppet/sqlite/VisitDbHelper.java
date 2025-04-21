package com.example.apppet.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VisitDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "visitas.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_VISITS = "visitas";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PET_ID = "pet_id";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_MOTIVO = "motivo";
    public static final String COLUMN_OBSERVACIONES = "observaciones";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_VISITS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PET_ID + " INTEGER, " +
                    COLUMN_FECHA + " TEXT, " +
                    COLUMN_MOTIVO + " TEXT, " +
                    COLUMN_OBSERVACIONES + " TEXT" +
                    ");";

    public VisitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITS);
        onCreate(db);
    }
}

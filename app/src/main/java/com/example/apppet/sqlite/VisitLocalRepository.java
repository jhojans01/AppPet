package com.example.apppet.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apppet.models.Visit;

import java.util.ArrayList;
import java.util.List;

public class VisitLocalRepository {

    private VisitDbHelper dbHelper;

    public VisitLocalRepository(Context context) {
        dbHelper = new VisitDbHelper(context);
    }

    public void insertarVisita(Visit visita) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(VisitDbHelper.COLUMN_PET_ID, visita.getPetId());
        values.put(VisitDbHelper.COLUMN_FECHA, visita.getFecha());
        values.put(VisitDbHelper.COLUMN_MOTIVO, visita.getMotivo());
        values.put(VisitDbHelper.COLUMN_OBSERVACIONES, visita.getObservaciones());

        db.insert(VisitDbHelper.TABLE_VISITS, null, values);
        db.close();
    }
    public List<Visit> obtenerVisitasPorMascota(int petId) {
        List<Visit> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + VisitDbHelper.TABLE_VISITS + " WHERE pet_id = ?",
                new String[]{String.valueOf(petId)}
        );

        while (cursor.moveToNext()) {
            Visit visita = new Visit();
            visita.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            visita.setPetId(cursor.getInt(cursor.getColumnIndexOrThrow("pet_id")));
            visita.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            visita.setMotivo(cursor.getString(cursor.getColumnIndexOrThrow("motivo")));
            visita.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
            lista.add(visita);
        }

        cursor.close();
        db.close();
        return lista;
    }

}

package com.uth.pm1ea0359.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "agenda_contactos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLA_CONTACTOS = "contactos";

    public static final String COL_ID = "id";
    public static final String COL_PAIS = "pais";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_TELEFONO = "telefono";
    public static final String COL_NOTA = "nota";
    public static final String COL_IMAGEN = "imagen";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + TABLA_CONTACTOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PAIS + " TEXT NOT NULL, " +
                COL_NOMBRE + " TEXT NOT NULL, " +
                COL_TELEFONO + " TEXT NOT NULL, " +
                COL_NOTA + " TEXT, " +
                COL_IMAGEN + " TEXT" +
                ")";
        db.execSQL(crearTabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_CONTACTOS);
        onCreate(db);
    }
}

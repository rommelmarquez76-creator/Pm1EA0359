package com.uth.pm1ea0359.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uth.pm1ea0359.database.DatabaseHelper;
import com.uth.pm1ea0359.model.Contacto;

import java.util.ArrayList;
import java.util.List;

public class ContactoDAO {

    private final DatabaseHelper helper;

    public ContactoDAO(Context context) {
        helper = new DatabaseHelper(context.getApplicationContext());
    }

    private ContentValues aValores(Contacto c) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_PAIS, c.getPais());
        v.put(DatabaseHelper.COL_NOMBRE, c.getNombre());
        v.put(DatabaseHelper.COL_TELEFONO, c.getTelefono());
        v.put(DatabaseHelper.COL_NOTA, c.getNota());
        v.put(DatabaseHelper.COL_IMAGEN, c.getImagen());
        return v;
    }

    private Contacto desdeCursor(Cursor cur) {
        return new Contacto(
                cur.getInt(cur.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                cur.getString(cur.getColumnIndexOrThrow(DatabaseHelper.COL_PAIS)),
                cur.getString(cur.getColumnIndexOrThrow(DatabaseHelper.COL_NOMBRE)),
                cur.getString(cur.getColumnIndexOrThrow(DatabaseHelper.COL_TELEFONO)),
                cur.getString(cur.getColumnIndexOrThrow(DatabaseHelper.COL_NOTA)),
                cur.getString(cur.getColumnIndexOrThrow(DatabaseHelper.COL_IMAGEN)));
    }

    public long insertar(Contacto c) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(DatabaseHelper.TABLA_CONTACTOS, null, aValores(c));
    }

    public List<Contacto> obtenerTodos() {
        List<Contacto> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cur = db.query(DatabaseHelper.TABLA_CONTACTOS, null, null, null,
                null, null, DatabaseHelper.COL_NOMBRE + " COLLATE NOCASE ASC");
        while (cur.moveToNext()) {
            lista.add(desdeCursor(cur));
        }
        cur.close();
        return lista;
    }

    public Contacto obtenerPorId(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cur = db.query(DatabaseHelper.TABLA_CONTACTOS, null,
                DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Contacto c = null;
        if (cur.moveToFirst()) {
            c = desdeCursor(cur);
        }
        cur.close();
        return c;
    }

    public int actualizar(Contacto c) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(DatabaseHelper.TABLA_CONTACTOS, aValores(c),
                DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(c.getId())});
    }

    public int eliminar(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLA_CONTACTOS,
                DatabaseHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}

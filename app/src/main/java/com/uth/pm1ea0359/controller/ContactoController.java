package com.uth.pm1ea0359.controller;

import android.content.Context;

import com.uth.pm1ea0359.dao.ContactoDAO;
import com.uth.pm1ea0359.model.Contacto;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactoController {


    private static final Pattern PATRON_NOMBRE =
            Pattern.compile("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(\\s[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*$");

    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{8}$");

    private static final Pattern PATRON_CODIGO = Pattern.compile("\\((\\d+)\\)");

    private final ContactoDAO dao;

    public ContactoController(Context context) {
        dao = new ContactoDAO(context);
    }



    public String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Debe escribir un nombre";
        }
        if (!PATRON_NOMBRE.matcher(nombre.trim()).matches()) {
            return "El nombre solo debe contener letras";
        }
        return null;
    }

    public String validarTelefono(String telefono) {
        if (telefono == null || !PATRON_TELEFONO.matcher(telefono.trim()).matches()) {
            return "Debe escribir un teléfono de 8 dígitos";
        }
        return null;
    }

    public String validarNota(String nota) {
        if (nota == null || nota.trim().isEmpty()) {
            return "Debe escribir una nota";
        }
        return null;
    }

    public String validarImagen(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return "Debe seleccionar una fotografía";
        }
        return null;
    }



    public boolean guardar(Contacto c) {
        return dao.insertar(c) > 0;
    }

    public boolean actualizar(Contacto c) {
        return dao.actualizar(c) > 0;
    }

    public boolean eliminar(int id) {
        return dao.eliminar(id) > 0;
    }

    public List<Contacto> listar() {
        return dao.obtenerTodos();
    }

    public Contacto buscar(int id) {
        return dao.obtenerPorId(id);
    }



    public static String codigoPais(String pais) {
        if (pais == null) return "";
        Matcher m = PATRON_CODIGO.matcher(pais);
        return m.find() ? m.group(1) : "";
    }


    public static String numeroCompleto(Contacto c) {
        return codigoPais(c.getPais()) + c.getTelefono();
    }


    public static String numeroFormateado(Contacto c) {
        String t = c.getTelefono();
        if (t != null && t.length() == 8) {
            t = t.substring(0, 4) + "-" + t.substring(4);
        }
        return "+" + codigoPais(c.getPais()) + " " + t;
    }
}

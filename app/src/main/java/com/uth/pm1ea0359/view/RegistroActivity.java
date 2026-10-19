package com.uth.pm1ea0359.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.uth.pm1ea0359.R;
import com.uth.pm1ea0359.controller.ContactoController;
import com.uth.pm1ea0359.controller.ImagenController;
import com.uth.pm1ea0359.model.Contacto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistroActivity extends AppCompatActivity {

    private static final String PREFS = "agenda_prefs";
    private static final String KEY_PAISES = "paises";

    private ImageView imgFoto;
    private Spinner spPais;
    private EditText etNombre, etTelefono, etNota;
    private Button btnSalvar;

    private ContactoController controller;
    private ArrayAdapter<String> adapterPais;
    private final List<String> paises = new ArrayList<>();

    private String rutaImagen = null;
    private int idEditar = -1;

    private final ActivityResultLauncher<String> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String ruta = ImagenController.guardarDesdeUri(this, uri);
                    if (ruta != null) {
                        mostrarFoto(ruta);
                    } else {
                        Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<Void> camaraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    String ruta = ImagenController.guardarBitmap(this, bitmap);
                    if (ruta != null) mostrarFoto(ruta);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        controller = new ContactoController(this);

        imgFoto = findViewById(R.id.imgFoto);
        spPais = findViewById(R.id.spPais);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);
        btnSalvar = findViewById(R.id.btnSalvar);
        ImageButton btnCamara = findViewById(R.id.btnCamara);
        Button btnLista = findViewById(R.id.btnLista);
        Button btnAgregarPais = findViewById(R.id.btnAgregarPais);

        cargarPaises();
        adapterPais = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paises);
        spPais.setAdapter(adapterPais);

        idEditar = getIntent().getIntExtra("id", -1);
        if (idEditar != -1) {
            cargarContactoParaEditar();
        }

        btnCamara.setOnClickListener(v -> elegirOrigenFoto());
        imgFoto.setOnClickListener(v -> elegirOrigenFoto());
        btnAgregarPais.setOnClickListener(v -> dialogoAgregarPais());
        btnSalvar.setOnClickListener(v -> guardar());
        btnLista.setOnClickListener(v -> startActivity(new Intent(this, ListaActivity.class)));
    }



    private void elegirOrigenFoto() {
        String[] opciones = {"Tomar fotografía", "Elegir de la galería"};
        new AlertDialog.Builder(this)
                .setTitle("Fotografía")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        try {
                            camaraLauncher.launch(null);
                        } catch (Exception e) {
                            Toast.makeText(this, "No hay cámara disponible", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        galeriaLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void mostrarFoto(String ruta) {
        rutaImagen = ruta;
        Bitmap bmp = ImagenController.cargar(ruta);
        if (bmp != null) imgFoto.setImageBitmap(bmp);
    }



    private void cargarPaises() {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        String guardados = sp.getString(KEY_PAISES, null);
        if (guardados == null) {
            paises.addAll(Arrays.asList("Honduras (504)", "Costa Rica (506)",
                    "Guatemala (502)", "El Salvador (503)"));
        } else {
            paises.addAll(Arrays.asList(guardados.split("\\|")));
        }
    }

    private void guardarPaises() {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putString(KEY_PAISES, String.join("|", paises)).apply();
    }

    private void dialogoAgregarPais() {
        LinearLayoutHelper h = new LinearLayoutHelper(this);
        new AlertDialog.Builder(this)
                .setTitle("Agregar país")
                .setView(h.layout)
                .setPositiveButton("Agregar", (d, w) -> {
                    String nombre = h.etNombre.getText().toString().trim();
                    String codigo = h.etCodigo.getText().toString().trim();
                    if (!nombre.matches("[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+") || !codigo.matches("\\d{1,4}")) {
                        Toast.makeText(this, "Nombre (solo letras) y código (solo números) requeridos",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    String nuevo = nombre + " (" + codigo + ")";
                    if (!paises.contains(nuevo)) {
                        paises.add(nuevo);
                        guardarPaises();
                        adapterPais.notifyDataSetChanged();
                    }
                    spPais.setSelection(paises.indexOf(nuevo));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private static class LinearLayoutHelper {
        final android.widget.LinearLayout layout;
        final EditText etNombre, etCodigo;

        LinearLayoutHelper(android.content.Context ctx) {
            layout = new android.widget.LinearLayout(ctx);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            int p = (int) (16 * ctx.getResources().getDisplayMetrics().density);
            layout.setPadding(p, p, p, 0);
            etNombre = new EditText(ctx);
            etNombre.setHint("Nombre del país");
            etCodigo = new EditText(ctx);
            etCodigo.setHint("Código (ej. 505)");
            etCodigo.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            layout.addView(etNombre);
            layout.addView(etCodigo);
        }
    }



    private void cargarContactoParaEditar() {
        Contacto c = controller.buscar(idEditar);
        if (c == null) {
            idEditar = -1;
            return;
        }
        if (!paises.contains(c.getPais())) {
            paises.add(c.getPais());
            guardarPaises();
        }
        spPais.setSelection(paises.indexOf(c.getPais()));
        etNombre.setText(c.getNombre());
        etTelefono.setText(c.getTelefono());
        etNota.setText(c.getNota());
        mostrarFoto(c.getImagen());
        btnSalvar.setText("Actualizar Contacto");
    }

    private void alerta(String mensaje, EditText campo) {
        if (campo != null) {
            campo.setError(mensaje);
            campo.requestFocus();
        }
        new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        String error = controller.validarImagen(rutaImagen);
        if (error != null) { alerta(error, null); return; }

        error = controller.validarNombre(nombre);
        if (error != null) { alerta(error, etNombre); return; }

        error = controller.validarTelefono(telefono);
        if (error != null) { alerta(error, etTelefono); return; }

        error = controller.validarNota(nota);
        if (error != null) { alerta(error, etNota); return; }

        String pais = spPais.getSelectedItem().toString();
        Contacto c = new Contacto(pais, nombre, telefono, nota, rutaImagen);

        if (idEditar == -1) {
            if (controller.guardar(c)) {
                Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show();
                limpiar();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        } else {
            c.setId(idEditar);
            if (controller.actualizar(c)) {
                Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void limpiar() {
        etNombre.setText("");
        etTelefono.setText("");
        etNota.setText("");
        rutaImagen = null;
        imgFoto.setImageResource(android.R.drawable.ic_menu_gallery);
        spPais.setSelection(0);
        etNombre.requestFocus();
    }
}

package com.uth.pm1ea0359.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.uth.pm1ea0359.R;
import com.uth.pm1ea0359.controller.ContactoController;
import com.uth.pm1ea0359.controller.ImagenController;
import com.uth.pm1ea0359.model.Contacto;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private ListView lvContactos;
    private EditText etBuscar;
    private ArrayAdapter<String> adapter;

    private ContactoController controller;
    private final List<Contacto> todos = new ArrayList<>();
    private final List<Contacto> mostrados = new ArrayList<>();
    private int seleccion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        controller = new ContactoController(this);

        lvContactos = findViewById(R.id.lvContactos);
        etBuscar = findViewById(R.id.etBuscar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,
                new ArrayList<String>());
        lvContactos.setAdapter(adapter);
        lvContactos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lvContactos.setOnItemClickListener((parent, view, position, id) -> {
            seleccion = position;
            lvContactos.setItemChecked(position, true);
            confirmarLlamada(mostrados.get(position));
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { filtrar(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
        ((Button) findViewById(R.id.btnCompartir)).setOnClickListener(v -> compartir());
        ((Button) findViewById(R.id.btnVerImagen)).setOnClickListener(v -> verImagen());
        ((Button) findViewById(R.id.btnEliminar)).setOnClickListener(v -> eliminar());
        ((Button) findViewById(R.id.btnActualizar)).setOnClickListener(v -> actualizar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        todos.clear();
        todos.addAll(controller.listar());
        filtrar();
    }

    private void filtrar() {
        String q = etBuscar.getText().toString().trim().toLowerCase();
        mostrados.clear();
        adapter.clear();
        for (Contacto c : todos) {
            if (q.isEmpty()
                    || c.getNombre().toLowerCase().contains(q)
                    || c.getTelefono().contains(q)) {
                mostrados.add(c);
                adapter.add(c.getNombre() + " | " + ContactoController.numeroCompleto(c));
            }
        }
        lvContactos.clearChoices();
        seleccion = -1;
        adapter.notifyDataSetChanged();
    }

    private Contacto seleccionado() {
        if (seleccion < 0 || seleccion >= mostrados.size()) {
            Toast.makeText(this, "Seleccione un contacto de la lista", Toast.LENGTH_SHORT).show();
            return null;
        }
        return mostrados.get(seleccion);
    }



    private void confirmarLlamada(Contacto c) {
        new AlertDialog.Builder(this)
                .setTitle("Acción")
                .setMessage("¿Desea llamar a " + c.getNombre() + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (d, w) -> {
                    Intent i = new Intent(this, LlamarActivity.class);
                    i.putExtra("id", c.getId());
                    startActivity(i);
                })
                .show();
    }


    private void compartir() {
        Contacto c = seleccionado();
        if (c == null) return;

        String texto = "Contacto\n"
                + "Nombre: " + c.getNombre() + "\n"
                + "País: " + c.getPais() + "\n"
                + "Teléfono: " + ContactoController.numeroFormateado(c) + "\n"
                + "Nota: " + c.getNota();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Contacto: " + c.getNombre());
        i.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(i, "Compartir contacto"));
    }



    private void verImagen() {
        Contacto c = seleccionado();
        if (c == null) return;

        Bitmap bmp = ImagenController.cargar(c.getImagen());
        if (bmp == null) {
            Toast.makeText(this, "Este contacto no tiene imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageView iv = new ImageView(this);
        iv.setAdjustViewBounds(true);
        iv.setPadding(24, 24, 24, 24);
        iv.setImageBitmap(bmp);

        new AlertDialog.Builder(this)
                .setTitle(c.getNombre())
                .setView(iv)
                .setPositiveButton("Cerrar", null)
                .show();
    }



    private void eliminar() {
        Contacto c = seleccionado();
        if (c == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Eliminar contacto")
                .setMessage("¿Desea eliminar a " + c.getNombre() + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (d, w) -> {
                    if (controller.eliminar(c.getId())) {
                        Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                        todos.remove(c);
                        filtrar();
                    }
                })
                .show();
    }



    private void actualizar() {
        Contacto c = seleccionado();
        if (c == null) return;
        Intent i = new Intent(this, RegistroActivity.class);
        i.putExtra("id", c.getId());
        startActivity(i);
    }
}

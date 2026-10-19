package com.uth.pm1ea0359.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.uth.pm1ea0359.R;
import com.uth.pm1ea0359.controller.ContactoController;
import com.uth.pm1ea0359.model.Contacto;

public class LlamarActivity extends AppCompatActivity {

    private Contacto contacto;

    private final ActivityResultLauncher<String> pedirPermiso =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (concedido) {
                    realizarLlamada();
                } else {
                    Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        ContactoController controller = new ContactoController(this);
        contacto = controller.buscar(getIntent().getIntExtra("id", -1));
        if (contacto == null) {
            Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvNombre)).setText(contacto.getNombre());
        ((TextView) findViewById(R.id.tvTelefono)).setText(ContactoController.numeroFormateado(contacto));

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
        findViewById(R.id.btnLlamar).setOnClickListener(v -> confirmarLlamada());
    }

    private void confirmarLlamada() {
        new AlertDialog.Builder(this)
                .setTitle("Acción")
                .setMessage("¿Desea llamar a " + contacto.getNombre() + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (d, w) -> llamar())
                .show();
    }

    private void llamar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            realizarLlamada();
        } else {
            pedirPermiso.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void realizarLlamada() {
        Intent llamar = new Intent(Intent.ACTION_CALL);
        llamar.setData(Uri.parse("tel:+" + ContactoController.numeroCompleto(contacto)));
        try {
            startActivity(llamar);
        } catch (SecurityException e) {
            Toast.makeText(this, "No se pudo realizar la llamada", Toast.LENGTH_SHORT).show();
        }
    }
}

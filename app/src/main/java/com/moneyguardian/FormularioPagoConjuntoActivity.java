package com.moneyguardian;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.moneyguardian.adapter.UsuarioArrayAdapter;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.ui.PagosConjuntosFragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormularioPagoConjuntoActivity extends AppCompatActivity {

    // El botón de la imagen
    private Button BSelectImage;

    // Imagen de preview
    private ImageView IVPreviewImage;
    private ListView listViewUsuarios;
    private ArrayList<Usuario> usuarios;
    private UsuarioArrayAdapter usuarioArrayAdapter;

    // Valores del pago conjunto
    private EditText nombrePago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_pago_conjunto);

        // TODO inicializar la lista con la BD
        usuarios = new ArrayList<>();
        usuarios.add(new Usuario("Pepe", "pepe@pepe.pepe", new ArrayList<>(), new ArrayList<>()));

        BSelectImage = findViewById(R.id.buttonSeleccionarImagenNuevoPagoConjunto);
        IVPreviewImage = findViewById(R.id.imagePreviewNuevoPagoConjunto);
        listViewUsuarios = findViewById(R.id.listUsuariosNuevoPagoConjunto);

        // Manejo del botón de la imágen
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        // Manjeo de la lista de usuarios

        usuarioArrayAdapter = new UsuarioArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, usuarios);
        listViewUsuarios.setChoiceMode(CHOICE_MODE_MULTIPLE);

        listViewUsuarios.setAdapter(usuarioArrayAdapter);

        // Manejo del botón de crear

        Button btnCrear = findViewById(R.id.buttonCrearNuevoPagoConjunto);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarPagoConjunto()) {
                    Log.i("Estado Pago Conjunto", "Validado");

                    List<Usuario> participantes = new ArrayList<Usuario>();
                    // Rellenamos la lista de usuarios
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarioArrayAdapter.isChecked(i)) {
                            participantes.add(usuarios.get(i));
                        }
                    }

                    // La fecha se inicializa automáticamente a la actual
                    // TODO quedaría añadir la imágen
                    PagoConjunto pagoConjunto = new PagoConjunto(nombrePago.getText().toString(), new Date(), participantes);

                    // TODO enviar a la base de datos

                    //Snackbar.make(findViewById(R.id.recyclerPagosConjuntos), R.string.PagoConjuntoCreado, Snackbar.LENGTH_LONG).show();

                    Intent intentResult = new Intent();
                    intentResult.putExtra(PagosConjuntosFragment.PAGO_CONJUNTO_CREADO, pagoConjunto);
                    setResult(RESULT_OK, intentResult);
                    finish();
                }
                Log.i("Estado Pago Conjunto", "No validado");
            }
        });

    }

    private void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    private boolean validarPagoConjunto() {
        // Esto no funciona
        // Log.i("Checkbox", String.valueOf(listViewUsuarios.getCheckedItemPositions().get(0)));
        // Esto si
        Log.i("Checkbox 2", String.valueOf(usuarioArrayAdapter.isChecked(0)));

        // Validar datos

        nombrePago = findViewById(R.id.editTextNombrePagoConjunto);
        if (nombrePago.getText().toString().trim().isEmpty()) {
            nombrePago.setError(getString(R.string.ErrorNombreVacio));
            return false;
        }

        // TODO validad icono

        boolean oneUserMarked = false;
        for (int i = 0; i < this.usuarios.size(); i++) {
            if (usuarioArrayAdapter.isChecked(i)) {
                oneUserMarked = true;
                break;
            }
        }
        // Si no hay, como mínimo, otro usuario en el pago, no dejamos que se efectue
        if (!oneUserMarked) {
            // TODO: al ser un text view, no deja ver cual es el error
            TextView usuarios = findViewById(R.id.textViewParticipantes);
            // TODO sacarlo al strings.xml?
            usuarios.setError("Debe haber como mínimo otro usuario en el pago");
            return false;
        }

        return true;
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        } catch (FileNotFoundException ef) {
                            // TODO
                            ef.printStackTrace();
                        } catch (IOException e) {
                            // TODO
                            e.printStackTrace();
                        }
                        IVPreviewImage.setImageBitmap(selectedImageBitmap);
                    }
                }
            });
}
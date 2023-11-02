package com.moneyguardian;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.moneyguardian.adapter.UsuarioArrayAdapter;
import com.moneyguardian.modelo.Usuario;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class FormularioPagoConjuntoActivity extends AppCompatActivity {

    // El botón de la imagen
    private Button BSelectImage;

    // Imagen de preview
    private ImageView IVPreviewImage;
    private ListView listViewUsuarios;
    private ArrayList<Usuario> usuarios;
    private UsuarioArrayAdapter usuarioArrayAdapter;

    // Activity result code
    private final static int SELECT_PICTURE = 200;

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

        // Sin utilizar un adapter custom:
        // listViewUsuarios.setAdapter(new ArrayAdapter<Usuario>(this, android.R.layout.simple_list_item_multiple_choice, usuarios));

        listViewUsuarios.setAdapter(usuarioArrayAdapter);

        // Manejo del botón de crear

        Button btnCrear = findViewById(R.id.buttonCrearNuevoPagoConjunto);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPagoConjunto();
            }
        });

    }

    private void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    private void crearPagoConjunto() {
        // Esto no funciona
        Log.i("Checkbox", String.valueOf(listViewUsuarios.getCheckedItemPositions().get(0)));
        // Esto si
        Log.i("Checkbox 2", String.valueOf(usuarioArrayAdapter.isChecked(0)));

        // TODO enviar a la base de datos

        // Abrir la venta a de ver información del pago
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
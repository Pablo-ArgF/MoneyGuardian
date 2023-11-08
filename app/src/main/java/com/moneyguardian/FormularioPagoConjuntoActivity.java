package com.moneyguardian;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.moneyguardian.adapter.UsuarioArrayAdapter;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.ui.DatePickerFragment;
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
    private Uri selectedImageUri;
    private ListView listViewUsuarios;
    private ArrayList<Usuario> usuarios;
    private UsuarioArrayAdapter usuarioArrayAdapter;

    // Valores del pago conjunto
    private EditText nombrePago;
    private EditText fechaPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_pago_conjunto);

        // TODO inicializar la lista con la BD
        usuarios = new ArrayList<>();
        usuarios.add(new Usuario("Pepe", "pepe@pepe.pepe", new ArrayList<>(), new ArrayList<>()));
        usuarios.add(new Usuario("Pepa", "pepe@pepe.pepe", new ArrayList<>(), new ArrayList<>()));
        usuarios.add(new Usuario("Pipi", "pepe@pepe.pepe", new ArrayList<>(), new ArrayList<>()));

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

        // Manejo de la seleccion de fecha
        EditText etPlannedDate = (EditText) findViewById(R.id.editFechaMaximaPagoConjunto);
        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // +1 because January is zero
                        final String selectedDate = day + " / " + (month + 1) + " / " + year;
                        etPlannedDate.setText(selectedDate);
                    }
                });

                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

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
                    PagoConjunto pagoConjunto = null;
                    // Si tenemos imagen
                    if (selectedImageUri != null) {
                        pagoConjunto = new PagoConjunto(nombrePago.getText().toString(), new Date(), participantes, selectedImageUri);
                        // Si no tenemos imagen
                    } else {
                        pagoConjunto = new PagoConjunto(nombrePago.getText().toString(), new Date(), participantes);
                    }

                    // TODO enviar a la base de datos

                    // TODO descomentar cuando se solucione el parcelable
                    //Snackbar.make(findViewById(R.id.layoutFormularioPagoConjunto), R.string.PagoConjuntoCreado, Snackbar.LENGTH_LONG).show();

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

        fechaPago = findViewById(R.id.editFechaMaximaPagoConjunto);
        if (fechaPago.getText().toString().trim().isEmpty()) {
            fechaPago.setError(getString(R.string.ErrorFechaVacia));
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
                            this.selectedImageUri = selectedImageUri;
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

    /**
     * Para guardar un archivo local en Firebase
     *
     * Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
     * StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
     * uploadTask = riversRef.putFile(file);
     *
     * // Register observers to listen for when the download is done or if it fails
     * uploadTask.addOnFailureListener(new OnFailureListener() {
     *     @Override
     *     public void onFailure(@NonNull Exception exception) {
     *         // Handle unsuccessful uploads
     *     }
     * }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
     *     @Override
     *     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
     *         // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
     *         // ...
     *     }
     * });
     */
}
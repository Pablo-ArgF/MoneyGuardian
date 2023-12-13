package com.moneyguardian;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moneyguardian.adapters.UsuarioArrayAdapter;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.ui.DatePickerFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;
import com.moneyguardian.util.ImageProcessor;
import com.moneyguardian.util.PagosConjuntosUtil;
import com.moneyguardian.util.UsuarioMapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FormularioPagoConjuntoActivity extends AppCompatActivity {

    // Manejo de imagen
    private ImageView IVPreviewImage;
    private boolean isImageSet;
    private Uri selectedImageUri;
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap selectedImageBitmap = null;
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    this.selectedImageUri = selectedImageUri;
                    // Para saber que la imagen está colocada y no es la default
                    this.isImageSet = true;
                } catch (IOException ef) {
                    // TODO
                    ef.printStackTrace();
                }
                IVPreviewImage.setImageBitmap(selectedImageBitmap);
            }
        }
    });
    private ListView listViewUsuarios;
    private ArrayList<UsuarioParaParcelable> usuarios;
    private UsuarioArrayAdapter usuarioArrayAdapter;

    // Valores del pago conjunto
    private EditText nombrePago;
    private EditText fechaPago;
    private FirebaseFirestore db;
    private StorageReference userImageRef;
    private String pagoConjuntoUUID;
    private UsuarioParaParcelable actualUser;
    private ArrayList<ItemPagoConjunto> itemsPago;
    private String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_pago_conjunto);

        // Manejo de imagen
        this.isImageSet = false;

        usuarios = new ArrayList<>();

        // Manejo de base de datos
        // Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pagoConjuntoUUID = UUID.randomUUID().toString();
        userImageRef = FirebaseStorage.getInstance().getReference().child("pagosConjuntos/" + pagoConjuntoUUID + ".jpg");

        // El botón de la imagen
        Button BSelectImage = findViewById(R.id.buttonSeleccionarImagenNuevoPagoConjunto);
        IVPreviewImage = findViewById(R.id.imagePreviewNuevoPagoConjunto);
        listViewUsuarios = findViewById(R.id.listUsuariosNuevoPagoConjunto);
        nombrePago = findViewById(R.id.editTextNombrePagoConjunto);
        fechaPago = findViewById(R.id.editFechaMaximaPagoConjunto);

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            actualUser = UsuarioMapper.mapBasicsParcelable(documentSnapshot);

            ArrayList<DocumentReference> amigos = (ArrayList<DocumentReference>) documentSnapshot.getData().get("friends");
            if (amigos != null) {
                List<Task<DocumentSnapshot>> taskAmigos = new ArrayList<>();

                for (DocumentReference d : amigos) {
                    taskAmigos.add(d.get());
                }

                Tasks.whenAllSuccess(taskAmigos).addOnSuccessListener(objects -> {
                    for (Object d : objects) {
                        usuarios.add((UsuarioMapper.mapBasicsParcelable((DocumentSnapshot) d)));
                    }

                    List<UsuarioParaParcelable> usuariosSeleccionados = new ArrayList<>();

                    itemsPago = new ArrayList<>();

                    if (getIntent().getExtras() != null && getIntent().getExtras().getParcelable("OLD_PAGO") != null) {
                        PagoConjunto oldPago = getIntent().getExtras().getParcelable("OLD_PAGO");
                        nombrePago.setText(oldPago.getNombre());
                        pagoConjuntoUUID = oldPago.getId();

                        itemsPago = new ArrayList<>(oldPago.getItems());
                        owner = oldPago.getOwner();

                        String pattern = "dd/MM/yyyy";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                        if (oldPago.getImagen() != null) {
                            this.selectedImageUri = oldPago.getImagen();
                            this.isImageSet = true;
                            Picasso.get().load(this.selectedImageUri).into(this.IVPreviewImage);
                        }

                        fechaPago.setText(simpleDateFormat.format(oldPago.getFechaLimite()));
                        usuariosSeleccionados = oldPago.getParticipantes();
                    }

                    usuarioArrayAdapter = new UsuarioArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, usuarios, usuariosSeleccionados);
                    listViewUsuarios.setChoiceMode(CHOICE_MODE_MULTIPLE);

                    listViewUsuarios.setAdapter(usuarioArrayAdapter);

                });
            }

        });

        // Manejo del botón de la imágen
        BSelectImage.setOnClickListener(v -> imageChooser());

        // Manejo de la seleccion de fecha
        EditText etPlannedDate = (EditText) fechaPago;
        etPlannedDate.setOnClickListener(v -> {
            DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;
                etPlannedDate.setText(selectedDate);
            }, System.currentTimeMillis() - 1000);

            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // Manejo del botón de crear

        Button btnCrear = findViewById(R.id.buttonCrearNuevoPagoConjunto);
        btnCrear.setOnClickListener(v -> {
            if (validarPagoConjunto()) {
                List<UsuarioParaParcelable> participantes;

                // Rellenamos la lista de usuarios
                participantes = usuarioArrayAdapter.getChecked();

                participantes.add(actualUser);

                String[] fechaTexto = fechaPago.getText().toString().trim().split("/");
                Calendar fechaLimite = Calendar.getInstance();

                fechaLimite.set(Integer.parseInt(fechaTexto[2].trim()), Integer.parseInt(fechaTexto[1].trim()) - 1, Integer.parseInt(fechaTexto[0].trim()));

                Date dateLimite = fechaLimite.getTime();

                // La fecha se inicializa automáticamente a la actual
                owner = mAuth.getUid();
                PagoConjunto pagoConjunto = null;
                pagoConjunto = new PagoConjunto(pagoConjuntoUUID, nombrePago.getText().toString(), new Date(), new ArrayList<>(participantes), selectedImageUri, dateLimite,
                        itemsPago, owner);

                // Si tenemos imagen
                if (selectedImageUri != null) {
                    // Añadimos la imagen a la BD
                    Bitmap imageBitmap = ((BitmapDrawable) IVPreviewImage.getDrawable()).getBitmap();
                    // Si la imagen se añade correctamente
                    UploadTask task = ImageProcessor.processImage(imageBitmap, userImageRef, getApplicationContext());
                    PagoConjunto finalPagoConjunto = pagoConjunto;
                    task.addOnSuccessListener(taskSnapshot -> {
                        //we store the link to the image in the store in the db
                        userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Guardamos el pago conjunto
                                    finalPagoConjunto.setImagen(uri);
                                    PagosConjuntosUtil.addPagoConjunto(finalPagoConjunto, usuarioArrayAdapter.getChecked(), pagoConjuntoUUID);
                                    db.collection("pagosConjuntos").
                                            document(pagoConjuntoUUID).update("imagen", uri).
                                            addOnFailureListener(e -> Toast.makeText(getApplicationContext(), getString(R.string.error_upload_pago_image), Toast.LENGTH_LONG).show());
                                }
                        );
                    });
                } else {
                    PagosConjuntosUtil.addPagoConjunto(pagoConjunto, usuarioArrayAdapter.getChecked(), pagoConjuntoUUID);
                }

                Intent intentResult = new Intent();
                intentResult.putExtra(PagosConjuntosFragment.PAGO_CONJUNTO_CREADO, pagoConjunto);
                setResult(RESULT_OK, intentResult);
                finish();
            }
            Log.i("Estado Pago Conjunto", "No validado");
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
        //Log.i("Checkbox 2", String.valueOf(usuarioArrayAdapter.isChecked(0)));

        // Validar datos

        nombrePago = findViewById(R.id.editTextNombrePagoConjunto);
        if (nombrePago.getText().toString().trim().isEmpty()) {
            nombrePago.setError(getString(R.string.ErrorNombreVacio));
            return false;
        }

        // La validación de la imagen solo comprueba si está colocada o no, si no lo está, es null
        if (!this.isImageSet) {
            selectedImageUri = null;
        }

        if (fechaPago.getText().toString().trim().isEmpty()) {
            fechaPago.setError(getString(R.string.ErrorFechaVacia));
            return false;
        }

        // Si no hay, como mínimo, otro usuario en el pago, no dejamos que se efectue
        if (!usuarioArrayAdapter.atLeastOneUserSelected()) {
            // TODO: al ser un text view, no deja ver cual es el error
            TextView usuarios = findViewById(R.id.textViewParticipantes);
            // TODO sacarlo al strings.xml?
            usuarios.setError("Debe haber como mínimo otro usuario en el pago");
            return false;
        }

        return true;
    }

}
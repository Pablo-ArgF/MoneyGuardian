package com.moneyguardian;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.moneyguardian.adapters.UsuarioArrayAdapter;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.ui.DatePickerFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;
import com.moneyguardian.util.ImageProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FormularioPagoConjuntoActivity extends AppCompatActivity {

    // El botón de la imagen
    private Button BSelectImage;

    // Manejo de imagen
    private ImageView IVPreviewImage;
    private boolean isImageSet;
    private Uri selectedImageUri;
    private ListView listViewUsuarios;
    private ArrayList<UsuarioParaParcelable> usuarios;
    private ArrayList<DocumentReference> usuariosUUIDs;
    private UsuarioArrayAdapter usuarioArrayAdapter;

    // Valores del pago conjunto
    private EditText nombrePago;
    private EditText fechaPago;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference userImageRef;
    private String pagoConjuntoUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_pago_conjunto);

        // Manejo de imagen
        this.isImageSet = false;

        // Manejo de base de datos
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pagoConjuntoUUID = UUID.randomUUID().toString();
        userImageRef = FirebaseStorage.getInstance().getReference().child("pagosConjuntos/" + pagoConjuntoUUID + ".jpg");

        //Inicializar la lista con la BD
        this.cargarAmigos();

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
                }, System.currentTimeMillis() - 1000);

                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Manejo del botón de crear

        Button btnCrear = findViewById(R.id.buttonCrearNuevoPagoConjunto);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarPagoConjunto()) {
                    List<UsuarioParaParcelable> participantes = new ArrayList<UsuarioParaParcelable>();
                    // Rellenamos la lista de usuarios
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarioArrayAdapter.isChecked(i)) {
                            participantes.add(usuarios.get(i));
                        }
                    }

                    String[] fechaTexto = fechaPago.getText().toString().trim().split("/");
                    Calendar fechaLimite = Calendar.getInstance();

                    fechaLimite.set(Integer.parseInt(fechaTexto[0].trim()), Integer.parseInt(fechaTexto[1].trim()), Integer.parseInt(fechaTexto[2].trim()));

                    Date dateLimite = fechaLimite.getTime();

                    // La fecha se inicializa automáticamente a la actual
                    PagoConjunto pagoConjunto = null;

                    // Si no tenemos imagen
                    if (selectedImageUri == null) {
                        pagoConjunto = new PagoConjunto(pagoConjuntoUUID, nombrePago.getText().toString(), new Date(), participantes, dateLimite);
                    } else {
                        // Si tenemos imagen
                        pagoConjunto = new PagoConjunto(pagoConjuntoUUID, nombrePago.getText().toString(), new Date(), participantes, selectedImageUri, dateLimite);

                        // Añadimos la imagen a la BD
                        Bitmap imageBitmap = ((BitmapDrawable) IVPreviewImage.getDrawable()).getBitmap();
                        // Si la imagen se añade correctamente
                        UploadTask task = ImageProcessor.processImage(imageBitmap, userImageRef, getApplicationContext());
                        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //we store the link to the image in the store in the db
                                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        db.collection("pagosConjuntos").document(pagoConjuntoUUID).update("imagen", uri).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), getString(R.string.error_upload_pago_image), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    // Creación del Map para persistencia

                    // TODO existe una manera de sacar el collectionPath para que sea configurable?
                    Map<String, Object> pagoConjuntoDoc = new HashMap<>();
                    pagoConjuntoDoc.put("nombre", pagoConjunto.getNombre());
                    pagoConjuntoDoc.put("imagen", pagoConjunto.getImagen());
                    pagoConjuntoDoc.put("fechaLimite", pagoConjunto.getFechaLimite());
                    pagoConjuntoDoc.put("fechaPago", pagoConjunto.getFechaPago());
                    // Guardamos los participantes como una lista de referencias
                    ArrayList<DocumentReference> nestedParticipantes = new ArrayList<DocumentReference>();
                    nestedParticipantes.addAll(usuariosUUIDs);
                    pagoConjuntoDoc.put("participantes", nestedParticipantes);
                    // OJO: el usuario pagador debe ir dentro de un Map para poder realizar la query en Firestore
                    List<String> userId = new ArrayList<String>();
                    userId.add(mAuth.getCurrentUser().getUid());
                    pagoConjuntoDoc.put("pagador", userId);
                    PagoConjunto finalPagoConjunto = pagoConjunto;
                    db.collection("pagosConjuntos").document(pagoConjuntoUUID).set(pagoConjuntoDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i("FIREBASE SET", "Se añadió el objeto");
                            DocumentReference docReference = db.collection("pagosConjuntos").
                                    document(pagoConjuntoUUID);

                            db.collection("users").document(userId.get(0)).
                                    update("pagosConjuntos",
                                            FieldValue.arrayUnion(docReference)).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            for(DocumentReference u : nestedParticipantes) {
                                                db.collection("users").document(u.getId()).
                                                        update("pagosConjuntos",
                                                                FieldValue.arrayUnion(docReference));
                                            }
                                            finish();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("FIRBASE SET", "Error writing document", e);
                        }
                    });
                }
                Log.i("Estado Pago Conjunto", "No validado");
            }
        });

    }

    private void cargarAmigos() {
        this.usuarios = new ArrayList<>();
        this.usuariosUUIDs = new ArrayList<>();
        DocumentReference amigosRef = db.collection("users/").
                document(mAuth.getCurrentUser().getUid());
        amigosRef.get().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = (DocumentSnapshot) task.getResult();
                    // result: Usuario -> Usuario.get("friends"): String[]
                    ArrayList<DocumentReference> amigos = (ArrayList<DocumentReference>) result.getData().get("friends");
                    if (amigos != null) {
                        for (DocumentReference amigo : amigos) {
                            String id = amigo.getId();
                            DocumentReference amigoReference = db.collection("users/").document(id);
                            // Guardamos la referencia al amigo como un DocumentReference
                            usuariosUUIDs.add(amigoReference);
                            amigoReference.get().addOnCompleteListener(getAmigoListener);
                        }
                    } else {
                        throw new RuntimeException(String.valueOf(R.string.ErrorBaseDatosAmigos));
                    }

                } else {
                    // TODO: handle error?

                }
            }
        });
    }


    private OnCompleteListener<DocumentSnapshot> getAmigoListener = new OnCompleteListener<DocumentSnapshot>() {

        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot queryResult = task.getResult();
                // Guardamos el usuario para la UI y parcelable
                usuarios.add(new UsuarioParaParcelable((String) queryResult.get("name"),
                        (String) queryResult.get("email"), (String) queryResult.get("profilePicture")));
                // Ahora, para actualizar la lista,
                // necesitamos volver a crear el adapter y asignarselo a la list view
                // POR CADA USUARIO AÑADIDO, tal vez haya una manera de optimizar este código...
                usuarioArrayAdapter = new UsuarioArrayAdapter(getApplicationContext(),
                        android.R.layout.simple_list_item_multiple_choice, usuarios);
                listViewUsuarios.setAdapter(usuarioArrayAdapter);
                usuarioArrayAdapter.notifyDataSetChanged();
            }
        }
    };


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

        fechaPago = findViewById(R.id.editFechaMaximaPagoConjunto);
        if (fechaPago.getText().toString().trim().isEmpty()) {
            fechaPago.setError(getString(R.string.ErrorFechaVacia));
            return false;
        }

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
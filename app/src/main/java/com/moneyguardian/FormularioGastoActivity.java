package com.moneyguardian;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.ui.DatePickerFragment;
import com.moneyguardian.ui.ListaGastosFragment;
import com.moneyguardian.util.GastosUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormularioGastoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isIngreso = false;

    // Formulario
    EditText nombre;
    EditText balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        // Recogemos la "FLAG" para ver si es un gasto o un ingreso
        if (b != null) {
            isIngreso = b.getBoolean("Ingreso");
        }
        setContentView(R.layout.activity_formulario_gasto);

        TextView textViewNombre = findViewById(R.id.textViewNombreGasto);
        EditText editTextNombre = findViewById(R.id.nombreGastoNuevo);
        if (!isIngreso) {
            // Si es un gasto
            textViewNombre.setText(R.string.nombreGastoNuevo);
            editTextNombre.setHint(R.string.nombreGastoNuevo);
        } else {
            // Si es un ingreso
            textViewNombre.setText(R.string.nombreIngresoNuevo);
            editTextNombre.setHint(R.string.nombreIngresoNuevo);
        }


        // Manejo de base de datos
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button buttonCreate = findViewById(R.id.buttonCrearGasto);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nombre = findViewById(R.id.nombreGastoNuevo);
                balance = findViewById(R.id.balanceGastoNuevo);

                if (validateGasto()) {
                    Gasto gasto = saveGasto();

                    Intent intentResult = new Intent();
                    intentResult.putExtra(ListaGastosFragment.GASTO_CREADO, gasto);
                    setResult(RESULT_OK, intentResult);
                    finish();
                }

            }
        });

        // Manejo de la fecha y el calendario

        EditText fechaText = findViewById(R.id.editDateGasto);
        fechaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // +1 because January is zero
                        final String selectedDate = day + " / " + (month + 1) + " / " + year;
                        fechaText.setText(selectedDate);
                    }
                });

                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Manejo del spinner con las categorías
        Spinner spCategorias = findViewById(R.id.spinnerCategoriasGasto);
        Query query = null;
        if (isIngreso) {
            query = db.collection("categorias/").whereEqualTo("tipo", "ingreso");
        } else {
            query = db.collection("categorias/").whereEqualTo("tipo", "gasto");
        }
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> categorias = new ArrayList<>();
                List<DocumentSnapshot> refs = task.getResult().getDocuments();
                refs.forEach(ref -> {
                    categorias.add((String) ref.get("nombre"));
                });
                spCategorias.setAdapter(new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_spinner_item, categorias));
            }
        });


        spCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modoOscuro(parent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private Gasto saveGasto() {
        float balanceFinal = (this.isIngreso ? 1 : -1) * Float.valueOf(balance.getText().toString());

        // Manejo de la fecha en caso de que hubiere
        EditText textFecha = findViewById(R.id.editDateGasto);
        Date fecha;
        if (!textFecha.getText().toString().trim().isEmpty()) {
            try {
                fecha = new SimpleDateFormat("dd / MM / yyyy", new Locale("es")).parse(textFecha.getText().toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else
            fecha = new Date();

        Spinner spCategorias = findViewById(R.id.spinnerCategoriasGasto);

        // Guardamos el gasto, con la fecha actual o la establecida por el usuario
        Gasto gasto = new Gasto(nombre.getText().toString(), balanceFinal, spCategorias.getSelectedItem().toString(), fecha);

        // Guardamos el gasto en la bd
        gasto = GastosUtil.addGasto(gasto);

        return gasto;
    }

    private boolean validateGasto() {
        if (nombre.getText().toString().trim().isEmpty()) {
            nombre.setError(getString(R.string.error_empty_name));
            return false;
        }
        if (balance.getText().toString().isEmpty()) {
            balance.setError(getString(R.string.error_empty_balance));
            return false;
        }
        return true;
    }

    /**
     * Este metodo no rebería realmente usarse, ya que la interfaz solo
     * debería depender de los themes
     */
    private void modoOscuro(AdapterView<?> parent) {
        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                break;

            default:
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                break;
        }
    }

}
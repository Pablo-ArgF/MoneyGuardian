package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.ui.ListaGastosFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;

import java.util.HashMap;
import java.util.UUID;

public class FormularioGastoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String gastoUUID;
    private boolean isIngreso = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        // Recogemos la "FLAG" para ver si es un gasto o un ingreso
        if (b != null) {
            isIngreso = b.getBoolean("Ingreso");
        }
        setContentView(R.layout.activity_formulario_gasto);

        // Manejo de base de datos
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        gastoUUID = UUID.randomUUID().toString();

        Button buttonCreate = findViewById(R.id.buttonCrearGasto);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateGasto()) {

                    Gasto gasto = saveGasto();

                    Intent intentResult = new Intent();
                    intentResult.putExtra(ListaGastosFragment.GASTO_CREADO, gasto);
                    setResult(RESULT_OK, intentResult);
                    finish();
                }

            }
        });

    }

    private Gasto saveGasto() {

        EditText nombre = findViewById(R.id.nombreGastoNuevo);
        EditText balance = findViewById(R.id.balanceGastoNuevo);

        float balanceFinal = (this.isIngreso ? 1 : -1) * Float.valueOf(balance.getText().toString());
        Gasto gasto = new Gasto(nombre.getText().toString(), balanceFinal, null);

        // TODO no me fio, habría que echarle un ojo por si acaso
        DocumentReference gastoReference = db.collection("gastos/").document(gastoUUID);
        gastoReference.set(gasto);

        // Guardamos la referencia al objeto en el usuario
        // Hacemos un update que añadirá el objeto a la lista en el usuario
        db.collection("users/").document(mAuth.getUid()).update("gastos",
                FieldValue.arrayUnion(gastoReference));

        return gasto;
    }

    private boolean validateGasto() {
        // TODO later
        return true;
    }


}
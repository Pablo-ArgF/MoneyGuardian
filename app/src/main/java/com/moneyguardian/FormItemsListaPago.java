package com.moneyguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.DecimalFilterForInput;
import com.moneyguardian.UsersFormItemsListaAdapter;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FormItemsListaPago extends AppCompatActivity {

    // TODO deberian llevar private...?
    RecyclerView usersToAdd;
    Spinner whoPaysSpinner;
    EditText totalMoney;
    Button btnCreateNewItemPago;
    EditText name;
    CheckBox moneyChangeActivatedCheckBox;
    private List<UsuarioParaParcelable> usuariosDelPago;
    private double cantidadTotal;

    private boolean checkBoxesActivated;
    private boolean changeMoneyActivated;
    private UsuarioParaParcelable usuarioSeleccionado;
    private PagoConjunto pagoConjunto;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List< DocumentReference> participantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_items_lista_pago);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        totalMoney = (EditText) findViewById(R.id.editTextTotalMoneyItemPago);
        btnCreateNewItemPago = (Button) findViewById(R.id.btnCreteItemPago);
        name = (EditText) findViewById(R.id.formItemPagoNameTextField);
        moneyChangeActivatedCheckBox = (CheckBox) findViewById(R.id.checkBoxChangeMoney);

        usersToAdd = (RecyclerView)findViewById(R.id.recicledViewUsersToSelect);
        usersToAdd.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        usersToAdd.setLayoutManager(layoutManager);

        pagoConjunto = getIntent().getExtras().getParcelable("PAGO");

        usuariosDelPago = new ArrayList<>();

        UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                new UsersFormItemsListaAdapter(usuariosDelPago, cantidadTotal,checkBoxesActivated
                        ,usuarioSeleccionado,changeMoneyActivated);

        usersToAdd.setAdapter(usersFormItemsListaAdapter);

        whoPaysSpinner = (Spinner) findViewById(R.id.spinnerWhoPays);
        ArrayAdapter<UsuarioParaParcelable> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,usuariosDelPago);
        whoPaysSpinner.setAdapter(adapterSpinner);
        whoPaysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usuarioSeleccionado = usuariosDelPago.get(position);

                UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                        new UsersFormItemsListaAdapter(usuariosDelPago, cantidadTotal,
                                checkBoxesActivated,usuarioSeleccionado,changeMoneyActivated);
                usersToAdd.setAdapter(usersFormItemsListaAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        db.collection("pagosConjuntos")
                .document(pagoConjunto.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List< DocumentReference> users =
                                    (List< DocumentReference >) task.getResult().get("participantes");

                            participantes = users;

                            users.forEach(user ->
                            {
                                user.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot d) {
                                            adapterSpinner.add(UsuarioMapper.mapBasicsParcelable(d));
                                        }
                                    });
                            });

                            List< String > pagador =
                                    (List< String >) task.getResult().get("pagador");
                            db.collection("users").document(pagador.get(0)).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        adapterSpinner.add(UsuarioMapper.mapBasicsParcelable(task.getResult()));
                                    }
                                }
                            });
                        }
                    }
                });




        totalMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkBoxesActivated = !s.toString().isEmpty();
                if(s.toString().isEmpty()){
                    cantidadTotal = 0;
                }else{
                    cantidadTotal = Double.parseDouble(s.toString());
                }


                UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                        new UsersFormItemsListaAdapter(usuariosDelPago, cantidadTotal,
                                    checkBoxesActivated,usuarioSeleccionado,changeMoneyActivated);
                usersToAdd.setAdapter(usersFormItemsListaAdapter);
            }
        });
        totalMoney.setFilters(new InputFilter[]{new DecimalFilterForInput(2)});

        totalMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkBoxesActivated = !s.toString().isEmpty();
                if(s.toString().isEmpty()){
                    cantidadTotal = 0;
                }else{
                    cantidadTotal = Double.parseDouble(s.toString());
                }


                UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                        new UsersFormItemsListaAdapter(usuariosDelPago, cantidadTotal,
                                checkBoxesActivated,usuarioSeleccionado,changeMoneyActivated);
                usersToAdd.setAdapter(usersFormItemsListaAdapter);
            }
        });
        totalMoney.setFilters(new InputFilter[]{new DecimalFilterForInput(2)});

        btnCreateNewItemPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<UsuarioParaParcelable, Double> usersSelected = ((UsersFormItemsListaAdapter)
                        usersToAdd.getAdapter()).getUsersSelected();
                if(checkAllFields()){
                    if(((UsersFormItemsListaAdapter)usersToAdd.getAdapter()).allMoneyIspaid()){
                        if(usersSelected.size() > 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            LayoutInflater inflater = builder.create().getLayoutInflater();
                            builder.setView(inflater.inflate(R.layout.dialog_warning_create_item_pago,
                                    null)).setPositiveButton(R.string.acceptBtn,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            ItemPagoConjunto itemPago = new ItemPagoConjunto(
                                                    name.getText().toString(), usersSelected);

                                            saveInDataBase(itemPago);

                                            Intent intent = new Intent();
                                            intent.putExtra("NEW_ITEM",itemPago);
                                            setResult(RESULT_OK,intent);
                                            finish();
                                        }
                                    });
                            builder.setView(inflater.inflate(R.layout.dialog_warning_create_item_pago,
                                    null)).setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            LayoutInflater inflater = builder.create().getLayoutInflater();
                            builder.setView(inflater.inflate(R.layout.dialog_warning_user_selected,
                                    null)).setPositiveButton(R.string.acceptBtn,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                            });


                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        LayoutInflater inflater = builder.create().getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_warning_not_all_pay,
                                null)).setPositiveButton(R.string.acceptBtn,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });


                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });

        moneyChangeActivatedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMoneyActivated = moneyChangeActivatedCheckBox.isChecked();

                UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                        new UsersFormItemsListaAdapter(usuariosDelPago, cantidadTotal,
                                checkBoxesActivated,usuarioSeleccionado,changeMoneyActivated);
                usersToAdd.setAdapter(usersFormItemsListaAdapter);
            }
        });

    }

    private void saveInDataBase(ItemPagoConjunto itemPago) {
        Map<String, Object> itemsPagoConj = new HashMap<>();
        Map<String, Double> usersWithMoney = new HashMap<>();
        for(Map.Entry<UsuarioParaParcelable, Double> u : itemPago.getPagos().entrySet()){
            usersWithMoney.put(u.getKey().getId(),u.getValue());
        }
        itemsPagoConj.put(itemPago.getNombre(),usersWithMoney);

        db.collection("pagosConjuntos").document(pagoConjunto.getId())
                .update("itemsPago", FieldValue.arrayUnion(itemsPagoConj)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("FIREBASE SET", "Se añadió el objeto");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FIRBASE SET", "Error writing document", e);
                    }
                });

    }

    private boolean checkAllFields() {
        if(name.getText().toString().trim().isEmpty()){
            name.setError("Este campo es obligatorio");
            return false;
        }
        if(totalMoney.getText().toString().trim().isEmpty()){
            totalMoney.setError("Este campo es obligatorio");
            return false;
        }

        return true;
    }
}
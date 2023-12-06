package com.moneyguardian;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.adapters.UsersFormItemsListaAdapter;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.DecimalFilterForInput;

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

    private FirebaseFirestore db;
    private UsersFormItemsListaAdapter usersFormItemsListaAdapter;
    private String itemPagoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_items_lista_pago);
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = FirebaseFirestore.getInstance();
        totalMoney = findViewById(R.id.editTextTotalMoneyItemPago);
        btnCreateNewItemPago = findViewById(R.id.btnCreteItemPago);
        name = findViewById(R.id.formItemPagoNameTextField);
        moneyChangeActivatedCheckBox = findViewById(R.id.checkBoxChangeMoney);

        usersToAdd = findViewById(R.id.recicledViewUsersToSelect);
        usersToAdd.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        usersToAdd.setLayoutManager(layoutManager);

        pagoConjunto = getIntent().getExtras().getParcelable("PAGO");

        usuariosDelPago = pagoConjunto.getParticipantes();


        whoPaysSpinner = findViewById(R.id.spinnerWhoPays);
        ArrayAdapter<UsuarioParaParcelable> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, usuariosDelPago);
        whoPaysSpinner.setAdapter(adapterSpinner);
        whoPaysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usuarioSeleccionado = usuariosDelPago.get(position);

                HashMap<UsuarioParaParcelable, Double> usuarios = usersFormItemsListaAdapter.getUsersSelected();

                usersFormItemsListaAdapter.changeSelecctedUser(usuarioSeleccionado, usuarios);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (getIntent().getExtras().getParcelable("ITEM") != null) {
            ItemPagoConjunto itemPagoConjunto = getIntent().getExtras().getParcelable("ITEM");
            name.setText(itemPagoConjunto.getNombre());
            totalMoney.setText("" + itemPagoConjunto.getMoney());
            for (int i = 0; i < pagoConjunto.getParticipantes().size(); i++) {
                if (pagoConjunto.getParticipantes().get(i).getId().equals(itemPagoConjunto.getUserThatPays().getId())) {
                    whoPaysSpinner.setSelection(i);
                    usuarioSeleccionado = pagoConjunto.getParticipantes().get(i);
                    itemPagoid = itemPagoConjunto.getId();
                    cantidadTotal = itemPagoConjunto.getMoney();
                    checkBoxesActivated = true;
                    break;
                }
            }
        } else {
            itemPagoid = UUID.randomUUID().toString();
            for (int i = 0; i < pagoConjunto.getParticipantes().size(); i++) {
                if (pagoConjunto.getParticipantes().get(i).getId().equals(pagoConjunto.getOwner())) {
                    whoPaysSpinner.setSelection(i);
                    usuarioSeleccionado = pagoConjunto.getParticipantes().get(i);
                    break;
                }
            }
        }

        usersFormItemsListaAdapter = new UsersFormItemsListaAdapter(usuariosDelPago, cantidadTotal, checkBoxesActivated, changeMoneyActivated, usuarioSeleccionado);

        usersToAdd.setAdapter(usersFormItemsListaAdapter);

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
                if (s.toString().isEmpty()) {
                    cantidadTotal = 0;
                } else {
                    cantidadTotal = Double.parseDouble(s.toString());
                }

                usersFormItemsListaAdapter.activateAllCheckBox(checkBoxesActivated);
                usersFormItemsListaAdapter.changeCantidadTotal(cantidadTotal);
            }
        });
        totalMoney.setFilters(new InputFilter[]{new DecimalFilterForInput(2)});

        btnCreateNewItemPago.setOnClickListener(v -> {
            HashMap<UsuarioParaParcelable, Double> usersSelected = ((UsersFormItemsListaAdapter) usersToAdd.getAdapter()).getUsersSelected();
            if (checkAllFields()) {
                if (usersFormItemsListaAdapter.allMoneyIspaid()) {
                    if (usersSelected.size() > 0) {
                        if (checkIfYouAreLonely(v, usersSelected)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            LayoutInflater inflater = builder.create().getLayoutInflater();
                            builder.setView(inflater.inflate(R.layout.dialog_warning_create_item_pago, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> {

                                ItemPagoConjunto itemPago = new ItemPagoConjunto(itemPagoid, name.getText().toString(), usersSelected, usuarioSeleccionado, cantidadTotal);

                                saveInDataBase(itemPago);

                                Intent intent = new Intent();
                                intent.putExtra("NEW_ITEM", itemPago);
                                setResult(RESULT_OK, intent);
                                finish();
                            });
                            builder.setView(inflater.inflate(R.layout.dialog_warning_create_item_pago, null)).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        LayoutInflater inflater = builder.create().getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_warning_user_selected, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> dialog.cancel());


                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    LayoutInflater inflater = builder.create().getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dialog_warning_not_all_pay, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> dialog.cancel());


                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        moneyChangeActivatedCheckBox.setOnClickListener(v -> {
            changeMoneyActivated = moneyChangeActivatedCheckBox.isChecked();

            usersFormItemsListaAdapter.activateEditMoney(changeMoneyActivated);

        });

    }

    private boolean checkIfYouAreLonely(View v, HashMap<UsuarioParaParcelable, Double> usersSelected) {
        if (usersSelected.size() == 1 && usersSelected.containsKey(usuarioSeleccionado)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            LayoutInflater inflater = builder.create().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_lonely, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        }
        return true;
    }

    private void saveInDataBase(ItemPagoConjunto itemPago) {
        Map<String, Object> itemsPagoConj = new HashMap<>();
        itemsPagoConj.put("nombre", itemPago.getNombre());
        Map<String, Double> usersWithMoney = new HashMap<>();
        for (Map.Entry<UsuarioParaParcelable, Double> u : itemPago.getPagos().entrySet()) {
            usersWithMoney.put(u.getKey().getId(), u.getValue());
        }

        itemsPagoConj.put("totalDinero", cantidadTotal);

        itemsPagoConj.put("UsuariosConPagos", usersWithMoney);
        itemsPagoConj.put("usuarioPago", usuarioSeleccionado.getId());


        db.collection("pagosConjuntos").document(pagoConjunto.getId()).collection("itemsPago").document(itemPago.getId()).set(itemsPagoConj).addOnSuccessListener(unused -> Log.i("FIREBASE SET", "Se añadió el objeto")).addOnFailureListener(e -> Log.w("FIRBASE SET", "Error writing document", e));

    }

    private boolean checkAllFields() {
        if (name.getText().toString().trim().isEmpty()) {
            name.setError("Este campo es obligatorio");
            return false;
        }
        if (totalMoney.getText().toString().trim().isEmpty()) {
            totalMoney.setError("Este campo es obligatorio");
            return false;
        }

        return true;
    }
}
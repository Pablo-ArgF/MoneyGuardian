package com.moneyguardian;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.DecimalFilterForInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormItemsListaPago extends AppCompatActivity {

    RecyclerView usersToAdd;
    Spinner whoPaysSpinner;
    EditText totalMoney;
    Button btnCreateNewItemPago;
    EditText name;
    CheckBox moneyChangeActivatedCheckBox;
    private List<UsuarioParaParcelable> usuariosDelPago;
    private List<UsuarioParaParcelable> usersNeedToPay;
    private double cantidadTotal;

    private boolean checkBoxesActivated;
    private boolean changeMoneyActivated;
    private UsuarioParaParcelable usuarioSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_items_lista_pago);
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalMoney = (EditText) findViewById(R.id.editTextTotalMoneyItemPago);
        btnCreateNewItemPago = (Button) findViewById(R.id.btnCreteItemPago);
        name = (EditText) findViewById(R.id.formItemPagoNameTextField);
        moneyChangeActivatedCheckBox = (CheckBox) findViewById(R.id.checkBoxChangeMoney);

        usersToAdd = (RecyclerView)findViewById(R.id.recicledViewUsersToSelect);
        usersToAdd.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        usersToAdd.setLayoutManager(layoutManager);

        usuariosDelPago = getIntent().getExtras().getParcelableArrayList("USERS_OF_PAYMENT");
        usersNeedToPay = new ArrayList<>(usuariosDelPago);

        UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                new UsersFormItemsListaAdapter(usersNeedToPay, cantidadTotal,checkBoxesActivated
                                                        ,usuarioSeleccionado,changeMoneyActivated);

        usersToAdd.setAdapter(usersFormItemsListaAdapter);

        whoPaysSpinner = (Spinner) findViewById(R.id.spinnerWhoPays);
        ArrayAdapter<UsuarioParaParcelable> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,usersNeedToPay);
        whoPaysSpinner.setAdapter(adapterSpinner);
        whoPaysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usersNeedToPay = new ArrayList<>(usuariosDelPago);
                usersNeedToPay.remove(position);
                usuarioSeleccionado = usuariosDelPago.get(position);

                UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                        new UsersFormItemsListaAdapter(usersNeedToPay, cantidadTotal,
                                    checkBoxesActivated,usuarioSeleccionado,changeMoneyActivated);
                usersToAdd.setAdapter(usersFormItemsListaAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                        new UsersFormItemsListaAdapter(usersNeedToPay, cantidadTotal,
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
                                            //TODO:Guardar el item en la base de datos


                                            ItemPagoConjunto itemPago = new ItemPagoConjunto(
                                                    name.getText().toString(), usersSelected);

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
                        new UsersFormItemsListaAdapter(usersNeedToPay, cantidadTotal,
                                checkBoxesActivated,usuarioSeleccionado,changeMoneyActivated);
                usersToAdd.setAdapter(usersFormItemsListaAdapter);
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
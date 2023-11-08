package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.moneyguardian.modelo.Usuario;

import java.util.List;

public class FormItemsListaPago extends AppCompatActivity {

    RecyclerView usersToAdd;
    List<Usuario> usuariosDelPago;
    private double cantidadPorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_items_lista_pago);

        usersToAdd = (RecyclerView)findViewById(R.id.recicledViewUsersToSelect);
        usersToAdd.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        usersToAdd.setLayoutManager(layoutManager);

        UsersFormItemsListaAdapter usersFormItemsListaAdapter =
                new UsersFormItemsListaAdapter(
                        getIntent().getExtras().getParcelableArrayList("USERS_OF_PAYMENT"),
                        cantidadPorUsuario);

        usersToAdd.setAdapter(usersFormItemsListaAdapter);
    }
}
package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.ui.ItemPagosFragment;
import com.moneyguardian.ui.ListaAmigosFragment;
import com.moneyguardian.ui.ListaPagosFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        ListaAmigosFragment amigosFragment = ListaAmigosFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                amigosFragment).commit();*/
        //PagosConjuntosFragment pagosConjuntosFragment = PagosConjuntosFragment.newInstance();


        HashMap<Usuario, Integer> userMap = new HashMap<>();

        // Generar 10 entradas ficticias
        for (int i = 1; i <= 10; i++) {
            Usuario usuario = new Usuario("Usuario " + i, "usuario" + i + "@example.com", null, null);
            int valor = i - 3; // Valor ficticio
            userMap.put(usuario, valor);
        }


        ItemPagosFragment fragment = ItemPagosFragment.newInstance("Prueba",userMap);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_amigos_pagos,
                fragment).commit();
    }
}
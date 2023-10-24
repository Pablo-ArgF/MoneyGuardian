package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moneyguardian.ui.PagosConjuntosFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        ListaAmigosFragment amigosFragment = ListaAmigosFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                amigosFragment).commit();*/
        PagosConjuntosFragment pagosConjuntosFragment = PagosConjuntosFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_amigos_pagos,
                pagosConjuntosFragment).commit();
    }
}
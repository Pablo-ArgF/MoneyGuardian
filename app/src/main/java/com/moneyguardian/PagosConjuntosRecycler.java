package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class PagosConjuntosRecycler extends AppCompatActivity {

    // Identificadores de intent

    public static final String PAGO_CONJUNTO_SELECCIONADO = "pago_conjunto_seleccionado";
    public static final String PAGO_CONJUNTO_CREADO = "pago_conjunto_creado";

    // Identificador de activiy
    // TODO a cambiar si es necesario
    public static final int GESTION_ACTIVITY = 1;

    // Modelo de datos

    // TODO private ArrayList<PagoConjunto> listaPagosConjuntos;
    // TODO private PagoConjunto pagoConjunto;
    private RecyclerView listaPagosConjuntosView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_conjuntos_recycler);

        // TODO cargarPagos();
    }
}
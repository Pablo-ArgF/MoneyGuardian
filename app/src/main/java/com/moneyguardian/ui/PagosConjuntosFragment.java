package com.moneyguardian.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moneyguardian.PagosConjuntosListaAdapter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.PagoConjunto;

import java.util.ArrayList;

public class PagosConjuntosFragment extends Fragment {

    // Identificadores de intent

    public static final String PAGO_CONJUNTO_SELECCIONADO = "pago_conjunto_seleccionado";
    public static final String PAGO_CONJUNTO_CREADO = "pago_conjunto_creado";

    // Identificador de activiy
    // TODO Necesario?
    public static final int GESTION_ACTIVITY = 1;

    // Modelo de datos

    private ArrayList<PagoConjunto> listaPagosConjuntos = new ArrayList<>();
    private PagoConjunto pagoConjunto;
    private RecyclerView listaPagosConjuntosView;

    public PagosConjuntosFragment() {
        // Required empty public constructor
    }

    public static PagosConjuntosFragment newInstance() {
        PagosConjuntosFragment fragment = new PagosConjuntosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pagos_conjuntos, container, false);

        if (getArguments() != null) {

            // cargarPagos

            listaPagosConjuntosView = (RecyclerView) root.findViewById(R.id.recyclerPagosConjuntos);

            listaPagosConjuntosView.setHasFixedSize(true);

            PagosConjuntosListaAdapter pagosConjuntosListaAdapter =
                    new PagosConjuntosListaAdapter(listaPagosConjuntos,
                            new PagosConjuntosListaAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(PagoConjunto pago) {
                                    clickonItem(pagoConjunto);
                                }
                            });

            listaPagosConjuntosView.setAdapter(pagosConjuntosListaAdapter);
        }

        // TODO Cargamos aquí la lista de pagos? O en un onACtivityResult?
        PagosConjuntosListaAdapter pagosConjuntosListaAdapter =
                new PagosConjuntosListaAdapter(listaPagosConjuntos, (pago) -> {
                    clickonItem(pago);
                });

        listaPagosConjuntosView.setAdapter(pagosConjuntosListaAdapter);

        // Inflate the layout for this fragment
        return root;
    }

    // Click del item del adapter
    public void clickonItem(PagoConjunto pagoConjunto) {
        Log.i("Click adapter", "Item Clicked " + pagoConjunto.toString());
        // TODO sin hacer hasta que llegue la funcionalidad y los layouts
        // Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();

        // Paso el modo de apertura
        //Intent intent = new Intent(MainRecycler.this, ShowMovie.class);
        //intent.putExtra(PELICULA_SELECCIONADA, peli);

        // Ahora hace una transición
        //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
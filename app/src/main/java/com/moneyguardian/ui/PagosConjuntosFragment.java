package com.moneyguardian.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moneyguardian.PagosConjuntosListaAdapter;
import com.moneyguardian.R;

public class PagosConjuntosFragment extends Fragment {

    // Identificadores de intent

    public static final String PAGO_CONJUNTO_SELECCIONADO = "pago_conjunto_seleccionado";
    public static final String PAGO_CONJUNTO_CREADO = "pago_conjunto_creado";

    // Identificador de activiy
    // TODO Necesario?
    public static final int GESTION_ACTIVITY = 1;

    // Modelo de datos

    // TODO private ArrayList<PagoConjunto> listaPagosConjuntos;
    // TODO private PagoConjunto pagoConjunto;
    private RecyclerView listaPagosConjuntosView;

    public PagosConjuntosFragment() {
        // Required empty public constructor
    }

    public static PagosConjuntosFragment newInstance(String param1, String param2) {
        PagosConjuntosFragment fragment = new PagosConjuntosFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // mParam1 = getArguments().getString(ARG_PARAM1);

            // cargarPagos

            //listaPagosConjuntosView = (RecyclerView) findViewById(R.id.reciclerView);

            listaPagosConjuntosView.setHasFixedSize(true);

            //PagosConjuntosListaAdapter pagosConjuntosListaAdapter =
            // new PagosConjuntosListaAdapter(listaPagosConjuntos,
            // new ListaPeliculaAdapter.OnItemClickListener() {
            // @Override
            // public void onItemClick(PagoConjunto pago){ clickonitem(pagoConjunto);}
            // });

            //listaPagosConjuntosView.setAdapter(pagosConjuntosAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_pagos_conjuntos, container, false);

        TextView nombrePago = root.findViewById(R.id.textNombrePagoConjunto);
        //nombrePago.setText(nombrePagoConjunto);

        // TODO Cargamos aquí la lista de pagos? O en un onACtivityResult?
        // PagosConjuntosListaAdapter pagosConjuntosListaAdapter =
        // new PagosConjuntosListaAdapter(listaPagosConjuntos, (pago) -> {
        // cliconItem(pago);
        // });

        //listaPagosConjuntosView.setAdapter(pagosConjuntosListaAdapter);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pagos_conjuntos, container, false);
    }
}
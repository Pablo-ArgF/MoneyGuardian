package com.moneyguardian.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.moneyguardian.FormularioPagoConjuntoActivity;
import com.moneyguardian.MainActivity;
import com.moneyguardian.PagosConjuntosListaAdapter;
import com.moneyguardian.R;
import com.moneyguardian.SocialActivity;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.Usuario;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

        listaPagosConjuntosView = (RecyclerView) root.findViewById(R.id.recyclerPagosConjuntos);
        listaPagosConjuntosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(root.getContext().getApplicationContext());
        listaPagosConjuntosView.setLayoutManager(layoutManager);


        listaPagosConjuntos = cargarDatos();

        PagosConjuntosListaAdapter pagosConjuntosListaAdapter =
                new PagosConjuntosListaAdapter(listaPagosConjuntos,
                        new PagosConjuntosListaAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(PagoConjunto pago) {
                                clickonItem(pago);
                            }
                        });

        listaPagosConjuntosView.setAdapter(pagosConjuntosListaAdapter);

        Button btnNuevoPago = root.findViewById(R.id.btnNewPagoConjunto);
        btnNuevoPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO problemas con los fragments...
                // Intent intent = new Intent(SocialActivity.this, FormularioPagoConjuntoActivity.class);

            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    private ArrayList<PagoConjunto> cargarDatos() {

        Usuario u1 = new Usuario("Usuario1", "usuario1@gmail.com", null, null);
        Usuario u2 = new Usuario("Usuario2", "usuario2@gmail.com", null, null);
        Usuario u3 = new Usuario("Usuario3", "usuario3@gmail.com", null, null);
        Usuario u4 = new Usuario("Usuario4", "usuario4@gmail.com", null, null);

        u1.setAmigos(Arrays.asList(u2, u3, u4));
        u2.setAmigos(Arrays.asList(u1, u3));
        u3.setAmigos(Arrays.asList(u1, u2, u4));
        u4.setAmigos(Arrays.asList(u1, u3));

        HashMap<Usuario, Integer> userPays1 = new HashMap<Usuario, Integer>();
        userPays1.put(u1, 200);
        userPays1.put(u2, -100);
        userPays1.put(u3, -25);
        userPays1.put(u4, -75);
        HashMap<Usuario, Integer> userPays2 = new HashMap<Usuario, Integer>();
        userPays2.put(u1, 200);
        userPays2.put(u2, -200);
        HashMap<Usuario, Integer> userPays3 = new HashMap<Usuario, Integer>();
        userPays3.put(u1, -100);
        userPays3.put(u2, 150);
        userPays3.put(u3, -50);
        HashMap<Usuario, Integer> userPays4 = new HashMap<Usuario, Integer>();
        userPays4.put(u3, 25);
        userPays4.put(u4, -25);
        HashMap<Usuario, Integer> userPays5 = new HashMap<Usuario, Integer>();
        userPays5.put(u2, -100);
        userPays5.put(u3, 200);
        userPays5.put(u4, -100);

        ItemPagoConjunto ip1 = new ItemPagoConjunto("Item Pago 1", userPays1);
        ItemPagoConjunto ip2 = new ItemPagoConjunto("Item Pago 2", userPays2);
        ItemPagoConjunto ip3 = new ItemPagoConjunto("Item Pago 3", userPays3);
        ItemPagoConjunto ip4 = new ItemPagoConjunto("Item Pago 4", userPays4);
        ItemPagoConjunto ip5 = new ItemPagoConjunto("Item Pago 5", userPays5);

        PagoConjunto pg1 = new PagoConjunto("Pago Conjunto 1",
                new Date(), Arrays.asList(u1, u2, u3, u4), Arrays.asList(ip1, ip2, ip3));
        PagoConjunto pg2 = new PagoConjunto("Pago Conjunto 2",
                new Date(), Arrays.asList(u3, u4), Arrays.asList(ip4));
        PagoConjunto pg3 = new PagoConjunto("Pago Conjunto 3",
                new Date(), Arrays.asList(u2, u3, u4), Arrays.asList(ip5));

        u1.setMisPagosConjuntos(Arrays.asList(pg1));
        u2.setMisPagosConjuntos(Arrays.asList(pg1, pg3));
        u3.setMisPagosConjuntos(Arrays.asList(pg1, pg3, pg2));
        u4.setMisPagosConjuntos(Arrays.asList(pg1, pg3, pg2));

        ArrayList<PagoConjunto> result = new ArrayList<>();
        result.add(pg1);
        result.add(pg2);
        result.add(pg3);

        return result;
    }


    // Click del item del adapter
    public void clickonItem(PagoConjunto pagoConjunto) {
        ListaPagosFragment listaPagosFragment = ListaPagosFragment.newInstance
                (pagoConjunto.getItems(), pagoConjunto.getNombre());

        getParentFragmentManager().beginTransaction().
                replace(R.id.fragment_container_amigos_pagos, listaPagosFragment).commit();
    }
}
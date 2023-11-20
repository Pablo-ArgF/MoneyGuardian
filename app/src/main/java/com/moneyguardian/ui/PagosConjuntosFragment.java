package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.moneyguardian.adapters.PagosConjuntosListaAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.moneyguardian.FormularioPagoConjuntoActivity;
import com.moneyguardian.R;
import com.moneyguardian.modelo.PagoConjunto;

import java.util.ArrayList;
import java.util.Date;

public class PagosConjuntosFragment extends Fragment {

    // Identificadores de intent

    public static final String PAGO_CONJUNTO_SELECCIONADO = "pago_conjunto_seleccionado";
    public static final String PAGO_CONJUNTO_CREADO = "pago_conjunto_creado";

    // Identificador de activiy
    public static final int GESTION_ACTIVITY = 1;

    // Modelo de datos

    private ArrayList<PagoConjunto> listaPagosConjuntos = new ArrayList<>();
    private PagosConjuntosListaAdapter pagosConjuntosListaAdapter;
    private PagoConjunto pagoConjunto;
    private RecyclerView listaPagosConjuntosView;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;


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


        listaPagosConjuntos = new ArrayList<>();
        cargarDatos();

        pagosConjuntosListaAdapter =
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
                // Al ser un fragment, hay que usar getActivity para obtener el contexto
                Intent intent = new Intent(getActivity(), FormularioPagoConjuntoActivity.class);
                startActivityForResult(intent, GESTION_ACTIVITY);
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    private void cargarDatos() {

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        CollectionReference pagosRef = db.collection("pagosConjuntos");
        ArrayList<PagoConjunto> pagos = new ArrayList<>();
        pagosRef.
                whereArrayContains("pagador", auth.getCurrentUser().getUid()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = (String) document.getData().get("nombre");
                                Uri imagen = null;
                                if (document.getData().get("imagen") != null) {
                                    imagen = Uri.parse((String) document.getData().get("imagen"));
                                }
                                Date fechaPago = ((Timestamp) document.getData().get("fechaPago")).toDate();
                                Date fechaLimite = ((Timestamp) document.getData().get("fechaLimite")).toDate();

                                // TODO separar
                                if(nombre == null || fechaLimite == null || fechaPago == null){
                                    throw new RuntimeException(String.valueOf(R.string.ErrorBaseDatosPago));
                                }
                                pagos.add(new PagoConjunto(nombre, fechaPago, new ArrayList<>(), imagen, fechaLimite));
                            }
                            pagosConjuntosListaAdapter.updateList(pagos);

                        } else {
                            Log.i("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    // Click del item del adapter
    public void clickonItem(PagoConjunto pagoConjunto) {
        ListaPagosFragment listaPagosFragment = ListaPagosFragment.newInstance(pagoConjunto);

        getParentFragmentManager().beginTransaction().
                replace(R.id.fragmentContainerMain, listaPagosFragment).addToBackStack(null).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos a qué petición se está respondiendo

        if (requestCode == GESTION_ACTIVITY) {
            // Nos aseguramos que el resultado fue OK
            if (resultCode == RESULT_OK) {

                Snackbar.make(getActivity().findViewById(R.id.fragmentPagosConjuntos), R.string.PagoConjuntoCreado, Snackbar.LENGTH_LONG).show();

                this.pagoConjunto = data.getParcelableExtra(PAGO_CONJUNTO_CREADO);

                // Refrescar el RecyclerView, lo ponemos a 0 y cargamos los datos de la BD
                this.listaPagosConjuntos = new ArrayList<>();
                cargarDatos();
            }
        }
    }
}
package com.moneyguardian.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.moneyguardian.FormularioPagoConjuntoActivity;
import com.moneyguardian.R;
import com.moneyguardian.adapters.PagosConjuntosListaAdapter;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagosConjuntosFragment extends Fragment {

    // Identificador de activiy
    public static final int GESTION_ACTIVITY = 1;

    // Modelo de datos

    private PagosConjuntosListaAdapter pagosConjuntosListaAdapter;

    private FirebaseFirestore db;
    private ListenerRegistration docListener;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pagos_conjuntos, container, false);
        db = FirebaseFirestore.getInstance();
        addListenerToCollection();


        RecyclerView listaPagosConjuntosView = root.findViewById(R.id.recyclerPagosConjuntos);
        listaPagosConjuntosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext().getApplicationContext());
        listaPagosConjuntosView.setLayoutManager(layoutManager);


        ArrayList<PagoConjunto> listaPagosConjuntos = new ArrayList<>();
        cargarDatos();

        pagosConjuntosListaAdapter = new PagosConjuntosListaAdapter(listaPagosConjuntos,
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

        // Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ArrayList<PagoConjunto> pagos = new ArrayList<>();

        db.collection("users").document(auth.getCurrentUser().getUid()).
                get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                List<DocumentReference> referenciasPagos = (List<DocumentReference>) documentSnapshot.get("pagosConjuntos");

                if(referenciasPagos != null) {

                    for (DocumentReference document : referenciasPagos) {

                        db.collection("pagosConjuntos").document(document.getId()).
                                get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document) {
                                if (document.getData() != null) {
                                    String nombre = (String) document.getData().get("nombre");
                                    Uri imagen = null;
                                    if (document.getData().get("imagen") != null) {
                                        imagen = Uri.parse((String) document.getData().get("imagen"));
                                    }
                                    Date fechaPago = ((Timestamp) document.getData().get("fechaPago")).toDate();
                                    Date fechaLimite = ((Timestamp) document.getData().get("fechaLimite")).toDate();

                                    String owner = ((ArrayList<String>) document.getData().get("pagador")).get(0);

                                    List< DocumentReference> users =
                                            (List< DocumentReference >) document.getData().get("participantes");

                                    List<UsuarioParaParcelable> participantes = new ArrayList<>();

                                    users.forEach(user ->
                                    {
                                        user.get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot d) {
                                                        participantes.add(UsuarioMapper.mapBasicsParcelable(d));
                                                    }
                                                });
                                    });

                                    List<ItemPagoConjunto> itemsPago = new ArrayList<>();

                                    Uri finalImagen = imagen;

                                    db.collection("pagosConjuntos")
                                            .document(document.getId()).collection("itemsPago")
                                            .orderBy("nombre").get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<DocumentSnapshot> itemsPagoSnapshot = queryDocumentSnapshots.getDocuments();

                                                    for (DocumentSnapshot itemPago : itemsPagoSnapshot) {
                                                        HashMap<UsuarioParaParcelable, Double> cantidadesConUsers = new HashMap<>();
                                                        String id = itemPago.getId();
                                                        String nombre = itemPago.getString("nombre");
                                                        HashMap<String, Double> cantidadesConUsersReferences =
                                                                (HashMap<String, Double>) itemPago
                                                                        .get("UsuariosConPagos");

                                                        for (Map.Entry<String, Double> user : cantidadesConUsersReferences.entrySet()) {
                                                            cantidadesConUsers.put(new UsuarioParaParcelable(user.getKey()), user.getValue());
                                                        }


                                                        itemsPago.add(new ItemPagoConjunto(id, nombre, cantidadesConUsers));
                                                    }

                                                    if (nombre == null || fechaLimite == null || fechaPago == null) {
                                                        throw new RuntimeException(String.valueOf(R.string.ErrorBaseDatosPago));
                                                    }

                                                    Log.i("Firebase GET", document.getData().toString());

                                                    pagos.add(new PagoConjunto(document.getId(), nombre,
                                                            fechaPago, new ArrayList<>(participantes),
                                                            finalImagen, fechaLimite, itemsPago, owner));
                                                    pagosConjuntosListaAdapter.updateList(pagos);
                                                }
                                            });
                                }else{
                                    pagosConjuntosListaAdapter.updateList(new ArrayList<>());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    // Click del item del adapter
    public void clickonItem(PagoConjunto pagoConjunto) {
        ListaPagosFragment listaPagosFragment = ListaPagosFragment.newInstance(pagoConjunto);

        getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, listaPagosFragment).addToBackStack(null).commit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (docListener != null) {
            docListener.remove();
        }
    }

    private void addListenerToCollection() {
        if (docListener == null) {
            docListener = db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w("LISTENER", "Litener Failed");
                                return;
                            }

                            cargarDatos();
                        }
                    });
        }
    }
}
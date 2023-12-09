package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;
import static androidx.core.util.ObjectsCompat.requireNonNull;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.FormularioPagoConjuntoActivity;
import com.moneyguardian.MainActivity;
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
    public static final String PAGO_CONJUNTO_CREADO = "PAGO";

    // Modelo de datos
    SwipeRefreshLayout swipeRefreshLayout;
    private PagosConjuntosListaAdapter pagosConjuntosListaAdapter;
    private MainActivity mainActivity;
    private FirebaseFirestore db;
    private Uri imagen;


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
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pagos_conjuntos, container, false);
        mainActivity = (MainActivity) getActivity();
        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshPagosConjuntos);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            cargarDatos();
            swipeRefreshLayout.setRefreshing(false);
        });


        RecyclerView listaPagosConjuntosView = root.findViewById(R.id.recyclerPagosConjuntos);
        listaPagosConjuntosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext().getApplicationContext());
        listaPagosConjuntosView.setLayoutManager(layoutManager);


        ArrayList<PagoConjunto> listaPagosConjuntos = new ArrayList<>();
        if (this.mainActivity.getPagosConjuntos() == null || mainActivity.getPagosConjuntos().size() == 0) cargarDatos();
        else listaPagosConjuntos = new ArrayList<>(mainActivity.getPagosConjuntos());

        pagosConjuntosListaAdapter = new PagosConjuntosListaAdapter(listaPagosConjuntos, pago -> clickonItem(pago));

        listaPagosConjuntosView.setAdapter(pagosConjuntosListaAdapter);

        Button btnNuevoPago = root.findViewById(R.id.btnNewPagoConjunto);
        btnNuevoPago.setOnClickListener(v -> {
            // Al ser un fragment, hay que usar getActivity para obtener el contexto
            Intent intent = new Intent(getActivity(), FormularioPagoConjuntoActivity.class);
            startActivityForResult(intent, GESTION_ACTIVITY);
        });

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GESTION_ACTIVITY) {
            assert data != null;
            PagoConjunto pagoConjuntoNuevo = requireNonNull(data.getExtras()).getParcelable(PAGO_CONJUNTO_CREADO);
            mainActivity.addPagoCOnjunto(pagoConjuntoNuevo);
            pagosConjuntosListaAdapter.addItem(pagoConjuntoNuevo);
        }
    }

    private void cargarDatos() {

        // Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ArrayList<PagoConjunto> pagos = new ArrayList<>();

        db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {

            List<DocumentReference> referenciasPagos = (List<DocumentReference>) documentSnapshot.get("pagosConjuntos");

            if (referenciasPagos.size() != 0) {

                for (DocumentReference document : referenciasPagos) {

                    db.collection("pagosConjuntos").document(document.getId()).get().addOnSuccessListener(document1 -> {
                        if (document1.getData() != null) {
                            String nombre = (String) document1.getData().get("nombre");
                            imagen = null;
                            if (document1.getData().get("imagen") != null) {
                                imagen = Uri.parse((String) document1.getData().get("imagen"));
                            }
                            Date fechaPago = ((Timestamp) document1.getData().get("fechaPago")).toDate();
                            Date fechaLimite = ((Timestamp) document1.getData().get("fechaLimite")).toDate();

                            String owner = ((DocumentReference) document1.getData().get("pagador")).getId();

                            List<DocumentReference> users = (List<DocumentReference>) document1.getData().get("participantes");

                            List<UsuarioParaParcelable> participantes = new ArrayList<>();

                            List<Task> taskList = new ArrayList<>();

                            users.forEach(user -> {
                                taskList.add(user.get());
                            });

                            Tasks.whenAllSuccess(taskList).addOnSuccessListener(objects -> {
                                for (Object d : objects) {
                                    participantes.add((UsuarioMapper.mapBasicsParcelable((DocumentSnapshot) d)));
                                }
                                List<ItemPagoConjunto> itemsPago = new ArrayList<>();

                                Uri finalImagen = imagen;

                                db.collection("pagosConjuntos").document(document1.getId()).collection("itemsPago").orderBy("nombre").get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    List<DocumentSnapshot> itemsPagoSnapshot = queryDocumentSnapshots.getDocuments();

                                    for (DocumentSnapshot itemPago : itemsPagoSnapshot) {
                                        HashMap<UsuarioParaParcelable, Double> cantidadesConUsers = new HashMap<>();
                                        String id = itemPago.getId();
                                        String nombre1 = itemPago.getString("nombre");
                                        Double cantidadTotal = itemPago.getDouble("totalDinero");
                                        HashMap<String, Double> cantidadesConUsersReferences = (HashMap<String, Double>) itemPago.get("UsuariosConPagos");

                                        for (Map.Entry<String, Double> user : cantidadesConUsersReferences.entrySet()) {
                                            cantidadesConUsers.put(new UsuarioParaParcelable(user.getKey()), user.getValue());
                                        }

                                        UsuarioParaParcelable userThatPays = new UsuarioParaParcelable(itemPago.getString("usuarioPago"));

                                        itemsPago.add(new ItemPagoConjunto(id, nombre1, cantidadesConUsers, userThatPays, cantidadTotal));
                                    }

                                    if (nombre == null) {
                                        throw new RuntimeException(String.valueOf(R.string.ErrorBaseDatosPago));
                                    }

                                    Log.i("Firebase GET", document1.getData().toString());

                                    pagos.add(new PagoConjunto(document1.getId(), nombre, fechaPago, new ArrayList<>(participantes), finalImagen, fechaLimite, itemsPago,
                                            owner));
                                    mainActivity.setPagosConjuntos(pagos);
                                    pagosConjuntosListaAdapter.updateList(pagos);
                                });
                            });
                        }
                    });
                }
            } else {
                mainActivity.setPagosConjuntos(new ArrayList<>());
                pagosConjuntosListaAdapter.updateList(new ArrayList<>());
            }
        });
    }

    // Click del item del adapter
    public void clickonItem(PagoConjunto pagoConjunto) {
        ListaPagosFragment listaPagosFragment = ListaPagosFragment.newInstance(pagoConjunto);

        getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, listaPagosFragment).addToBackStack(null).commit();
    }

}
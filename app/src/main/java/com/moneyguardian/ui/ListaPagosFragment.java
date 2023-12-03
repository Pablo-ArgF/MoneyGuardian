package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.moneyguardian.FormItemsListaPago;
import com.moneyguardian.R;
import com.moneyguardian.adapters.ItemListaAdapter;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.Animations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaPagosFragment extends Fragment {

    public static final int GESTION_ACTIVITY = 2;
    private static final String LISTA_PAGOS = "ListaPagos";
    private static final String NAME_PAGO = "Nombre";
    private static final String IMAGEN = "Imagen";
    private static final String PAGO_CONJUNTO = "Pago Conjunto";
    RecyclerView listItemsPagosView;
    private Uri imagen;
    private String namePago;
    private List<ItemPagoConjunto> listaPagos;
    FloatingActionButton mainOpenButton;
    FloatingActionButton btnAddNewItem;
    FloatingActionButton fabDelete;
    FloatingActionButton fabEdit;
    private PagoConjunto pagoConjunto;
    private ItemListaAdapter lpAdapter;

    private FirebaseFirestore db;
    private ListenerRegistration docListener;

    public static ListaPagosFragment newInstance(PagoConjunto param1) {
        ListaPagosFragment fragment = new ListaPagosFragment();
        Bundle args = new Bundle();
        args.putParcelable(PAGO_CONJUNTO, param1);
        args.putParcelableArrayList(LISTA_PAGOS, new ArrayList<>(param1.getItems()));
        args.putString(NAME_PAGO, param1.getNombre());
        args.putParcelable(IMAGEN, param1.getImagen());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pagoConjunto = getArguments().getParcelable(PAGO_CONJUNTO);
            listaPagos = getArguments().getParcelableArrayList(LISTA_PAGOS);
            namePago = getArguments().getString(NAME_PAGO);
            imagen = getArguments().getParcelable(IMAGEN);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        addListenerToCollection();
        //Mostramos el fragmento en el contenedor
        View root = inflater.inflate(R.layout.fragment_lista_pagos, container, false);
        TextView tvName = root.findViewById(R.id.namePagos);
        tvName.setText(namePago);
        ImageView ivImagen = root.findViewById(R.id.iconPago);
        if (imagen != null) Picasso.get().load(imagen).into(ivImagen);
        mainOpenButton = root.findViewById(R.id.floatingActionButtonMainPagoConjunto);
        btnAddNewItem = root.findViewById(R.id.btnNewItemPago);
        fabDelete = root.findViewById(R.id.floatingActionButtonDeletePagoConjunto);
        fabEdit = root.findViewById(R.id.floatingActionButtonEditPagoConjunto);

        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormItemsListaPago.class);
                intent.putExtra("PAGO", pagoConjunto);
                startActivityForResult(intent, GESTION_ACTIVITY);
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });


        new Animations(root).setOnClickAnimationAndVisibility(mainOpenButton,
                Arrays.asList(btnAddNewItem,fabDelete,fabEdit));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listItemsPagosView = view.findViewById(R.id.mainListPagosRecycler);
        listItemsPagosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext().getApplicationContext());
        listItemsPagosView.setLayoutManager(layoutManager);

        lpAdapter = new ItemListaAdapter(listaPagos, new ItemListaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ItemPagoConjunto itemPago) {
                clickonItem(itemPago);
            }
        });
        listItemsPagosView.setAdapter(lpAdapter);
    }

    private void clickonItem(ItemPagoConjunto itemPago) {

        ItemPagosFragment argumentoFragment = ItemPagosFragment.newInstance(itemPago, pagoConjunto);

        getParentFragmentManager().beginTransaction().
                replace(R.id.fragmentContainerMain, argumentoFragment).addToBackStack(null).commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GESTION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                assert data != null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (docListener != null) {
            docListener.remove();
        }
    }

    private void delete(){
        DocumentReference docReference = db.collection("pagosConjuntos").
                document(pagoConjunto.getId());

        docReference.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        for (UsuarioParaParcelable p : pagoConjunto.getParticipantes()) {
                            db.collection("users").document(p.getId()).update("pagosConjuntos",
                                    FieldValue.arrayRemove(docReference));
                        }
                        
                        db.collection("users").document(pagoConjunto.getOwner()).update("pagosConjuntos",
                                FieldValue.arrayRemove(docReference));
                        getParentFragmentManager().popBackStack();
                    }
                });
    }

    private void addListenerToCollection() {

        if (docListener == null) {
            docListener = db.collection("pagosConjuntos").document(pagoConjunto.getId())
                    .collection("itemsPago").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w("LISTENER", "Litener Failed");
                        return;
                    }

                    List<ItemPagoConjunto> itemsPago = new ArrayList<>();

                    if(!value.getDocumentChanges().isEmpty()) {

                        db.collection("pagosConjuntos").document(pagoConjunto.getId())
                                .collection("itemsPago").orderBy("nombre").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> itemsPagoSnapshot = queryDocumentSnapshots.getDocuments();

                                        for (DocumentSnapshot itemPago : itemsPagoSnapshot) {
                                            HashMap<UsuarioParaParcelable, Double> cantidadesConUsers = new HashMap<>();
                                            String id = itemPago.getId();
                                            String nombre = itemPago.getString("nombre");
                                            HashMap<String, Double> cantidadesConUsersReferences = (HashMap<String, Double>) itemPago.get("UsuariosConPagos");

                                            for (Map.Entry<String, Double> user : cantidadesConUsersReferences.entrySet()) {
                                                cantidadesConUsers.put(new UsuarioParaParcelable(user.getKey()), user.getValue());
                                            }


                                            itemsPago.add(new ItemPagoConjunto(id, nombre, cantidadesConUsers));
                                        }

                                        pagoConjunto.setItems(itemsPago);
                                        listaPagos = itemsPago;
                                        lpAdapter.changeAllList(itemsPago);

                                    }
                                });
                    }
                }
            });
        }
    }
}

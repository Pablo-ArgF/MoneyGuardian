package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;

import static androidx.core.util.ObjectsCompat.requireNonNull;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
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
import java.util.Objects;

public class ListaPagosFragment extends Fragment {

    public static final int GESTION_ACTIVITY = 2;
    private static final String LISTA_PAGOS = "ListaPagos";
    private static final String NAME_PAGO = "Nombre";
    private static final String IMAGEN = "Imagen";
    private static final String PAGO_CONJUNTO = "Pago Conjunto";
    RecyclerView listItemsPagosView;
    FloatingActionButton mainOpenButton;
    FloatingActionButton btnAddNewItem;
    FloatingActionButton fabDelete;
    FloatingActionButton fabEdit;
    SwipeRefreshLayout swipeRefreshLayout;
    private Uri imagen;
    private String namePago;
    private List<ItemPagoConjunto> listaPagos;
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
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshListaPagos);

        btnAddNewItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FormItemsListaPago.class);
            intent.putExtra("PAGO", pagoConjunto);
            startActivityForResult(intent, GESTION_ACTIVITY);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateFromDB();
            swipeRefreshLayout.setRefreshing(false);
        });

        fabDelete.setOnClickListener(v -> delete());


        new Animations(root).setOnClickAnimationAndVisibility(mainOpenButton, Arrays.asList(btnAddNewItem, fabDelete, fabEdit));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listItemsPagosView = view.findViewById(R.id.mainListPagosRecycler);
        listItemsPagosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext().getApplicationContext());
        listItemsPagosView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        listItemsPagosView.setLayoutManager(layoutManager);

        lpAdapter = new ItemListaAdapter(listaPagos, this::clickonItem);
        listItemsPagosView.setAdapter(lpAdapter);
    }

    private void clickonItem(ItemPagoConjunto itemPago) {

        ItemPagosFragment argumentoFragment = ItemPagosFragment.newInstance(itemPago, pagoConjunto);

        getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, argumentoFragment).addToBackStack(null).commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GESTION_ACTIVITY){
            assert data != null;
            lpAdapter.addItem((ItemPagoConjunto) requireNonNull(data.getExtras()).getParcelable("NEW_ITEM"));
        }

    }

    private void delete() {
        DocumentReference docReference = db.collection("pagosConjuntos").document(pagoConjunto.getId());

        docReference.delete().addOnCompleteListener(task -> {
            for (UsuarioParaParcelable p : pagoConjunto.getParticipantes()) {
                db.collection("users").document(p.getId()).update("pagosConjuntos", FieldValue.arrayRemove(docReference));
            }

            db.collection("users").document(pagoConjunto.getOwner()).update("pagosConjuntos", FieldValue.arrayRemove(docReference));
            getParentFragmentManager().popBackStack();
        });
    }

    private void updateFromDB() {

        List<ItemPagoConjunto> itemsPago = new ArrayList<>();

        db.collection("pagosConjuntos").document(pagoConjunto.getId()).collection("itemsPago").orderBy("nombre").get().addOnSuccessListener(queryDocumentSnapshots -> {
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

        });

    }
}

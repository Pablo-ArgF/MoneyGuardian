package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.moneyguardian.FormItemsListaPago;
import com.moneyguardian.FormularioPagoConjuntoActivity;
import com.moneyguardian.adapters.ItemListaAdapter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaPagosFragment extends Fragment {

    private static final String LISTA_PAGOS = "ListaPagos";
    private static final String NAME_PAGO = "Nombre";
    private static final String IMAGEN = "Imagen";
    private static final String PAGO_CONJUNTO = "Pago Conjunto";
    private String imagen;
    private String namePago;
    private List<ItemPagoConjunto> listaPagos;
    private Button btnAddNewItem;

    private PagoConjunto pagoConjunto;

    public static final int GESTION_ACTIVITY = 2;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    RecyclerView listItemsPagosView;
    private ItemListaAdapter lpAdapter;

    public static ListaPagosFragment newInstance(PagoConjunto param1) {
        ListaPagosFragment fragment = new ListaPagosFragment();
        Bundle args = new Bundle();
        args.putParcelable(PAGO_CONJUNTO, param1);
        args.putParcelableArrayList(LISTA_PAGOS, new ArrayList<>(param1.getItems()));
        args.putString(NAME_PAGO, param1.getNombre());
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
            imagen = getArguments().getString(IMAGEN);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO:Pillar los datos de la base de datos.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Mostramos el fragmento en el contenedor
        View root = inflater.inflate(R.layout.fragment_lista_pagos, container, false);
        TextView tvName = root.findViewById(R.id.namePagos);
        tvName.setText(namePago);
        ImageView ivImagen = root.findViewById(R.id.iconPago);
        btnAddNewItem = root.findViewById(R.id.btnNewItemPago);

        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormItemsListaPago.class);
                intent.putExtra("PAGO",pagoConjunto);
                startActivityForResult(intent, GESTION_ACTIVITY);
            }
        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listItemsPagosView = (RecyclerView) view.findViewById(R.id.mainListPagosRecycler);
        listItemsPagosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(view.getContext().getApplicationContext());
        listItemsPagosView.setLayoutManager(layoutManager);

        lpAdapter = new ItemListaAdapter(listaPagos,
                new ItemListaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ItemPagoConjunto itemPago) {
                        clickonItem(itemPago);
                    }
                });
        listItemsPagosView.setAdapter(lpAdapter);
        //cargarDatos();
    }

    private void clickonItem(ItemPagoConjunto itemPago) {

        ItemPagosFragment argumentoFragment = ItemPagosFragment.newInstance
                (itemPago.getNombre(), itemPago.getPagos());

        getParentFragmentManager().beginTransaction().
                replace(R.id.fragmentContainerMain, argumentoFragment).addToBackStack(null).commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        ItemPagoConjunto itemNuevo = data.getParcelableExtra("NEW_ITEM");

        if (requestCode == GESTION_ACTIVITY) {
            if(resultCode == RESULT_OK) {
                cargarDatos();

            }
        }
    }

    public void cargarDatos(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listaPagos = new ArrayList<>();

        db.collection("pagosConjuntos").
                document(pagoConjunto.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            List<Map<String, Map<String, Double>>> items = (List<Map<String, Map<String, Double>>>) document.getData().get("itemsPago");
                            if(items != null && items.size() != 0) {
                                for (int i = 0;i < items.size();i++) {
                                    for (Map.Entry<String, Map<String, Double>> item : items.get(i).entrySet()) {
                                        HashMap<UsuarioParaParcelable, Double> cantidadesConUsers = new HashMap<>();
                                        for (Map.Entry<String, Double> users : item.getValue().entrySet()) {
                                            cantidadesConUsers.put(new UsuarioParaParcelable(users.getKey()), Double.parseDouble(users.getValue().toString()));
                                        }

                                        listaPagos.add(new ItemPagoConjunto(item.getKey(), cantidadesConUsers));
                                    }
                                }
                            }

                            lpAdapter = new ItemListaAdapter(listaPagos,
                                    new ItemListaAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(ItemPagoConjunto itemPago) {
                                            clickonItem(itemPago);
                                        }
                                    });
                            listItemsPagosView.setAdapter(lpAdapter);
                        }else{
                            Log.i("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

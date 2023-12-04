package com.moneyguardian.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.adapters.ListaBalanceItemAdapter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.Animations;
import com.moneyguardian.util.UserChecks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemPagosFragment extends Fragment {

    private static final String NAME = "name";
    private static final String ITEM_PAGO = "itemPago";
    private static final String USERS_AND_PAYMENTS = "balance";
    private static final String PAGO = "pagoConjunto";
    private String nombreItem;
    private HashMap<UsuarioParaParcelable,Double> mapBalance = new HashMap<>();
    private ItemPagoConjunto itemPagoConjunto;
    private PagoConjunto pagoConjunto;
    private List<ItemPagoConjunto> listaItemsPagoConjunto;

    private TextView tvNombreItem;
    private RecyclerView rvBalance;
    private FloatingActionButton openButton;
    private FloatingActionButton editButton;
    private FloatingActionButton deleteButton;

    private FirebaseFirestore db;
    // Botones
    private Animations animations;

    public static ItemPagosFragment newInstance(ItemPagoConjunto itemPago, PagoConjunto pagoConjunto) {
        ItemPagosFragment fragment = new ItemPagosFragment();
        Bundle args = new Bundle();
        args.putString(NAME, itemPago.getNombre());
        args.putSerializable(USERS_AND_PAYMENTS, itemPago.getPagos());
        args.putParcelable(ITEM_PAGO, itemPago);
        args.putParcelable(PAGO,pagoConjunto);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombreItem = getArguments().getString(NAME);
            mapBalance = (HashMap<UsuarioParaParcelable, Double>) getArguments().get(USERS_AND_PAYMENTS);
            itemPagoConjunto = getArguments().getParcelable(ITEM_PAGO);
            pagoConjunto = getArguments().getParcelable(PAGO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_item_pagos, container, false);
        db = FirebaseFirestore.getInstance();
        tvNombreItem = root.findViewById(R.id.tituloItemPago);
        rvBalance = root.findViewById(R.id.recyclerListaItems);

        // Animaciones de botones
        animations = new Animations(root);

        //Botones
        openButton = root.findViewById(R.id.floatingActionButtonItemPago);
        editButton = root.findViewById(R.id.floatingActionButtonEditItemPago);
        deleteButton = root.findViewById(R.id.floatingActionButtonItemDelete);

        if(!new UserChecks().checkUser(pagoConjunto.getOwner())){
            openButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            openButton.setClickable(false);
            editButton.setClickable(false);
            deleteButton.setClickable(false);
        }else {

            animations.setOnClickAnimationAndVisibility(openButton, Arrays.asList(editButton, deleteButton));


            deleteButton.setOnClickListener(v ->
                    db.collection("pagosConjuntos").document(pagoConjunto.getId()).
                    collection("itemsPago").document(itemPagoConjunto.getId()).delete().
                    addOnSuccessListener(unused -> {
                        pagoConjunto.getItems().remove(itemPagoConjunto);
                        getParentFragmentManager().popBackStack();
                    }));
        }

        //we add the layout manager to the group list
        RecyclerView.LayoutManager groupLayoutManager = new LinearLayoutManager(container.getContext());
        rvBalance.setLayoutManager(groupLayoutManager);

        ListaBalanceItemAdapter adapter = new ListaBalanceItemAdapter(mapBalance);

        rvBalance.setAdapter(adapter);

        tvNombreItem.setText(this.nombreItem);

        return root;
    }

    private void editItemPago(){

    }

}


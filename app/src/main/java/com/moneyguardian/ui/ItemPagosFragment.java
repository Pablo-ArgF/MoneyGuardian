package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;
import static androidx.core.util.ObjectsCompat.requireNonNull;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.FormItemsListaPago;
import com.moneyguardian.adapters.ListaBalanceItemAdapter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.Animations;
import com.moneyguardian.util.UserChecks;

import java.util.Arrays;
import java.util.HashMap;

public class ItemPagosFragment extends Fragment {

    private static final String NAME = "name";
    private static final String ITEM_PAGO = "itemPago";
    private static final String USERS_AND_PAYMENTS = "balance";
    private static final String PAGO = "pagoConjunto";
    private static final int AVTIVITY_RETURN = 2;
    private String nombreItem;
    private HashMap<UsuarioParaParcelable,Double> mapBalance = new HashMap<>();
    private ItemPagoConjunto itemPagoConjunto;
    private PagoConjunto pagoConjunto;

    private TextView tvNombreItem;
    private RecyclerView rvBalance;
    private FloatingActionButton openButton;
    private FloatingActionButton editButton;
    private FloatingActionButton deleteButton;

    private FirebaseFirestore db;
    // Botones
    private Animations animations;
    private ListaBalanceItemAdapter adapter;

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
                        delteItem(root);
                    }));

            editButton.setOnClickListener(v -> {
                editItemPago();
            });
        }

        //we add the layout manager to the group list
        RecyclerView.LayoutManager groupLayoutManager = new LinearLayoutManager(container.getContext());
        rvBalance.setLayoutManager(groupLayoutManager);

        adapter = new ListaBalanceItemAdapter(mapBalance);

        rvBalance.setAdapter(adapter);

        tvNombreItem.setText(this.nombreItem);

        return root;
    }

    private void delteItem(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = builder.create().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_delete_question, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> {
            pagoConjunto.getItems().remove(itemPagoConjunto);
            getParentFragmentManager().popBackStack();
        });
        builder.setView(inflater.inflate(R.layout.dialog_delete_question, null)).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == AVTIVITY_RETURN){
            assert data != null;
            ItemPagoConjunto newItemPagoConjunto = data.getExtras().getParcelable("NEW_ITEM");
            pagoConjunto.upadteItem(newItemPagoConjunto);
            mapBalance = newItemPagoConjunto.getPagos();
            itemPagoConjunto = newItemPagoConjunto;

            adapter = new ListaBalanceItemAdapter(mapBalance);
            rvBalance.setAdapter(adapter);
        }
    }

    private void editItemPago(){
        Intent intent = new Intent(getActivity(), FormItemsListaPago.class);
        intent.putExtra("PAGO", pagoConjunto);
        intent.putExtra("ITEM", itemPagoConjunto);
        startActivityForResult(intent, AVTIVITY_RETURN);
        openButton.callOnClick();
    }

}


package com.moneyguardian.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moneyguardian.ListaBalanceItemAdapter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.modelo.UsuarioParaParcelable;

import java.util.HashMap;
import java.util.Map;

public class ItemPagosFragment extends Fragment {

    private static final String NAME = "name";
    private static final String USERS_AND_PAYMENTS = "balance";

    private String nombreItem;
    private HashMap<UsuarioParaParcelable,Integer> mapBalance = new HashMap<>();

    private TextView tvNombreItem;
    private RecyclerView rvBalance;

    public static ItemPagosFragment newInstance(String param1,
                                                HashMap<UsuarioParaParcelable,Integer> param3) {
        ItemPagosFragment fragment = new ItemPagosFragment();
        Bundle args = new Bundle();
        args.putString(NAME, param1);
        args.putSerializable(USERS_AND_PAYMENTS, param3);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombreItem = getArguments().getString(NAME);
            mapBalance = (HashMap<UsuarioParaParcelable, Integer>) getArguments().get(USERS_AND_PAYMENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_item_pagos, container, false);
        tvNombreItem = root.findViewById(R.id.tituloItemPago);
        rvBalance = root.findViewById(R.id.recyclerListaItems);

        //we add the layout manager to the group list
        RecyclerView.LayoutManager groupLayoutManager = new LinearLayoutManager(container.getContext());
        rvBalance.setLayoutManager(groupLayoutManager);

        ListaBalanceItemAdapter adapter = new ListaBalanceItemAdapter(mapBalance);

        rvBalance.setAdapter(adapter);

        tvNombreItem.setText(this.nombreItem);

        return root;
    }
}


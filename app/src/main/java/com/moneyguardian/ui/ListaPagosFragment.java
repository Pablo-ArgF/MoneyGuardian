package com.moneyguardian.ui;

import android.content.Intent;
import android.os.Bundle;
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

import com.moneyguardian.adapters.ItemListaAdapter;
import com.moneyguardian.FormItemsListaPago;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;

import java.util.ArrayList;
import java.util.List;

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

    RecyclerView listItemsPagosView;

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
                intent.putExtra("USERS_OF_PAYMENT",new ArrayList<>(pagoConjunto.getParticipantes()));
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

        ItemListaAdapter lpAdapter = new ItemListaAdapter(listaPagos,
                new ItemListaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ItemPagoConjunto itemPago) {
                        clickonItem(itemPago);
                    }
                });
        listItemsPagosView.setAdapter(lpAdapter);
    }

    private void clickonItem(ItemPagoConjunto itemPago) {

        ItemPagosFragment argumentoFragment = ItemPagosFragment.newInstance
                (itemPago.getNombre(), itemPago.getPagos());

        getParentFragmentManager().beginTransaction().
                replace(R.id.fragment_container_amigos_pagos, argumentoFragment).addToBackStack(null).commit();

    }
}

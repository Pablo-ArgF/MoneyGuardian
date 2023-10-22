package com.moneyguardian.ui;

import android.os.Bundle;
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

import com.moneyguardian.ItemListaAdapter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListaPagosFragment extends Fragment {

    private static final String LISTA_PAGOS = "ListaPagos";
    private static final String NAME_PAGO = "Nombre";
    private static final String IMAGEN = "Imagen";
    private String imagen;
    private String namePago;
    private List<ItemPagoConjunto> listaPagos;

    RecyclerView listItemsPagosView;

    public static ListaPagosFragment newInstance(String param1,String param2,String param3) {
        ListaPagosFragment fragment = new ListaPagosFragment();
        Bundle args = new Bundle();
        args.putString(LISTA_PAGOS, param1);
        args.putString(NAME_PAGO, param2);
        args.putString(IMAGEN, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listaPagos = getArguments().getParcelableArrayList(LISTA_PAGOS);
            namePago = getArguments().getString(NAME_PAGO);
            imagen = getArguments().getString(IMAGEN);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Mostramos el fragmento en el contenedor
        View root= inflater.inflate(R.layout.activity_main, container, false);
        TextView tvName = root.findViewById(R.id.namePago);
        tvName.setText(namePago);
        ImageView ivImagen = root.findViewById(R.id.iconPago);
        Picasso.get()
                .load(imagen).into(ivImagen);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listItemsPagosView = (RecyclerView)view.findViewById(R.id.mainListPagosRecycler);
        listItemsPagosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(view.getContext().getApplicationContext());
        listItemsPagosView.setLayoutManager(layoutManager);

       ItemListaAdapter lpAdapter= new ItemListaAdapter(listaPagos,
                new ItemListaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ItemPagoConjunto itemPago) {
                        clickonItem(itemPago);
                    }
                });
        listItemsPagosView.setAdapter(lpAdapter);
    }

    private void clickonItem(ItemPagoConjunto itemPago) {

        ItemPagosFragment argumentoFragment=ItemPagosFragment.newInstance
                (itemPago.getNombre(),itemPago.getUser(),itemPago.getPagos());

        getParentFragmentManager().beginTransaction().
                replace(R.id.fragmentListaPagos, argumentoFragment).commit();

    }
}

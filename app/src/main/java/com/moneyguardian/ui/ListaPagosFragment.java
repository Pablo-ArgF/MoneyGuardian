package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;
import static androidx.core.util.ObjectsCompat.requireNonNull;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.FormItemsListaPago;
import com.moneyguardian.FormularioPagoConjuntoActivity;
import com.moneyguardian.MainActivity;
import com.moneyguardian.R;
import com.moneyguardian.adapters.ItemListaAdapter;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.Animations;
import com.moneyguardian.util.UserChecks;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListaPagosFragment extends Fragment {

    public static final int GESTION_ACTIVITY = 2;
    private static final String LISTA_PAGOS = "ListaPagos";
    private static final String NAME_PAGO = "Nombre";
    private static final String IMAGEN = "Imagen";
    private static final String PAGO_CONJUNTO = "Pago Conjunto";
    private static final int AVTIVITY_RETURN = 3;
    RecyclerView listItemsPagosView;
    FloatingActionButton mainOpenButton;
    FloatingActionButton btnAddNewItem;
    FloatingActionButton fabDelete;
    FloatingActionButton fabEdit;
    SwipeRefreshLayout swipeRefreshLayout;

    SearchView serachBar;
    LinearLayout noItemsPago;

    View divider;
    private MainActivity mainActivity;
    private Uri imagen;
    private String namePago;
    private List<ItemPagoConjunto> listaPagos;
    private PagoConjunto pagoConjunto;
    private ItemListaAdapter lpAdapter;

    private FirebaseFirestore db;
    private TextView tvName;
    private ImageView ivImagen;
    private TextView itemsSeleccionadosTV;
    private Animations animations;

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
        mainActivity = (MainActivity) getActivity();
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

        tvName = root.findViewById(R.id.namePagos);
        tvName.setText(pagoConjunto.getNombre());
        ivImagen = root.findViewById(R.id.iconPago);
        divider = root.findViewById(R.id.dividerItemPagos);
        if (imagen != null) {
            Picasso.get().load(pagoConjunto.getImagen()).into(ivImagen);
            ivImagen.setVisibility(View.VISIBLE);
        } else {
            ivImagen.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, fromDPtoPX(root, 2));
            params.addRule(RelativeLayout.BELOW, R.id.namePagos);
            params.setMargins(fromDPtoPX(root, 10), fromDPtoPX(root, 5), fromDPtoPX(root, 10), 0);
            divider.setLayoutParams(params);
        }


        mainOpenButton = root.findViewById(R.id.floatingActionButtonMainPagoConjunto);
        btnAddNewItem = root.findViewById(R.id.btnNewItemPago);
        fabDelete = root.findViewById(R.id.floatingActionButtonDeletePagoConjunto);
        fabEdit = root.findViewById(R.id.floatingActionButtonEditPagoConjunto);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshListaPagos);
        serachBar = root.findViewById(R.id.searchItemPago);
        noItemsPago = root.findViewById(R.id.noItemsPago);
        itemsSeleccionadosTV = root.findViewById(R.id.itemsSeleccionados);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateFromDB();
            swipeRefreshLayout.setRefreshing(false);
            itemsSeleccionadosTV.setVisibility(View.GONE);
            ItemListaAdapter.changeSelectMode(false);
        });

        if (!new UserChecks().checkUser(pagoConjunto.getOwner())) {
            mainOpenButton.setVisibility(View.GONE);
            btnAddNewItem.setVisibility(View.GONE);
            fabEdit.setVisibility(View.GONE);
            fabDelete.setVisibility(View.GONE);
            mainOpenButton.setClickable(false);
            btnAddNewItem.setClickable(false);
            fabEdit.setClickable(false);
            fabDelete.setClickable(false);
        } else {
            btnAddNewItem.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), FormItemsListaPago.class);
                intent.putExtra("PAGO", pagoConjunto);
                startActivityForResult(intent, GESTION_ACTIVITY);
                mainOpenButton.callOnClick();
            });
            fabDelete.setOnClickListener(this::delete);
            fabEdit.setOnClickListener(v -> {
                editPagoConjunto();
            });


            animations = new Animations(root);
            animations.setOnClickAnimationAndVisibility(mainOpenButton);
            animations.setOtherButtons(Arrays.asList(btnAddNewItem, fabDelete, fabEdit));
        }

        return root;
    }

    private int fromDPtoPX(View root, int dps) {
        Resources r = root.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics());

        return px;
    }

    private void editPagoConjunto() {
        Intent intent = new Intent(getActivity(), FormularioPagoConjuntoActivity.class);
        intent.putExtra("OLD_PAGO", pagoConjunto);
        startActivityForResult(intent, AVTIVITY_RETURN);
        mainOpenButton.callOnClick();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listItemsPagosView = view.findViewById(R.id.mainListPagosRecycler);
        listItemsPagosView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext().getApplicationContext());
        listItemsPagosView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        listItemsPagosView.setLayoutManager(layoutManager);


        lpAdapter = new ItemListaAdapter(pagoConjunto.getItems(), this::clickonItem, v -> {
            longPress();
            return true;
        });

        lpAdapter.setHasStableIds(true);
        listItemsPagosView.setAdapter(lpAdapter);

        serachBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                lpAdapter.changeAllList((pagoConjunto.getItems().stream().filter(itemPagoConjunto -> itemPagoConjunto.getNombre().toLowerCase().contains(newText.toLowerCase())).collect(Collectors.toList())));
                ItemListaAdapter.setItemsPagoSeleccionados(new ArrayList<>());
                ItemListaAdapter.changeSelectMode(false);
                itemsSeleccionadosTV.setVisibility(View.GONE);
                return true;
            }
        });

        if (pagoConjunto.getItems().isEmpty()) {
            noItemsPago.setVisibility(View.VISIBLE);
            listItemsPagosView.setVisibility(View.GONE);
        } else {
            noItemsPago.setVisibility(View.GONE);
            listItemsPagosView.setVisibility(View.VISIBLE);
        }
    }

    private void longPress() {
        ItemListaAdapter.changeSelectMode(true);
        itemsSeleccionadosTV.setVisibility(View.VISIBLE);
        itemsSeleccionadosTV.setText(lpAdapter.getItemsPagoSeleccionados().size() + " " + getString(R.string.items_seleccionados_to_delete));
        if (btnAddNewItem.getVisibility() == View.VISIBLE) {
            mainOpenButton.performClick();
        }
        mainOpenButton.clearAnimation();
        mainOpenButton.setOnClickListener(this::deleteItemsSlected);
        mainOpenButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red, getContext().getTheme())));
        mainOpenButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_delete, getContext().getTheme()));
    }

    private void deleteItemsSlected(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = builder.create().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_delete_question, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> {
            for(ItemPagoConjunto i : lpAdapter.getItemsPagoSeleccionados()){
                db.collection("pagosConjuntos").document(pagoConjunto.getId()).collection("itemsPago").document(i.getId()).delete().addOnSuccessListener(unused -> {
                    pagoConjunto.getItems().remove(i);
                    lpAdapter.notifyDataSetChanged();
                    notSelectedItems();
                });
            }
            ItemListaAdapter.setItemsPagoSeleccionados(new ArrayList<>());
        });
        builder.setView(inflater.inflate(R.layout.dialog_delete_question, null)).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        dialog.show();

        TextView question = dialog.findViewById(R.id.tvDeleteQuestionDialog);

        if(lpAdapter.getItemsPagoSeleccionados().size() == 1){
            question.setText(R.string.delete_selected_item);
        }else{
            question.setText(R.string.delete_selected_items_plural);
        }
    }


    private void clickonItem(ItemPagoConjunto itemPago) {
        if (ItemListaAdapter.isSelectedModeOn()) {
            itemsSeleccionadosTV.setText(lpAdapter.getItemsPagoSeleccionados().size() + " " + getString(R.string.items_seleccionados_to_delete));
            if (lpAdapter.getItemsPagoSeleccionados().isEmpty()) {
                notSelectedItems();
            }
        } else {

            if (itemPago.isPagado()) {

            } else {
                ItemPagosFragment argumentoFragment = ItemPagosFragment.newInstance(itemPago, pagoConjunto);

                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, argumentoFragment).addToBackStack(null).commit();
            }
        }
    }

    private void notSelectedItems() {
        itemsSeleccionadosTV.setVisibility(View.GONE);
        mainOpenButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_input_add, getContext().getTheme()));
        mainOpenButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_blue_dark, getContext().getTheme())));
        animations.setOnClickAnimationAndVisibility(mainOpenButton);
        animations.setOtherButtons(Arrays.asList(btnAddNewItem, fabDelete, fabEdit));
        if (lpAdapter.getItemCount() == 0) {
            noItemsPago.setVisibility(View.VISIBLE);
            listItemsPagosView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GESTION_ACTIVITY) {
            assert data != null;
            ItemPagoConjunto newItem = data.getExtras().getParcelable("NEW_ITEM");
            lpAdapter.addItem(requireNonNull(newItem));
        }

        if (resultCode == RESULT_OK && requestCode == AVTIVITY_RETURN) {
            mainActivity.getPagosConjuntos().remove(pagoConjunto);
            pagoConjunto = data.getExtras().getParcelable("PAGO");
            mainActivity.addPagoCOnjunto(pagoConjunto);
/*            lpAdapter = new ItemListaAdapter(pagoConjunto.getItems(), this::clickonItem, );
            listItemsPagosView.setAdapter(lpAdapter);*/

            if (pagoConjunto.getImagen() != null) {
                Picasso.get().load(pagoConjunto.getImagen()).into(ivImagen);
                ivImagen.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, fromDPtoPX(getView(), 2));
                params.addRule(RelativeLayout.BELOW, R.id.iconPago);
                params.setMargins(fromDPtoPX(getView(), 10), fromDPtoPX(getView(), 5), fromDPtoPX(getView(), 10), 0);
                divider.setLayoutParams(params);
            }

            tvName.setText(pagoConjunto.getNombre());
        }

        if (lpAdapter.getItemCount() == 0) {
            noItemsPago.setVisibility(View.VISIBLE);
            listItemsPagosView.setVisibility(View.GONE);
        } else {
            noItemsPago.setVisibility(View.GONE);
            listItemsPagosView.setVisibility(View.VISIBLE);
        }

    }

    private void delete(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = builder.create().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_delete_question, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> {
            DocumentReference docReference = db.collection("pagosConjuntos").document(pagoConjunto.getId());

            docReference.delete().addOnCompleteListener(task -> {
                for (UsuarioParaParcelable p : pagoConjunto.getParticipantes()) {
                    db.collection("users").document(p.getId()).update("pagosConjuntos", FieldValue.arrayRemove(docReference));
                }

                db.collection("users").document(pagoConjunto.getOwner()).update("pagosConjuntos", FieldValue.arrayRemove(docReference));
                mainActivity.getPagosConjuntos().remove(pagoConjunto);
                getParentFragmentManager().popBackStack();
            });
        });
        builder.setView(inflater.inflate(R.layout.dialog_delete_question, null)).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateFromDB() {

        List<ItemPagoConjunto> itemsPago = new ArrayList<>();

        db.collection("pagosConjuntos").document(pagoConjunto.getId()).collection("itemsPago").orderBy("nombre").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> itemsPagoSnapshot = queryDocumentSnapshots.getDocuments();

            for (DocumentSnapshot itemPago : itemsPagoSnapshot) {
                HashMap<UsuarioParaParcelable, Double> cantidadesConUsers = new HashMap<>();
                String id = itemPago.getId();
                String nombre = itemPago.getString("nombre");
                Double cantidadTotal = itemPago.getDouble("totalDinero");
                HashMap<String, Double> cantidadesConUsersReferences = (HashMap<String, Double>) itemPago.get("UsuariosConPagos");

                for (Map.Entry<String, Double> user : cantidadesConUsersReferences.entrySet()) {
                    cantidadesConUsers.put(new UsuarioParaParcelable(user.getKey()), user.getValue());
                }

                UsuarioParaParcelable userThatPays = new UsuarioParaParcelable(itemPago.getString("usuarioPago"));

                itemsPago.add(new ItemPagoConjunto(id, nombre, cantidadesConUsers, userThatPays, cantidadTotal));
            }

            pagoConjunto.setItems(itemsPago);
            listaPagos = itemsPago;
            lpAdapter.changeAllList(itemsPago);
            ItemListaAdapter.setItemsPagoSeleccionados(new ArrayList<>());
            if (pagoConjunto.getItems().isEmpty()) {
                noItemsPago.setVisibility(View.VISIBLE);
                listItemsPagosView.setVisibility(View.GONE);
            } else {
                noItemsPago.setVisibility(View.GONE);
                listItemsPagosView.setVisibility(View.VISIBLE);
            }

        });

    }
}

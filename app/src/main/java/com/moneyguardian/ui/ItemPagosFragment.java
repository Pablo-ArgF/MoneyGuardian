package com.moneyguardian.ui;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.FormItemsListaPago;
import com.moneyguardian.R;
import com.moneyguardian.adapters.ListaBalanceItemAdapter;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;
import com.moneyguardian.util.Animations;
import com.moneyguardian.util.UserChecks;
import com.moneyguardian.util.UsuarioMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPagosFragment extends Fragment {

    private static final String NAME = "name";
    private static final String ITEM_PAGO = "itemPago";
    private static final String USERS_AND_PAYMENTS = "balance";
    private static final String PAGO = "pagoConjunto";
    private static final int AVTIVITY_RETURN = 2;
    private String nombreItem;
    private HashMap<UsuarioParaParcelable, Double> mapBalance = new HashMap<>();
    private ItemPagoConjunto itemPagoConjunto;
    private PagoConjunto pagoConjunto;

    private TextView tvNombreItem;
    private RecyclerView rvBalance;
    private FloatingActionButton openButton;
    private FloatingActionButton editButton;
    private FloatingActionButton deleteButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseFirestore db;
    // Botones
    private Animations animations;
    private ListaBalanceItemAdapter adapter;
    private FirebaseAuth auth;
    private FloatingActionButton payButton;

    public static ItemPagosFragment newInstance(ItemPagoConjunto itemPago, PagoConjunto pagoConjunto) {
        ItemPagosFragment fragment = new ItemPagosFragment();
        Bundle args = new Bundle();
        args.putString(NAME, itemPago.getNombre());
        args.putSerializable(USERS_AND_PAYMENTS, itemPago.getPagos());
        args.putParcelable(ITEM_PAGO, itemPago);
        args.putParcelable(PAGO, pagoConjunto);
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
    public void onResume() {
        super.onResume();
        if (itemPagoConjunto.getPagos().containsKey(new UsuarioParaParcelable(auth.getCurrentUser().getUid())) && itemPagoConjunto.getPagos().get(new UsuarioParaParcelable(auth.getCurrentUser().getUid())) != itemPagoConjunto.getMoney() && itemPagoConjunto.getPagos().get(new UsuarioParaParcelable(auth.getCurrentUser().getUid())) != 0.0 && !itemPagoConjunto.getUserThatPays().getId().equals(auth.getUid())) {
            animations.setOtherButtons(Arrays.asList(editButton, deleteButton, payButton));
        } else {
            animations.setOtherButtons(Arrays.asList(editButton, deleteButton));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_item_pagos, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        tvNombreItem = root.findViewById(R.id.tituloItemPago);
        rvBalance = root.findViewById(R.id.recyclerListaItems);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshItemsPagosConjunto);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            upadteFromDB();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Animaciones de botones
        animations = new Animations(root);

        //Botones
        openButton = root.findViewById(R.id.floatingActionButtonItemPago);
        editButton = root.findViewById(R.id.floatingActionButtonEditItemPago);
        deleteButton = root.findViewById(R.id.floatingActionButtonItemDelete);
        payButton = root.findViewById(R.id.floatingActionButtonPayItemPago);


        if (!new UserChecks().checkUser(pagoConjunto.getOwner())) {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            editButton.setClickable(false);
            deleteButton.setClickable(false);
            if (itemPagoConjunto.getPagos().containsKey(new UsuarioParaParcelable(auth.getCurrentUser().getUid())) && (itemPagoConjunto.getPagos().get(new UsuarioParaParcelable(auth.getCurrentUser().getUid())) != itemPagoConjunto.getMoney()) && (itemPagoConjunto.getPagos().get(new UsuarioParaParcelable(auth.getCurrentUser().getUid())) != 0.0) && !itemPagoConjunto.getUserThatPays().getId().equals(auth.getUid())) {
                openButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_done_24, getContext().getTheme()));
                openButton.setOnClickListener(this::showDialogMain);
            } else {
                openButton.setVisibility(View.GONE);
                openButton.setClickable(false);
            }

        } else {
            animations.setOnClickAnimationAndVisibility(openButton);

            payButton.setOnClickListener(this::showDialogPay);

            deleteButton.setOnClickListener(v -> db.collection("pagosConjuntos").document(pagoConjunto.getId()).collection("itemsPago").document(itemPagoConjunto.getId()).delete().addOnSuccessListener(unused -> {
                delteItem(root);
            }));

            editButton.setOnClickListener(v -> {
                editItemPago();
            });
        }

        //we add the layout manager to the group list
        RecyclerView.LayoutManager groupLayoutManager = new LinearLayoutManager(container.getContext());
        rvBalance.setLayoutManager(groupLayoutManager);

        getUserParcelables();

        tvNombreItem.setText(itemPagoConjunto.getNombre());

        return root;
    }

    private void showDialogPay(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = builder.create().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_pay_question, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> {
            marcarComoPagado();
            openButton.performClick();
            payButton.clearAnimation();
            animations.setOtherButtons(Arrays.asList(editButton, deleteButton));
        });
        builder.setView(inflater.inflate(R.layout.dialog_pay_question, null)).setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogMain(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = builder.create().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_pay_question, null)).setPositiveButton(R.string.acceptBtn, (dialog, which) -> {
            marcarComoPagado();
            openButton.setVisibility(View.GONE);
            openButton.setClickable(false);
        });
        builder.setView(inflater.inflate(R.layout.dialog_pay_question, null)).setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.cancel();

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void marcarComoPagado() {
        UsuarioParaParcelable completeUser = null;
        HashMap<UsuarioParaParcelable, Double> pagos = itemPagoConjunto.getPagos();

        for (UsuarioParaParcelable u : pagos.keySet()) {
            if (u.getId().equals(auth.getCurrentUser().getUid())) {
                completeUser = u;
                break;
            }
        }

        if (!completeUser.getId().equals(itemPagoConjunto.getUserThatPays().getId())) {
            itemPagoConjunto.getPagos().put(itemPagoConjunto.getUserThatPays(),
                    Math.round((pagos.get(itemPagoConjunto.getUserThatPays()) + pagos.get(completeUser)) * 100.0) / 100.0);
            itemPagoConjunto.getPagos().put(completeUser, 0.0);
        }

        Map<String, Double> usersWithMoney = new HashMap<>();
        for (Map.Entry<UsuarioParaParcelable, Double> u : pagos.entrySet()) {
            usersWithMoney.put(u.getKey().getId(), u.getValue());
        }

        db.collection("pagosConjuntos").document(pagoConjunto.getId()).collection("itemsPago").document(itemPagoConjunto.getId()).update("UsuariosConPagos", usersWithMoney);

        adapter.upadteList(itemPagoConjunto.getPagos());

    }

    private void upadteFromDB() {
        db.collection("pagosConjuntos").document(pagoConjunto.getId()).collection("itemsPago").document(itemPagoConjunto.getId()).get().addOnCompleteListener(task -> {
            Map<String, Object> result = task.getResult().getData();
            HashMap<UsuarioParaParcelable, Double> cantidadesConUsers = new HashMap<>();

            String id = task.getResult().getId();
            String nombre = (String) result.get("nombre");
            Double cantidadTotal = (Double) result.get("totalDinero");
            HashMap<String, Double> cantidadesConUsersReferences = (HashMap<String, Double>) result.get("UsuariosConPagos");

            /*for (Map.Entry<String, Double> user : cantidadesConUsersReferences.entrySet()) {
                cantidadesConUsers.put(new UsuarioParaParcelable(user.getKey()), user.getValue());
            }
*/
            UsuarioParaParcelable userThatPays = new UsuarioParaParcelable((String) result.get("usuarioPago"));

            List<Task<DocumentSnapshot>> taskList = new ArrayList<>();

            cantidadesConUsersReferences.forEach((user,value) -> {
                taskList.add(db.collection("users").document(user).get());
            });

            Tasks.whenAllSuccess(taskList).addOnSuccessListener(objects -> {

                for (Object d : objects) {
                    UsuarioParaParcelable user = (UsuarioMapper.mapBasicsParcelable((DocumentSnapshot) d));
                    cantidadesConUsers.put(user,cantidadesConUsersReferences.get(user.getId()));
                }


                itemPagoConjunto = new ItemPagoConjunto(id, nombre, cantidadesConUsers, userThatPays, cantidadTotal);
                tvNombreItem.setText(itemPagoConjunto.getNombre());
                adapter.upadteList(itemPagoConjunto.getPagos());
            });
        });

    }

    private void getUserParcelables() {

        List<Task> taskList = new ArrayList<>();
        HashMap<UsuarioParaParcelable, Double> newMap = new HashMap<>();
        List<UsuarioParaParcelable> allUsersInfo = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Double u : mapBalance.values()) {
            values.add(u);
        }

        for (UsuarioParaParcelable u : mapBalance.keySet()) {
            taskList.add(db.collection("users").document(u.getId()).get());
        }

        Tasks.whenAllSuccess(taskList).addOnSuccessListener(objects -> {
            for (Object d : objects) {
                allUsersInfo.add((UsuarioMapper.mapBasicsParcelable((DocumentSnapshot) d)));
            }

            for (int i = 0; i < allUsersInfo.size(); i++) {
                newMap.put(allUsersInfo.get(i), values.get(i));
            }

            itemPagoConjunto.setPagos(newMap);
            adapter = new ListaBalanceItemAdapter(newMap);
            rvBalance.setAdapter(adapter);

        });
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

        if (resultCode == RESULT_OK && requestCode == AVTIVITY_RETURN) {
            assert data != null;
            ItemPagoConjunto newItemPagoConjunto = data.getExtras().getParcelable("NEW_ITEM");
            pagoConjunto.upadteItem(newItemPagoConjunto);
            mapBalance = newItemPagoConjunto.getPagos();
            itemPagoConjunto = newItemPagoConjunto;
            tvNombreItem.setText(itemPagoConjunto.getNombre());


            getUserParcelables();
        }
    }

    private void editItemPago() {
        Intent intent = new Intent(getActivity(), FormItemsListaPago.class);
        intent.putExtra("PAGO", pagoConjunto);
        intent.putExtra("ITEM", itemPagoConjunto);
        startActivityForResult(intent, AVTIVITY_RETURN);
        openButton.callOnClick();
    }

}


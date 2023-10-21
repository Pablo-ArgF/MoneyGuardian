package com.moneyguardian.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moneyguardian.R;
import com.moneyguardian.model.Usuario;

import java.util.HashMap;
import java.util.Map;

public class ItemPagosFragment extends Fragment {

    private static final String NAME = "name";
    private static final String USER_PAID = "userPaid";
    private static final String USERS_AND_PAYMENTS = "usersAndPayments";

    private String mParam1;
    private String mParam2;
    private HashMap<Usuario,Integer> mParam3;

    public static ItemPagosFragment newInstance(String param1, String param2,
                                                HashMap<Usuario,Integer> param3) {
        ItemPagosFragment fragment = new ItemPagosFragment();
        Bundle args = new Bundle();
        args.putString(NAME, param1);
        args.putString(USER_PAID, param2);
        args.putSerializable(USERS_AND_PAYMENTS, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(NAME);
            mParam2 = getArguments().getString(USER_PAID);
            mParam3 = getArguments().getParcelable(USERS_AND_PAYMENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_pagos, container, false);
    }
}
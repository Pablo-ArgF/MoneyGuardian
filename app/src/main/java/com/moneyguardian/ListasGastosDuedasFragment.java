package com.moneyguardian;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moneyguardian.ui.DeudasListFragment;
import com.moneyguardian.ui.ListaGastosFragment;

public class ListasGastosDuedasFragment extends Fragment {
    private int currentFragment;
    private DeudasListFragment deudasListFragmentragment;
    private ListaGastosFragment listaGastosFragment;

    public ListasGastosDuedasFragment() {
        // Required empty public constructor
        listaGastosFragment = new ListaGastosFragment();
        deudasListFragmentragment = new DeudasListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listas_gastos_duedas, container, false);

        getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).commit();
        currentFragment = 0;
        BottomNavigationView bottomNavigationView = root.findViewById(R.id.listasNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_deudas && R.id.menu_deudas != currentFragment) {
                    currentFragment = R.id.menu_deudas;
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, deudasListFragmentragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.menu_gastos && R.id.menu_gastos != currentFragment) {
                    ((MainActivity) getActivity()).setLoading(false);
                    currentFragment = R.id.menu_gastos;
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).commit();
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_listas_gastos_deudas, menu);
    }

}
package com.moneyguardian;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private BottomNavigationView bottomNavigationView;

    public ListasGastosDuedasFragment() {
        // Required empty public constructor
        listaGastosFragment = new ListaGastosFragment();
        deudasListFragmentragment = new DeudasListFragment();
        currentFragment = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listas_gastos_duedas, container, false);

        // TODO error, al volver al fragment no carga ninguno de los otros dos fragments
        if (currentFragment == 0) {
            getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).commit();
        }

        bottomNavigationView = root.findViewById(R.id.listasNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_deudas && R.id.menu_deudas != currentFragment) {
                    currentFragment = R.id.menu_deudas;
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, deudasListFragmentragment).addToBackStack(null).commit();
                    return true;
                } else if (item.getItemId() == R.id.menu_gastos && R.id.menu_gastos != currentFragment) {
                    ((MainActivity) getActivity()).setLoading(false);
                    currentFragment = R.id.menu_gastos;
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).addToBackStack(null).commit();
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

    @Override
    public void onResume() {
        super.onResume();
        if (currentFragment == R.layout.fragment_deudas_list) {
            deudasListFragmentragment = new DeudasListFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, deudasListFragmentragment).commit();
        } else {
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.menu_gastos);
            }
            listaGastosFragment = new ListaGastosFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).commit();
        }
    }
}
package com.moneyguardian.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.moneyguardian.MainActivity;
import com.moneyguardian.R;

public class ListasGastosDuedasFragment extends Fragment {
    private int currentFragment;
    private DeudasListFragment deudasListFragmentragment;
    private ListaGastosFragment listaGastosFragment;
    // UI
    private ColorStateList nonSelectedColor;

    public ListasGastosDuedasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        listaGastosFragment = new ListaGastosFragment();
        deudasListFragmentragment = new DeudasListFragment();
        currentFragment = 0;

        View root = inflater.inflate(R.layout.fragment_listas_gastos_duedas, container, false);


        // Manejo de los cambios de fragmento

        TextView btnListaGastos = root.findViewById(R.id.btnListaGastos);
        TextView btnListaDeudas = root.findViewById(R.id.btnListaDeudas);
        btnListaGastos.setTextColor(getResources().getColor(R.color.blue, null));
        this.nonSelectedColor = btnListaDeudas.getTextColors();

        if (currentFragment == 0) {
            getChildFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).addToBackStack(null).commit();
        }


        btnListaGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnListaGastos.setTextColor(getResources().getColor(R.color.blue, null));
                btnListaDeudas.setTextColor(nonSelectedColor);
                ((MainActivity) getActivity()).setLoading(false);
                currentFragment = 1;
                getChildFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).addToBackStack(null).commit();
            }
        });

        btnListaDeudas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnListaDeudas.setTextColor(getResources().getColor(R.color.blue, null));
                btnListaGastos.setTextColor(nonSelectedColor);
                currentFragment = 2;
                getChildFragmentManager().beginTransaction().replace(R.id.fragmentListas, deudasListFragmentragment).addToBackStack(null).commit();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentFragment == R.layout.fragment_list_deudas) {
            deudasListFragmentragment = new DeudasListFragment();
            getChildFragmentManager().beginTransaction().replace(R.id.fragmentListas, deudasListFragmentragment).commit();
        } else {
            listaGastosFragment = new ListaGastosFragment();
            getChildFragmentManager().beginTransaction().replace(R.id.fragmentListas, listaGastosFragment).commit();
        }
    }
}
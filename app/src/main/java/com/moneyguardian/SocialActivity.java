package com.moneyguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moneyguardian.ui.ListaAmigosFragment;
import com.moneyguardian.ui.ListaPagosFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;

public class SocialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ListaAmigosFragment listaAmigosFragment = new ListaAmigosFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_amigos_pagos, listaAmigosFragment).commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /* Cuando se selecciona uno de los botones / ítems*/
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();

            /* Según el caso, crearemos un Fragmento u otro */
            if (itemId == R.id.navigation_pagos_conjuntos) {
                /* Haciendo uso del FactoryMethod pasándole todos los parámetros necesarios */

                PagosConjuntosFragment pagosConjuntosFragmentsFragment = new PagosConjuntosFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_amigos_pagos, pagosConjuntosFragmentsFragment).commit();
                return true;
            }

            if (itemId == R.id.navigation_amigos) {
                ListaAmigosFragment listaAmigosFragment = new ListaAmigosFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_amigos_pagos, listaAmigosFragment).commit();
                return true;
            }


            //Si no es nula y no entra... Algo falla.
            throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        ;
    };
}
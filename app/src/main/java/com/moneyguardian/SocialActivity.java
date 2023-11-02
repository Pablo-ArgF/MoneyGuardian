package com.moneyguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moneyguardian.ui.ListaAmigosFragment;
import com.moneyguardian.ui.ListaPagosFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;
import com.moneyguardian.userAuth.LoginActivity;

public class SocialActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO move this to main activity when changed
        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        if(user == null){
            //if no user -> send to login page
            Intent intent = new Intent(SocialActivity.this, LoginActivity.class);
            startActivity(intent);

        }

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
package com.moneyguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moneyguardian.ui.ListaAmigosFragment;
import com.moneyguardian.ui.ListaPagosFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        ListaAmigosFragment amigosFragment = ListaAmigosFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                amigosFragment).commit();*/
        ListaPagosFragment listaPagosFragment = ListaPagosFragment.newInstance(null,"");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                listaPagosFragment).commit();
    }
}
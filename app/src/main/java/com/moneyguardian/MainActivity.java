package com.moneyguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.ui.ListaAmigosFragment;
import com.moneyguardian.ui.MainFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;
import com.moneyguardian.userAuth.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private MainFragment fragmentMain;
    private PagosConjuntosFragment fragmentPagosConjuntos;
    private ListaAmigosFragment amigosFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentMain = new MainFragment();
        fragmentPagosConjuntos = new PagosConjuntosFragment();
        amigosFragment = new ListaAmigosFragment();

        //we load main fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
               fragmentMain).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    int id = item.getItemId();
                    if(id == R.id.home_menu_btn) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                                fragmentMain).commit();
                        return true;
                    }
                    if(id == R.id.lista_menu_btn){
                        //TODO implement lista view
                        return false;
                    }
                    if(id == R.id.amigos_menu_btn){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                                amigosFragment).commit();
                        return true;
                    }
                    if(id == R.id.pagos_conjuntos_menu_btn){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                                fragmentPagosConjuntos).commit();
                        return true;
                    }
                    return false;
                }
        );
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.home_menu_btn) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        if(id == R.id.lista_menu_btn){
            //TODO implement lista view
            return false;
        }
        if(id == R.id.amigos_menu_btn){
            startActivity(new Intent(this, SocialActivity.class));
            return true;
        }
        if(id == R.id.pagos_conjuntos_menu_btn){
            startActivity(new Intent(this,SocialActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
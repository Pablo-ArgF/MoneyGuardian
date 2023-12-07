package com.moneyguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.ui.DeudasListFragment;
import com.moneyguardian.ui.ListaAmigosFragment;
import com.moneyguardian.ui.ListaGastosFragment;
import com.moneyguardian.ui.MainFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;
import com.moneyguardian.userAuth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private MainFragment fragmentMain;
    private PagosConjuntosFragment fragmentPagosConjuntos;
    private ListaAmigosFragment amigosFragment;
    private ListasGastosDuedasFragment listDeudasGastosFragment;

    //for the loading
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private FrameLayout progressBarHolder;
    private boolean loading;

    //-----------
    //atributes common to more than one view, we avoid reloading
    private Usuario user;
    private List<Gasto> gastos = new ArrayList<>();
    private List<PagoConjunto> pagosConjuntos = new ArrayList<>();
    private List<Usuario> amigos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarHolder = findViewById(R.id.progressBarHolder);

        fragmentMain = new MainFragment();
        fragmentPagosConjuntos = new PagosConjuntosFragment();
        amigosFragment = new ListaAmigosFragment();
        listDeudasGastosFragment = new ListasGastosDuedasFragment();

        //we load main fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                fragmentMain).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    int id = item.getItemId();
                    if (id == R.id.home_menu_btn) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                                fragmentMain).commit();
                        return true;
                    }
                    if (id == R.id.lista_menu_btn) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                                listDeudasGastosFragment).commit();
                        return true;
                    }
                    if (id == R.id.amigos_menu_btn) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                                amigosFragment).commit();
                        return true;
                    }
                    if (id == R.id.pagos_conjuntos_menu_btn) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain,
                                fragmentPagosConjuntos).commit();
                        return true;
                    }
                    return false;
                }
        );
    }

    public void setLoading(boolean activate) {
        if (loading == activate)
            return;
        this.loading = activate;
        if (activate) {
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        } else {
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
        }
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public List<Gasto> getGastos() {
        return gastos;
    }

    public void setGastos(List<Gasto> gastos) {
        this.gastos = gastos;
    }

    public List<PagoConjunto> getPagosConjuntos() {
        return pagosConjuntos;
    }

    public void setPagosConjuntos(List<PagoConjunto> pagosConjuntos) {
        this.pagosConjuntos = pagosConjuntos;
    }

    public List<Usuario> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }
}
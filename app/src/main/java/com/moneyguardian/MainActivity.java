package com.moneyguardian;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.ui.ListaAmigosFragment;
import com.moneyguardian.ui.ListasGastosDuedasFragment;
import com.moneyguardian.ui.MainFragment;
import com.moneyguardian.ui.PagosConjuntosFragment;

import java.util.ArrayList;
import java.util.List;

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

    public void addPagoCOnjunto(PagoConjunto pg){
        this.pagosConjuntos.add(pg);
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
package com.moneyguardian.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Conexion {

    private Context context;

    public Conexion(Context context){
        this.context = context;
    }

    public boolean compruebaConexion() {
        boolean conectado = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        conectado = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return conectado;
    }

}

package com.moneyguardian.util;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.moneyguardian.modelo.Gasto;

import java.util.Date;

public class GastoMapper {

    public static Gasto map(DocumentSnapshot document){
        float balance = document.get("balance",Float.class);
        Date fecha = ((Timestamp) document.getData().get("fechaCreacion")).toDate();
        String nombre = document.getString("nombre");
        String categoria = document.getString("categoria");
        Gasto g = new Gasto();
        g.setBalance(balance);
        g.setNombre(nombre);
        g.setCategoria(categoria);
        g.setFechaCreacion(fecha);
        return g;
    }
}

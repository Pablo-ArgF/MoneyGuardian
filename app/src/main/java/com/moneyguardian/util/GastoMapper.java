package com.moneyguardian.util;

import com.google.firebase.firestore.DocumentSnapshot;
import com.moneyguardian.modelo.Gasto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GastoMapper {

    public static Gasto map(DocumentSnapshot document) {
        if (document.getData() != null) {
            float balance = document.get("balance", Float.class);
            Date fecha = null;
            try {
                fecha = new SimpleDateFormat("dd-MM-yyyy").parse(document.get("fechaCreacion", String.class));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String nombre = document.getString("nombre");
            String categoria = document.get("categoria", String.class);
            String ID = document.getId();
            Gasto g = new Gasto();
            g.setBalance(balance);
            g.setNombre(nombre);
            g.setCategoria(categoria);
            g.setFechaCreacion(fecha);
            g.setUUID(ID);
            return g;
        }
        return new Gasto();
    }
}

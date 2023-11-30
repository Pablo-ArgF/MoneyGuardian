package com.moneyguardian.util;

import android.net.Uri;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.moneyguardian.R;
import com.moneyguardian.modelo.ItemPagoConjunto;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagosConjuntosUtil {

    public static PagoConjunto getPagoConjuntoFrom(QueryDocumentSnapshot document) {
        String nombre = (String) document.getData().get("nombre");
        Uri imagen = null;
        if (document.getData().get("imagen") != null) {
            imagen = Uri.parse((String) document.getData().get("imagen"));
        }
        Date fechaPago = ((Timestamp) document.getData().get("fechaPago")).toDate();
        Date fechaLimite = ((Timestamp) document.getData().get("fechaLimite")).toDate();


        List<Map<String, Map<String, Double>>> itemsPagoSinTransform =
                (List<Map<String, Map<String, Double>>>)
                        document.getData().get("itemsPago");

        List<ItemPagoConjunto> itemsPago = new ArrayList<>();

        if (itemsPagoSinTransform != null) {
            for (int i = 0; i < itemsPagoSinTransform.size(); i++) {
                for (Map.Entry<String, Map<String, Double>> item :
                        itemsPagoSinTransform.get(i).entrySet()) {
                    HashMap<UsuarioParaParcelable, Double> usuariosConDinero = new HashMap<>();
                    for (Map.Entry<String, Double> usuarios : item.getValue().entrySet()) {
                        usuariosConDinero.put(new UsuarioParaParcelable(usuarios.getKey()), usuarios.getValue());
                    }

                    itemsPago.add(new ItemPagoConjunto(item.getKey(), usuariosConDinero));
                }
            }
        }
        if (nombre == null || fechaLimite == null || fechaPago == null) {
            throw new RuntimeException(String.valueOf(R.string.ErrorBaseDatosPago));
        }

        return new PagoConjunto(document.getId(), nombre, fechaPago, new ArrayList<>(), imagen, fechaLimite, itemsPago);
    }

}

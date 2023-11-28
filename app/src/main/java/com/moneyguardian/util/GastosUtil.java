package com.moneyguardian.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.Gasto;

import java.util.Map;

public class GastosUtil {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addGasto(Gasto g) {

    }

    public static void deleteGasto(Gasto g) {
        if (g.getReference() != null) {
            g.getReference().delete();
        }
    }

    public static void deleteGastos(Map<Gasto, Boolean> gastos) {
        for (Map.Entry<Gasto, Boolean> entry : gastos.entrySet()) {
            if (entry.getValue()) {
                deleteGasto(entry.getKey());
            }
        }
    }

}

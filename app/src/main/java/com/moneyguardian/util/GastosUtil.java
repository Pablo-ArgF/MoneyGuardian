package com.moneyguardian.util;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;

import java.util.ArrayList;
import java.util.Map;

import kotlin.NotImplementedError;

public class GastosUtil {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addGasto(Gasto g) {

    }

    public static void deleteGasto(Gasto g) {
        if (g.getReference() != null) {
            g.getReference().delete();
            // Borramos ese pago de la lista de pagos del usuario
            db.collection("users/").document(
                    auth.getUid().toString()).update("gastos", FieldValue.arrayRemove(g.getReference()));
        }
    }

    public static void deleteGastos(Map<Gasto, Boolean> gastos) {
        for (Map.Entry<Gasto, Boolean> entry : gastos.entrySet()) {
            if (entry.getValue()) {
                deleteGasto(entry.getKey());
            }
        }
    }

    public static int getImageFor(Gasto gasto) {
        switch (gasto.getCategoria()) {
            case "Alimentación":
                return R.drawable.ic_alimentacion;
            case "Salud":
                return R.drawable.ic_salud;
            case "Transporte":
                return R.drawable.ic_transport;
            case "Educación":
                return R.drawable.ic_educacion;
            default:
                return gasto.getBalance() > 0 ? R.drawable.ic_money : R.drawable.ic_money_off;
        }
    }

}

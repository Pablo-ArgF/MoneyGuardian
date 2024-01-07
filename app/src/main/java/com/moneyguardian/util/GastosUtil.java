package com.moneyguardian.util;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kotlin.NotImplementedError;

public class GastosUtil {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Gasto addGasto(Gasto gasto) {
        String gastoUUID = UUID.randomUUID().toString();
        Map<String, Object> gastoDB = new HashMap<>();
        gastoDB.put("nombre", gasto.getNombre());
        gastoDB.put("categoria", gasto.getCategoria());
        gastoDB.put("balance", gasto.getBalance());
        gastoDB.put("fechaCreacion", gasto.getFechaCreacion());
        DocumentReference gastoReference = db.collection("gastos/").document(gastoUUID);
        gastoReference.set(gastoDB);
        db.collection("users/").document(auth.getUid()).update("gastos",
                FieldValue.arrayUnion(gastoReference));
        gasto.setUUID(gastoUUID);
        return gasto;
    }

    public static void deleteGasto(Gasto g) {
        if (g.getReference() != null) {
            g.getReference().delete();
            // Borramos ese pago de la lista de pagos del usuario
            db.collection("users/").document(
                    auth.getUid().toString()).update("gastos", FieldValue.arrayRemove(g.getReference()));
        } else if (g.getUUID() != null) {
            DocumentReference ref = db.collection("gastos/").document(g.getUUID());
            g.setReference(ref);
            deleteGasto(g);
        }
    }

    public static List<Gasto> deleteGastos(Map<Gasto, Boolean> gastos) {
        List<Gasto> gastoList = new ArrayList<>();
        for (Map.Entry<Gasto, Boolean> entry : gastos.entrySet()) {
            if (entry.getValue()) {
                gastoList.add(entry.getKey());
                deleteGasto(entry.getKey());
            }
        }
        return gastoList;
    }

    public static int getImageFor(String category) {
        switch (category) {
            case "Alimentación":
                return R.drawable.ic_alimentacion;
            case "Salud":
                return R.drawable.ic_salud;
            case "Transporte":
                return R.drawable.ic_transport;
            case "Educación":
                return R.drawable.ic_educacion;
            case "Trabajo":
                return R.drawable.ic_trabajo;
            case "Ingresos pasivos":
                return R.drawable.ic_inv_pasivo;
            case "Inversion":
                return R.drawable.ic_inversion;
            default:
                return R.drawable.ic_money_off;
        }
    }

}

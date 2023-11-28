package com.moneyguardian.util;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.Gasto;

import java.util.ArrayList;
import java.util.Map;

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
            /**userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override public void onSuccess(DocumentSnapshot documentSnapshot) {
            ArrayList<DocumentReference> docs = (ArrayList<DocumentReference>) documentSnapshot.get("gastos");
            // TODO concurrency error
            for (DocumentReference dRef : docs) {
            if (dRef.equals(g.getReference())) {
            docs.remove(dRef);
            }
            }
            userRef.update("gastos", docs);
            }
            });
             **/
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

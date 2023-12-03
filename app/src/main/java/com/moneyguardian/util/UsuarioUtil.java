package com.moneyguardian.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.UsuarioParaParcelable;

import java.util.Map;

public class UsuarioUtil {
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void setEntryUser(String id, Map.Entry<UsuarioParaParcelable, Double> entry) {
        db.collection("/users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    String nombre = (String) doc.get("name");
                    String email = (String) doc.get("email");
                    String imagen = (String) doc.get("profilePicture");
                    UsuarioParaParcelable user = new UsuarioParaParcelable(nombre, email, imagen, doc.getId());
                    
                }
            }
        });
    }
}

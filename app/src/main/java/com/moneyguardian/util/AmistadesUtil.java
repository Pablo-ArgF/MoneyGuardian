package com.moneyguardian.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AmistadesUtil {
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();



    public static void aceptarSolicitudAmistad(Usuario usuario){
        //TODO
    }
    public static void denegarSolicitudAmistad(Usuario usuario){
        //TODO
    }

    public static void enviarSolicitudAmistad(Usuario usuario){
        //TODO
    }
}

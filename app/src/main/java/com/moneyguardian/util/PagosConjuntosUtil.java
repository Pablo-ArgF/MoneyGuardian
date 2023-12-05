package com.moneyguardian.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.PagoConjunto;
import com.moneyguardian.modelo.UsuarioParaParcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagosConjuntosUtil {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static void addPagoConjunto(PagoConjunto pagoConjunto, List<UsuarioParaParcelable> participantes, String UUID) {
        // Creación del Map para persistencia
        Map<String, Object> pagoConjuntoDoc = new HashMap<>();
        pagoConjuntoDoc.put("nombre", pagoConjunto.getNombre());
        pagoConjuntoDoc.put("imagen", pagoConjunto.getImagen());
        pagoConjuntoDoc.put("fechaLimite", pagoConjunto.getFechaLimite());
        pagoConjuntoDoc.put("fechaPago", pagoConjunto.getFechaPago());

        // Guardamos los participantes como una lista de referencias
        ArrayList<DocumentReference> nestedParticipantes = new ArrayList<>();
        for (UsuarioParaParcelable user : participantes) {
            nestedParticipantes.add(db.document("users/" + user.getId()));
        }
        // Refencia al propio usuario que crea el pago
        DocumentReference creadorReference = db.document("users/" + (auth.getCurrentUser().getUid()));
        nestedParticipantes.add(creadorReference);
        pagoConjuntoDoc.put("participantes", nestedParticipantes);

        // Guardamos al usuario actual como el creador
        pagoConjuntoDoc.put("creador", creadorReference);

        // La referencia al documento
        DocumentReference pagoRef = db.collection("pagosConjuntos").document(UUID);
        // Guardamos el pago
        pagoRef.set(pagoConjuntoDoc);

        CollectionReference usersRef = db.collection("users/");
        usersRef.document(auth.getCurrentUser().getUid()).update("pagosConjuntos", FieldValue.arrayUnion(pagoRef));
        // Añadimos a cada participante el pago conjunto
        for (DocumentReference ref : nestedParticipantes) {
            ref.update("pagosConjuntos", FieldValue.arrayUnion(pagoRef));
        }
    }

}

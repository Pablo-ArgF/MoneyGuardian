package com.moneyguardian.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.moneyguardian.modelo.Usuario;

public class AmistadesUtil {
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();


    /**
     * This method adds as friends the current auth user and the usuario user.
     * @param usuario to which current user will start to be friend
     */
    public static void aceptarSolicitudAmistad(Usuario usuario){
        //we update current user to add the usuario as friend
        db.collection("users")
                .document(auth.getUid())
                .update("friends", FieldValue.arrayUnion(db.collection("users").document(usuario.getId())))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //we update the usuario user to have the current logged in user as friend also
                        db.collection("users")
                            .document(usuario.getId())
                            .update("friends", FieldValue.arrayUnion(db.collection("users").document(auth.getUid())))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //we end up deleting the user from the friend request list
                                    eliminarDeListaSolicitudes(usuario);
                                }
                            });
                        //we remove current user from friend requests if exists
                        db.collection("users")
                                .document(usuario.getId())
                                .update("friendRequests", FieldValue.arrayRemove(
                                        db.collection("users").document(auth.getUid())));
                    }
                });
    }

    /**
     * This method rejects the friend request from usuario to current auth user
     * @param usuario to which current user will start to be friend
     */
    public static void denegarSolicitudAmistad(Usuario usuario){
        //we just remove the requests from the friendRequests array
        eliminarDeListaSolicitudes(usuario);
    }

    private static void eliminarDeListaSolicitudes(Usuario usuario) {
        //we just remove the requests from the friendRequests array
        db.collection("users")
                .document(auth.getUid())
                .update("friendRequests",  FieldValue.arrayRemove(db.collection("users").document(usuario.getId())))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.getException();
                    }
                });
    }

    /**
     * Adds to the usuario passsed as parameter a new entry on the "friendRequests" array containing
     * the id of the current authenticated user
     * @param usuario to whom we are sending the request
     */
    public static void enviarSolicitudAmistad(Usuario usuario){
        db.collection("users")
                .document(usuario.getId())
                .update("friendRequests",
                        FieldValue.arrayUnion(db.collection("users").document(auth.getUid())))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.getException();
                    }
                });
    }

    /**
     * Deletes the friend from the friend list and from the list of the other user
     * @param amigo friend to be removed
     */
    public static void borrarAmigo(Usuario amigo) {
        db.collection("users")
                .document(auth.getUid())
                .update("friends", FieldValue.arrayRemove(db.collection("users").document(amigo.getId())))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //we remove from the friend list
                        db.collection("users")
                                .document(amigo.getId())
                                .update("friends", FieldValue.arrayRemove(db.collection("users").document(auth.getUid())));
                    }
                });
    }
}

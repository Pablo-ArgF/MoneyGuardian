package com.moneyguardian.util;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.moneyguardian.modelo.Usuario;
import com.moneyguardian.modelo.UsuarioParaParcelable;

import java.util.ArrayList;
import java.util.List;

public class UsuarioMapper {


    /**
     * Maps the basic attrs, name, email and profile picture
     * @param ref document snapshot from which we extract the data
     * @return the user containing the data stored in the ref
     */
    public static Usuario mapBasics(DocumentSnapshot ref){
        Usuario usuario = new Usuario();
        usuario.setId(ref.getId());
        usuario.setNombre(ref.get("name",String.class));
        usuario.setCorreo(ref.get("email",String.class));
        usuario.setUriImg(ref.get("profilePicture",String.class));

        return usuario;
    }

    /**
     * Maps the basic attrs, name, email and profile picture
     * @param ref document snapshot from which we extract the data
     * @return the user containing the data stored in the ref
     */
    public static UsuarioParaParcelable mapBasicsParcelable(DocumentSnapshot ref){
        UsuarioParaParcelable usuario = new UsuarioParaParcelable();
        usuario.setId(ref.getId());
        usuario.setNombre(ref.get("name",String.class));
        usuario.setEmail(ref.get("email",String.class));
        usuario.setImageURI(ref.get("profilePicture",String.class));

        return usuario;
    }

}

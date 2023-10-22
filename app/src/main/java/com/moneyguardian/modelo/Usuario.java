package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Usuario implements Parcelable {

    private String nombreUsuario;
    private String correo;
    private List<Usuario> amigos;
    private List<PagoConjunto> misPagosConjuntos;
    // Consider adding a field for 'Foto' if needed


    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public Usuario(String nombreUsuario, String correo, List<Usuario> amigos, List<PagoConjunto> misPagosConjuntos) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.amigos = amigos;
        this.misPagosConjuntos = misPagosConjuntos;
    }


    protected Usuario(Parcel in) {
        nombreUsuario = in.readString();
        correo = in.readString();
        amigos = in.createTypedArrayList(Usuario.CREATOR);
        misPagosConjuntos = in.createTypedArrayList(PagoConjunto.CREATOR);
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public List<Usuario> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }

    public List<PagoConjunto> getMisPagosConjuntos() {
        return misPagosConjuntos;
    }

    public void setMisPagosConjuntos(List<PagoConjunto> misPagosConjuntos) {
        this.misPagosConjuntos = misPagosConjuntos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombreUsuario);
        dest.writeString(correo);
        dest.writeTypedList(amigos);
        dest.writeTypedList(misPagosConjuntos);
        // Write 'Foto' if it's a Parcelable or some other type
        // Example: dest.writeParcelable(foto, flags);
    }
}

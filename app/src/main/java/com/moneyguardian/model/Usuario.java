package com.moneyguardian.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Usuario implements Parcelable {

    private String nombre;
    private String email;
    private String foto;
    private List<Usuario> amigos;
    private List<PagosConjuntos> misPagosConjuntos;

    public Usuario(String nombre, String email, String foto, List<Usuario> amigos, List<PagosConjuntos> misPagosConjuntos) {
        this.nombre = nombre;
        this.email = email;
        this.foto = foto;
        this.amigos = amigos;
        this.misPagosConjuntos = misPagosConjuntos;
    }

    protected Usuario(Parcel in) {
        nombre = in.readString();
        email = in.readString();
        foto = in.readString();
        amigos = in.createTypedArrayList(Usuario.CREATOR);
        misPagosConjuntos = in.createTypedArrayList(PagosConjuntos.CREATOR);
    }

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }

    public List<PagosConjuntos> getMisPagosConjuntos() {
        return misPagosConjuntos;
    }

    public void setMisPagosConjuntos(List<PagosConjuntos> misPagosConjuntos) {
        this.misPagosConjuntos = misPagosConjuntos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(email);
        parcel.writeString(foto);
        parcel.writeTypedList(amigos);
        parcel.writeTypedList(misPagosConjuntos);
    }
}

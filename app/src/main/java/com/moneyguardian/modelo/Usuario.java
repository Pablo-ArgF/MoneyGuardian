package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

public class Usuario implements Parcelable {

    private String nombre;
    private String correo;
    private List<Usuario> amigos;
    private List<PagoConjunto> misPagosConjuntos; //TODO esto no creo que lo haya que tener
    private String uriImg;




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

    public Usuario(String nombre, String correo,String imgUri, List<Usuario> amigos, List<PagoConjunto> misPagosConjuntos) {
        this.nombre = nombre;
        this.correo = correo;
        this.amigos = amigos;
        this.misPagosConjuntos = misPagosConjuntos;
        this.uriImg = imgUri;
    }

    public Usuario() {
    }

    protected Usuario(Parcel in) {
        nombre = in.readString();
        correo = in.readString();
        uriImg = in.readString();
        amigos = in.createTypedArrayList(Usuario.CREATOR);
        misPagosConjuntos = in.createTypedArrayList(PagoConjunto.CREATOR);
    }

    public String getUriImg() {
        return uriImg;
    }

    public void setUriImg(String uriImg) {
        this.uriImg = uriImg;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        dest.writeString(nombre);
        dest.writeString(correo);
        dest.writeTypedList(amigos);
        dest.writeTypedList(misPagosConjuntos);
        dest.writeString(uriImg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return nombre.equals(usuario.nombre) && correo.equals(usuario.correo)
                && amigos.equals(usuario.amigos) && misPagosConjuntos.equals(usuario.misPagosConjuntos);
    }
}

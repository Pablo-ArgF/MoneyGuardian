package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Usuario implements Parcelable {

    private String nombre;
    private String correo;
    private List<UsuarioParaParcelable> amigos;
    private List<PagoConjunto> misPagosConjuntos; //TODO esto no creo que lo haya que tener
    // Consider adding a field for 'Foto' if needed


    public Usuario(String nombre, String correo, List<UsuarioParaParcelable> amigos, List<PagoConjunto> misPagosConjuntos) {
        this.nombre = nombre;
        this.correo = correo;
        this.amigos = amigos;
        this.misPagosConjuntos = misPagosConjuntos;
    }

    protected Usuario(Parcel in) {
        nombre = in.readString();
        correo = in.readString();
        amigos = in.createTypedArrayList(UsuarioParaParcelable.CREATOR);
        misPagosConjuntos = in.createTypedArrayList(PagoConjunto.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(correo);
        dest.writeTypedList(amigos);
        dest.writeTypedList(misPagosConjuntos);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public List<UsuarioParaParcelable> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<UsuarioParaParcelable> amigos) {
        this.amigos = amigos;
    }

    public List<PagoConjunto> getMisPagosConjuntos() {
        return misPagosConjuntos;
    }

    public void setMisPagosConjuntos(List<PagoConjunto> misPagosConjuntos) {
        this.misPagosConjuntos = misPagosConjuntos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return nombre.equals(usuario.nombre) && correo.equals(usuario.correo)
                && amigos.equals(usuario.amigos) && misPagosConjuntos.equals(usuario.misPagosConjuntos);
    }

    @Override
    public String toString() {
        return nombre + " " + correo;
    }
}

package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class GrupoUsuarios implements Parcelable {

    private String nombre;
    private List<Usuario> usuarios;


    public GrupoUsuarios(String nombre, List<Usuario> usuarios) {
        this.nombre = nombre;
        this.usuarios = usuarios;
    }

    protected GrupoUsuarios(Parcel in) {
        nombre = in.readString();
        usuarios = in.createTypedArrayList(Usuario.CREATOR);
    }

    public static final Creator<GrupoUsuarios> CREATOR = new Creator<GrupoUsuarios>() {
        @Override
        public GrupoUsuarios createFromParcel(Parcel in) {
            return new GrupoUsuarios(in);
        }

        @Override
        public GrupoUsuarios[] newArray(int size) {
            return new GrupoUsuarios[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeTypedList(usuarios);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}

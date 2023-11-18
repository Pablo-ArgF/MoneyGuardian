package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class UsuarioParaParcelable implements Parcelable {

    private String nombre;
    private String email;

    public UsuarioParaParcelable(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }


    protected UsuarioParaParcelable(Parcel in) {
        nombre = in.readString();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UsuarioParaParcelable> CREATOR = new Creator<UsuarioParaParcelable>() {
        @Override
        public UsuarioParaParcelable createFromParcel(Parcel in) {
            return new UsuarioParaParcelable(in);
        }

        @Override
        public UsuarioParaParcelable[] newArray(int size) {
            return new UsuarioParaParcelable[size];
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioParaParcelable that = (UsuarioParaParcelable) o;
        return Objects.equals(nombre, that.nombre) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, email);
    }
}

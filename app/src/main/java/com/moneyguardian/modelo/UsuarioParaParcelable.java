package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UsuarioParaParcelable implements Parcelable {

    private String nombre;
    private String email;
    private String imageURI;

    public UsuarioParaParcelable(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public UsuarioParaParcelable(String nombre, String email, String imageURI) {
        this(nombre, email);
        this.imageURI = imageURI;
    }


    protected UsuarioParaParcelable(Parcel in) {
        nombre = in.readString();
        email = in.readString();
        imageURI = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(email);
        dest.writeString(imageURI);
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
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
}

package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class UsuarioParaParcelable implements Parcelable, Comparable<UsuarioParaParcelable> {

    private String nombre;
    private String email;
    private String imageURI;
    private String id;

    public UsuarioParaParcelable() {
    }

    public UsuarioParaParcelable(String nombre, String email, String imageURI, String id) {
        this.nombre = nombre;
        this.email = email;
        this.imageURI = imageURI;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UsuarioParaParcelable(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public UsuarioParaParcelable(String id) {
        this.id = id;
    }

    public UsuarioParaParcelable(String nombre, String email, String imageURI) {
        this(nombre, email);
        this.imageURI = imageURI;
    }


    protected UsuarioParaParcelable(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        email = in.readString();
        imageURI = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(email);
        dest.writeString(imageURI);
        dest.writeString(id);
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

    @Override
    public String toString() {
        return getNombre();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        UsuarioParaParcelable newUser = (UsuarioParaParcelable) obj;

        if (!newUser.getId().equals(getId())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public int compareTo(UsuarioParaParcelable o) {
        int result = 0;
        if (this.id != null && o.getId() != null) {
            result = this.id.compareTo(o.getId());
        }
        if (result == 0) {
            result = this.email.compareTo(o.getEmail());
        }
        return result;
    }
}

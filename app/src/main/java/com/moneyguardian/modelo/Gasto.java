package com.moneyguardian.modelo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Gasto implements Parcelable {

    private String nombre;
    private float balance;
    private String imagen;

    public Gasto() {

    }

    public Gasto(String nombre, float balance, String imagen) {
        this.nombre = nombre;
        this.balance = balance;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeFloat(balance);
        dest.writeString(imagen);
    }

    protected Gasto(Parcel in) {
        nombre = in.readString();
        balance = in.readFloat();
        imagen = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Gasto> CREATOR = new Creator<Gasto>() {
        @Override
        public Gasto createFromParcel(Parcel in) {
            return new Gasto(in);
        }

        @Override
        public Gasto[] newArray(int size) {
            return new Gasto[size];
        }
    };
}

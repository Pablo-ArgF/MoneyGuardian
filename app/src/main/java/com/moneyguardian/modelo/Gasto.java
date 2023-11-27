package com.moneyguardian.modelo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Gasto implements Parcelable {

    private String nombre;
    private float balance;
    private String categoria;
    private Date fechaCreacion;

    public Gasto() {

    }

    public Gasto(String nombre, float balance, String categoria) {
        this.nombre = nombre;
        this.balance = balance;
        this.categoria = categoria;
    }

    public Gasto(String nombre, float balance, String imagen, Date fechaCreación) {
        this(nombre, balance, imagen);
        this.fechaCreacion = fechaCreación;
    }

    public Gasto(String nombre, float balance, String imagen, String fechaCreación) {
        this(nombre, balance, imagen);
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy", new Locale("es"));
        try {
            this.fechaCreacion = formater.parse(fechaCreación);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeFloat(balance);
        dest.writeString(categoria);
    }

    protected Gasto(Parcel in) {
        nombre = in.readString();
        balance = in.readFloat();
        categoria = in.readString();
        fechaCreacion = in.readParcelable(Date.class.getClassLoader());
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

    public String getFechaCreacion() {
        return new SimpleDateFormat("dd-MM-yyyy", new Locale("es")).format(this.fechaCreacion);
    }
    public Date getFechaCreacionAsDate(){
        return fechaCreacion;
    }
}

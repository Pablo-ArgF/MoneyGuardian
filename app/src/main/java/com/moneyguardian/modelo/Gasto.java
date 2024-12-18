package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Gasto implements Parcelable {

    private String nombre;
    private float balance;
    private String categoria;
    private Date fechaCreacion;
    private DocumentReference reference;
    private String UUID;

    public Gasto() {

    }

    public Gasto(String nombre, float balance, String categoria, Date fechaCreacion) {
        this.nombre = nombre;
        this.balance = balance;
        this.categoria = categoria;
        this.fechaCreacion = fechaCreacion;
    }

    public Gasto(String nombre, float balance, Date fechaCreacion) {
        this(nombre, balance, (String) null);
        this.fechaCreacion = fechaCreacion;
    }

    public Gasto(String nombre, float balance, String categoria) {
        this(nombre, balance, categoria, new Date());
    }

    public Gasto(String nombre, float balance, String categoria, String fechaCreacion) {
        this(nombre, balance, categoria);
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy", new Locale("es"));
        try {
            this.fechaCreacion = formater.parse(fechaCreacion);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setReference(DocumentReference ref) {
        this.reference = ref;
    }

    public DocumentReference getReference() {
        return this.reference;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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
        dest.writeSerializable(fechaCreacion);
        dest.writeString(UUID);
    }

    protected Gasto(Parcel in) {
        nombre = in.readString();
        balance = in.readFloat();
        categoria = in.readString();
        fechaCreacion = (Date) in.readSerializable();
        UUID = in.readString();
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

    public void setUUID(String gastoUUID) {
        this.UUID = gastoUUID;
    }

    public String getUUID() {
        return this.UUID;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaCreacionAsDate() {
        return fechaCreacion;
    }
}

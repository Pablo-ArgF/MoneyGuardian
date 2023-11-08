package com.moneyguardian.modelo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PagoConjunto implements Parcelable {
    private String nombre;
    private Date fechaPago;
    private Date fechaLimite;
    private Uri imagen = null;
    private List<Usuario> participantes;
    private List<ItemPagoConjunto> items;

    public PagoConjunto(String nombre, Date fechaPago, List<Usuario> participantes, List<ItemPagoConjunto> items) {
        this.nombre = nombre;
        this.fechaPago = fechaPago;
        this.participantes = participantes;
        this.items = items;
    }

    public PagoConjunto(String nombre, Date fecha, List<Usuario> participantes, Uri imagen) {
        this(nombre, fecha, participantes, new ArrayList<ItemPagoConjunto>());
        this.imagen = imagen;
    }

    public PagoConjunto(String nombre, Date fechaPago, List<Usuario> participantes) {
        this(nombre, fechaPago, participantes, new ArrayList<ItemPagoConjunto>());
    }

    public PagoConjunto(String nombre, Date fechaPago, List<Usuario> participantes, Date fechaLimite) {
        this(nombre, fechaPago, participantes, new ArrayList<ItemPagoConjunto>());
        this.fechaLimite = fechaLimite;
    }


    protected PagoConjunto(Parcel in) {
        nombre = in.readString();
        // TODO error aqui a la hora de hacer el parcelable
        participantes = in.createTypedArrayList(Usuario.CREATOR);
    }

    public static final Creator<PagoConjunto> CREATOR = new Creator<PagoConjunto>() {
        @Override
        public PagoConjunto createFromParcel(Parcel in) {
            return new PagoConjunto(in);
        }

        @Override
        public PagoConjunto[] newArray(int size) {
            return new PagoConjunto[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeSerializable(fechaPago); // Write Date as serializable
        dest.writeTypedList(participantes);
        dest.writeTypedList(items);
        // dest.writeSerializable(fechaLimite);
        // dest.writeParcelable(imagen, flags); // Va a funcionar?
        // Write 'Foto' or 'Icono' if they are Parcelable or some other type
        // Example: dest.writeParcelable(foto, flags);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public Uri getImagen() {
        return this.imagen;
    }

    public void setImagen(Uri imagen) {
        this.imagen = imagen;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public List<ItemPagoConjunto> getItems() {
        return items;
    }

    public void setItems(List<ItemPagoConjunto> items) {
        this.items = items;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Date getFechaLimite() {
        return this.fechaLimite;
    }

}

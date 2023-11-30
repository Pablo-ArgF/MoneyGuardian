package com.moneyguardian.modelo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PagoConjunto implements Parcelable {

    private String id;
    private String nombre;
    private Date fechaPago;
    private Date fechaLimite;
    private Uri imagen = null;
    private List<UsuarioParaParcelable> participantes;
    private List<ItemPagoConjunto> items;

    public PagoConjunto() {

    }

    public PagoConjunto(String id, String nombre, Date fechaPago, ArrayList<UsuarioParaParcelable>
            es, Uri imagen, Date fechaLimite, List<ItemPagoConjunto> itemsPago) {
        this(id, nombre, fechaPago, es, itemsPago);
        this.fechaLimite = fechaLimite;
        this.imagen = imagen;
    }

    public PagoConjunto(String id, String nombre, Date fechaPago, List<UsuarioParaParcelable> participantes, List<ItemPagoConjunto> items) {
        this.id = id;
        this.nombre = nombre;
        this.fechaPago = fechaPago;
        this.participantes = participantes;
        this.items = items;
    }

    public PagoConjunto(String id, String nombre, Date fecha, List<UsuarioParaParcelable> participantes, Uri imagen, Date fechaLimite) {
        this(id, nombre, fecha, participantes, fechaLimite);
        this.imagen = imagen;
    }

    public PagoConjunto(String id, String nombre, Date fechaPago, List<UsuarioParaParcelable> participantes) {
        this(id, nombre, fechaPago, participantes, new ArrayList<ItemPagoConjunto>());
    }

    public PagoConjunto(String id, String nombre, Date fechaPago, List<UsuarioParaParcelable> participantes, Date fechaLimite) {
        this(id, nombre, fechaPago, participantes, new ArrayList<ItemPagoConjunto>());
        this.fechaLimite = fechaLimite;
    }


    protected PagoConjunto(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        imagen = in.readParcelable(Uri.class.getClassLoader());
        fechaLimite = (Date) in.readSerializable(); // OJO, no se puede poner esta linea
        fechaPago = (Date) in.readSerializable(); // ni esta antes de leer las listas
        // No se por qué, pero si se leen antes las listas que las fechas
        // al parsear la lista de items una de ellas va a generarse con un tamaño enorme
        // y dará error de heap y un nullpointer (por el metodo de calcular pago), porque la inicializa
        // con tamaño > 0 aunque esté vacía
        participantes = in.createTypedArrayList(UsuarioParaParcelable.CREATOR);
        items = in.createTypedArrayList(ItemPagoConjunto.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeParcelable(imagen, flags);
        dest.writeSerializable(fechaLimite);
        dest.writeSerializable(fechaPago);
        dest.writeTypedList(participantes);
        dest.writeTypedList(items);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<UsuarioParaParcelable> getParticipantes() {
        return participantes;
    }

    public Uri getImagen() {
        return this.imagen;
    }

    public void setImagen(Uri imagen) {
        this.imagen = imagen;
    }

    public void setParticipantes(List<UsuarioParaParcelable> participantes) {
        this.participantes = participantes;
    }

    public List<ItemPagoConjunto> getItems() {
        return items;
    }

    public void addItem(ItemPagoConjunto ipc) {
        items.add(ipc);
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

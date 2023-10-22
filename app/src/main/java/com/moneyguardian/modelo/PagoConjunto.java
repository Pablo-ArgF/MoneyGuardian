package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class PagoConjunto implements Parcelable {
    private String nombre;
    private Date fechaPago;
    // Consider adding a field for 'Foto' or 'Icono' if needed
    private List<Usuario> participantes;
    private List<ItemPagoConjunto> items;

    public PagoConjunto(String nombre, Date fechaPago, List<Usuario> participantes, List<ItemPagoConjunto> items) {
        this.nombre = nombre;
        this.fechaPago = fechaPago;
        this.participantes = participantes;
        this.items = items;
    }


    protected PagoConjunto(Parcel in) {
        nombre = in.readString();
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
        // Write 'Foto' or 'Icono' if they are Parcelable or some other type
        // Example: dest.writeParcelable(foto, flags);
    }
}

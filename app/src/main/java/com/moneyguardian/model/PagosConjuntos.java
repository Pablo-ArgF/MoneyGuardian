package com.moneyguardian.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PagosConjuntos implements Parcelable{

    private String name;
    private String foto;
    private List<Usuario> participantes;
    private List<ItemPago> itemsPago;

    public PagosConjuntos(String name, String foto, List<Usuario> participantes, List<ItemPago> itemsPago) {
        this.name = name;
        this.foto = foto;
        this.participantes = participantes;
        this.itemsPago = itemsPago;
    }

    protected PagosConjuntos(Parcel in) {
        name = in.readString();
        foto = in.readString();
        itemsPago = in.createTypedArrayList(ItemPago.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(foto);
        dest.writeTypedList(itemsPago);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PagosConjuntos> CREATOR = new Creator<PagosConjuntos>() {
        @Override
        public PagosConjuntos createFromParcel(Parcel in) {
            return new PagosConjuntos(in);
        }

        @Override
        public PagosConjuntos[] newArray(int size) {
            return new PagosConjuntos[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public List<ItemPago> getItemsPago() {
        return itemsPago;
    }

    public void setItemsPago(List<ItemPago> itemsPago) {
        this.itemsPago = itemsPago;
    }
}

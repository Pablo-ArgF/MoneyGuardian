package com.moneyguardian.model;

import static java.lang.Math.abs;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ItemPago implements Parcelable {

    private String name;
    private String foto;
    private HashMap<Usuario,Integer> usariosPagos;

    public ItemPago(String name, String foto, HashMap<Usuario, Integer> usariosPagos) {
        this.name = name;
        this.foto = foto;
        this.usariosPagos = usariosPagos;
    }

    protected ItemPago(Parcel in) {
        name = in.readString();
        foto = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(foto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemPago> CREATOR = new Creator<ItemPago>() {
        @Override
        public ItemPago createFromParcel(Parcel in) {
            return new ItemPago(in);
        }

        @Override
        public ItemPago[] newArray(int size) {
            return new ItemPago[size];
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

    public HashMap<Usuario, Integer> getUsariosPagos() {
        return usariosPagos;
    }

    public void setUsariosPagos(HashMap<Usuario, Integer> usariosPagos) {
        this.usariosPagos = usariosPagos;
    }

    public int getMoney(){
        int total = 0;

        for(Usuario u : usariosPagos.keySet()){
            total += abs(usariosPagos.getOrDefault(u,0));
        }

        return total;
    }

    public String getUser() {

        return "Usuario que pago";

    }
}

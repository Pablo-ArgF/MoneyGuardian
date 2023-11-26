package com.moneyguardian.modelo;

import static java.lang.Math.abs;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.HashMap;


public class ItemPagoConjunto implements Parcelable {
    private String id;
    private String nombre;
    // Consider adding a field for 'Foto' or 'Icono' if needed
    private HashMap<UsuarioParaParcelable, Double> pagos;

    public ItemPagoConjunto(String id,String nombre, HashMap<UsuarioParaParcelable, Double> pagos) {
        this.id = id;
        this.nombre = nombre;
        this.pagos = pagos;
    }



    protected ItemPagoConjunto(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        pagos = new HashMap<>();
        pagos = in.readHashMap(UsuarioParaParcelable.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeMap(pagos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemPagoConjunto> CREATOR = new Creator<ItemPagoConjunto>() {
        @Override
        public ItemPagoConjunto createFromParcel(Parcel in) {
            return new ItemPagoConjunto(in);
        }

        @Override
        public ItemPagoConjunto[] newArray(int size) {
            return new ItemPagoConjunto[size];
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

    public HashMap<UsuarioParaParcelable, Double> getPagos() {
        return pagos;
    }

    public void setPagos(HashMap<UsuarioParaParcelable, Double> pagos) {
        this.pagos = pagos;
    }

    public double getMoney(){
        double total = 0;

        for(UsuarioParaParcelable u : pagos.keySet()){
            total += abs(pagos.getOrDefault(u,0.0));
        }

        return Math.round(total*100.0)/100.0;
    }

    public String getUser() {
        return "Usuario que pago";
    }
}

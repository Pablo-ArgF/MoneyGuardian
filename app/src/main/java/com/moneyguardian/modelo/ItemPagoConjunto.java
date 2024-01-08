package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;


public class ItemPagoConjunto implements Parcelable {
    private Double totalDinero;
    private UsuarioParaParcelable userThatPays;
    private String id;
    private String nombre;
    // Consider adding a field for 'Foto' or 'Icono' if needed
    private HashMap<UsuarioParaParcelable, Double> pagos;

    public ItemPagoConjunto(String id,String nombre, HashMap<UsuarioParaParcelable, Double> pagos,
                            UsuarioParaParcelable userThatPays,Double totalDinero) {
        this.id = id;
        this.nombre = nombre;
        this.pagos = pagos;
        this.userThatPays = userThatPays;
        this.totalDinero = totalDinero;
    }



    protected ItemPagoConjunto(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        pagos = new HashMap<>();
        pagos = in.readHashMap(UsuarioParaParcelable.class.getClassLoader());
        userThatPays = in.readParcelable(UsuarioParaParcelable.class.getClassLoader());
        totalDinero = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeMap(pagos);
        dest.writeParcelable(userThatPays,0);
        dest.writeDouble(totalDinero);
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
        if(totalDinero == null){
            return 0;
        }
        return totalDinero;
    }

    public String getUser() {
        return "Usuario que pago";
    }

    public UsuarioParaParcelable getUserThatPays() {
        return userThatPays;
    }

    public void setUserThatPays(UsuarioParaParcelable userThatPays) {
        this.userThatPays = userThatPays;
    }

    public boolean isPagado(){
        return getPagos().get(getUserThatPays()) == 0.0;
    }
}

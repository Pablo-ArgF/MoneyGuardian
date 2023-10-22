package com.moneyguardian.modelo;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.Map;

public class ItemPagoConjunto implements Parcelable {
    private String nombre;
    // Consider adding a field for 'Foto' or 'Icono' if needed
    private Map<Usuario, Integer> pagos;

    // Constructor, getters, and setters...

    public ItemPagoConjunto(String nombre, Map<Usuario, Integer> pagos) {
        this.nombre = nombre;
        this.pagos = pagos;
        // Initialize the Map if you want to track payments by users
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Parcelable implementation
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombre);
        // Write 'Foto' or 'Icono' if they are Parcelable or some other type
        // Example: dest.writeParcelable(foto, flags);
        // Write the 'pagos' Map
        dest.writeMap(pagos);
    }

    public static final Parcelable.Creator<ItemPagoConjunto> CREATOR = new Parcelable.Creator<ItemPagoConjunto>() {
        @Override
        public ItemPagoConjunto createFromParcel(Parcel in) {
            return new ItemPagoConjunto(in);
        }

        @Override
        public ItemPagoConjunto[] newArray(int size) {
            return new ItemPagoConjunto[size];
        }
    };

    private ItemPagoConjunto(Parcel in) {
        nombre = in.readString();
        // Read 'Foto' or 'Icono' if they are Parcelable or some other type
        // Example: foto = in.readParcelable(Foto.class.getClassLoader());
        // Read the 'pagos' Map
        pagos = in.readHashMap(Map.class.getClassLoader());
    }
}

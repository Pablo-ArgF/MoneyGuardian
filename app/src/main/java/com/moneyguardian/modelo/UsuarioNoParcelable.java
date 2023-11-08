package com.moneyguardian.modelo;

public class UsuarioNoParcelable {

    private String nombre;

    public UsuarioNoParcelable(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

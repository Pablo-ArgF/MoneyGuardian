package com.moneyguardian.modelo.dto;

import com.moneyguardian.modelo.UsuarioParaParcelable;

public class DeudaDTO {

    private UsuarioParaParcelable usuario;
    private UsuarioParaParcelable pagador;
    private double cantidad;
    private boolean isUserPagador;

    public DeudaDTO(UsuarioParaParcelable usuario, UsuarioParaParcelable pagador, double cantidad) {
        this.usuario = usuario;
        this.cantidad = cantidad;
        this.pagador = pagador;
        this.isUserPagador = false;
    }

    public boolean isUserPagador() {
        return isUserPagador;
    }

    public void setUserPagador(boolean userPagador) {
        isUserPagador = userPagador;
    }

    public UsuarioParaParcelable getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioParaParcelable usuario) {
        this.usuario = usuario;
    }

    public UsuarioParaParcelable getPagador() {
        return pagador;
    }

    public void setPagador(UsuarioParaParcelable pagador) {
        this.pagador = pagador;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}

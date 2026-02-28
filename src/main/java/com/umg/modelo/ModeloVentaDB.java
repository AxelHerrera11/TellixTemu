package com.umg.modelo;

import java.util.Date;
import java.util.List;

public class ModeloVentaDB {

    public String cliente;
    public String usuarioSistema;
    public int metodoPago;
    public int plazoCredito;
    public String tipoPlazo;
    public Date fechaLimite;
    public String numeroCuenta;
    public double totalVenta;
    public List<ModeloDetalleVenta> detalles;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(String usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public int getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(int metodoPago) {
        this.metodoPago = metodoPago;
    }

    public int getPlazoCredito() {
        return plazoCredito;
    }

    public void setPlazoCredito(int plazoCredito) {
        this.plazoCredito = plazoCredito;
    }

    public String getTipoPlazo() {
        return tipoPlazo;
    }

    public void setTipoPlazo(String tipoPlazo) {
        this.tipoPlazo = tipoPlazo;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public List<ModeloDetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ModeloDetalleVenta> detalles) {
        this.detalles = detalles;
    }
}

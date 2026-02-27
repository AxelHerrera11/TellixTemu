package com.umg.modelo;

import java.sql.Date;
import java.sql.Timestamp;

public class ModeloVentaDB {
    private String nit;
    private Date fechaOperacion;
    private Timestamp horaOperacion;
    private String usuarioSistema;
    private int metodoPago;
    private int plazoCredito;
    private String tipoPlazo;
    private String estado;
    private float total;

    public ModeloVentaDB() {
    }

    public ModeloVentaDB(String nit, Date fechaOperacion, Timestamp horaOperacion, String usuarioSistema, int metodoPago, int plazoCredito, String tipoPlazo, String estado, float total) {
        this.nit = nit;
        this.fechaOperacion = fechaOperacion;
        this.horaOperacion = horaOperacion;
        this.usuarioSistema = usuarioSistema;
        this.metodoPago = metodoPago;
        this.plazoCredito = plazoCredito;
        this.tipoPlazo = tipoPlazo;
        this.estado = estado;
        this.total = total;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public Date getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(Date fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public Timestamp getHoraOperacion() {
        return horaOperacion;
    }

    public void setHoraOperacion(Timestamp horaOperacion) {
        this.horaOperacion = horaOperacion;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}

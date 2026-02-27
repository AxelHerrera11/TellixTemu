package com.umg.modelo;

public class ModeloDetalleVentaDB {
    private int secuencia;
    private int codigo_producto;
    private int cantidad;
    private float precio_bruto;
    private float descuentos;
    private float impuestos;

    public ModeloDetalleVentaDB() {
    }

    public ModeloDetalleVentaDB(int secuencia, int codigo_producto, int cantidad, float precio_bruto, float descuentos, float impuestos) {
        this.secuencia = secuencia;
        this.codigo_producto = codigo_producto;
        this.cantidad = cantidad;
        this.precio_bruto = precio_bruto;
        this.descuentos = descuentos;
        this.impuestos = impuestos;
    }

    public int getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(int secuencia) {
        this.secuencia = secuencia;
    }

    public int getCodigo_producto() {
        return codigo_producto;
    }

    public void setCodigo_producto(int codigo_producto) {
        this.codigo_producto = codigo_producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio_bruto() {
        return precio_bruto;
    }

    public void setPrecio_bruto(float precio_bruto) {
        this.precio_bruto = precio_bruto;
    }

    public float getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(float descuentos) {
        this.descuentos = descuentos;
    }

    public float getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(float impuestos) {
        this.impuestos = impuestos;
    }
}

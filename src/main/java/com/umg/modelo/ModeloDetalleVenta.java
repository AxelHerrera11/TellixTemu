package com.umg.modelo;

public class ModeloDetalleVenta {

    public int codigoProducto;
    public int cantidad;
    public double precioBruto;
    public double descuentos;
    public double impuestos;

    public ModeloDetalleVenta(int codigoProducto, int cantidad, double precioBruto, double descuentos, double impuestos) {
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precioBruto = precioBruto;
        this.descuentos = descuentos;
        this.impuestos = impuestos;
    }
}

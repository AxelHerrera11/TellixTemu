package com.umg.interfaces;
import com.umg.modelo.*;

import java.util.List;

public interface IVenta {
    boolean insertarVenta(ModeloVentaDB modelo, List<ModeloDetalleVentaDB> modeloDetalle);
    boolean eliminarVenta(int secuencia);
    boolean insertarDetalleVenta(List<ModeloDetalleVentaDB> modelo);
    boolean eliminarDetallesVenta(int secuencia);
    boolean eliminarDetalleVenta(int secuencia, int correlativo);
    boolean seleccionarVenta(int secuencia);
    boolean seleccionarDetalleVenta(int seccuencia);
    boolean insertarInventario(int codigo, int cantidad);
    boolean actualizarStock(int codigo, int cantidad    );
    boolean insertarCuentaPorCobrar();
 
}

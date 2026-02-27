package com.umg.implementacion;
import com.umg.interfaces.IVenta;
import com.umg.modelo.*;
import com.umg.seguridad.Sesion;
import sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VentaImp implements IVenta {

    private final Conector con = Sesion.getConexion();

    @Override
    public boolean insertarVenta(ModeloVentaDB modelo, List<ModeloDetalleVentaDB> modeloDetalle) {
        return false;
    }

    @Override
    public boolean eliminarVenta(int secuencia) {
        return false;
    }

    @Override
    public boolean insertarDetalleVenta(List<ModeloDetalleVentaDB> modelo) {
        return false;
    }

    @Override
    public boolean eliminarDetallesVenta(int secuencia) {
        return false;
    }

    @Override
    public boolean eliminarDetalleVenta(int secuencia, int correlativo) {
        return false;
    }

    @Override
    public boolean seleccionarVenta(int secuencia) {
        return false;
    }

    @Override
    public boolean seleccionarDetalleVenta(int seccuencia) {
        return false;
    }

    @Override
    public boolean insertarInventario(int codigo, int cantidad) {
        return false;
    }

    @Override
    public boolean actualizarStock(int codigo, int cantidad) {
        return false;
    }

    @Override
    public boolean insertarCuentaPorCobrar() {
        return false;
    }
}

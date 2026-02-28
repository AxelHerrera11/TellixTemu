package com.umg.implementacion;

import com.umg.interfaces.IVenta;
import com.umg.modelo.*;
import com.umg.seguridad.Sesion;
import sql.Conector;
import sql.SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.*;

public class VentaImp implements IVenta {

    private final Conector con = Sesion.getConexion();
    SQL sql = new SQL();
    private String usuarioSistema;

    public VentaImp() {

    }

    @Override
    public int registrarVenta(ModeloVentaDB venta) {

        Connection cn = null;
        CallableStatement cs = null;

        try {

            cn = con.getConexion();

            cs = cn.prepareCall(sql.getRegistrarVenta());

            cs.setString(1, venta.cliente);
            cs.setString(2, Sesion.getUsuario());
            cs.setInt(3, venta.metodoPago);
            cs.setInt(4, venta.plazoCredito);

            if (venta.tipoPlazo != null)
                cs.setString(5, venta.tipoPlazo);
            else
                cs.setNull(5, Types.CHAR);

            if (venta.fechaLimite != null)
                cs.setDate(6, new java.sql.Date(venta.fechaLimite.getTime()));
            else
                cs.setNull(6, Types.DATE);

            if (venta.numeroCuenta != null)
                cs.setString(7, venta.numeroCuenta);
            else
                cs.setNull(7, Types.VARCHAR);

            cs.setDouble(8, venta.totalVenta);

            // ---- TVP ----
            SQLServerDataTable tvp = new SQLServerDataTable();

            tvp.addColumnMetadata("codigo_producto", Types.INTEGER);
            tvp.addColumnMetadata("cantidad", Types.INTEGER);
            tvp.addColumnMetadata("precio_bruto", Types.DECIMAL);
            tvp.addColumnMetadata("descuentos", Types.DECIMAL);
            tvp.addColumnMetadata("impuestos", Types.DECIMAL);

            for (ModeloDetalleVenta d : venta.detalles) {
                tvp.addRow(
                        d.codigoProducto,
                        d.cantidad,
                        d.precioBruto,
                        d.descuentos,
                        d.impuestos
                );
            }

            ((SQLServerCallableStatement) cs)
                    .setStructured(9, "dbo.TVP_DetalleVenta", tvp);

            cs.registerOutParameter(10, Types.INTEGER);

            cs.execute();

            return cs.getInt(10);

        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
            return -1;
        }
    }


    /* =========================================
       LISTAR METODOS DE PAGO
    ========================================= */
    public List<String[]> listarMetodosPago() {

        List<String[]> lista = new ArrayList<>();

        String sql = """
            SELECT codigo, descripcion
            FROM metodo_liquidacion
            ORDER BY descripcion
        """;

        try (PreparedStatement ps = con.preparar(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                        rs.getString("codigo"),
                        rs.getString("descripcion")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /* =========================================
       BUSCAR CLIENTE
    ========================================= */
    public String[] buscarCliente(String nit) {

        String sql = """
            SELECT
                nit,
                CONCAT(nombre_1,' ',nombre_2,' ',apellido_1,' ',apellido_2) AS nombre
            FROM cliente
            WHERE nit = ?
              AND estado = 'A'
        """;

        try (PreparedStatement ps = con.preparar(sql)) {

            ps.setString(1, nit);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new String[]{
                            rs.getString("nit"),
                            rs.getString("nombre")
                    };
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =========================================
       BUSCAR PRODUCTO
    ========================================= */
    public Object[] buscarProducto(int codigo) {

        String sql = """
        SELECT
            p.codigo,
            p.nombre,
            p.stock_actual,
            pr.precio_venta,

            ISNULL((
                SELECT SUM(valor)
                FROM asignacion_impuesto ai
                WHERE ai.codigo_producto = p.codigo
                AND ai.estado = 'A'
                AND GETDATE() BETWEEN ai.fecha_inicio AND ai.fecha_fin
            ),0) AS impuestos,

            ISNULL((
                SELECT SUM(valor)
                FROM asignacion_descuento ad
                WHERE ad.codigo_producto = p.codigo
                AND ad.estado = 'A'
                AND GETDATE() BETWEEN ad.fecha_inicio AND ad.fecha_fin
            ),0) AS descuentos

        FROM producto p
        LEFT JOIN precio pr
            ON pr.codigo_producto = p.codigo
           AND pr.estado = 'A'
           AND GETDATE() BETWEEN pr.inicio_vigencia AND pr.fin_vigencia

        WHERE p.estado = 'A'
        AND p.codigo = ?
        """;

        try (PreparedStatement ps = con.preparar(sql)) {

            ps.setInt(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    double precio = rs.getDouble("precio_venta");
                    double impuestos = rs.getDouble("impuestos");
                    double descuentos = rs.getDouble("descuentos");

                    double precioFinal = precio + impuestos - descuentos;

                    return new Object[]{
                            rs.getInt("codigo"),
                            rs.getString("nombre"),
                            rs.getInt("stock_actual"),
                            precio,
                            impuestos,
                            descuentos,
                            precioFinal
                    };
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =========================================
       LISTAR TIPO PLAZO
    ========================================= */
    public List<String[]> listarTipoPlazo() {

        List<String[]> lista = new ArrayList<>();

        lista.add(new String[]{"D", "Días"});
        lista.add(new String[]{"M", "Meses"});

        return lista;
    }
}
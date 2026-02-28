package sql;

public class SQL {
    private final String seleccionarClientePorNIT =  "SELECT * FROM cliente WHERE nit=?";
    private final String registrarVenta =  "{call dbo.sp_ingreso_venta(?,?,?,?,?,?,?,?,?,?,?)}";

    public String getSeleccionarClientePorNIT() {
        return seleccionarClientePorNIT;
    }

    public String getRegistrarVenta() {
        return registrarVenta;
    }
}


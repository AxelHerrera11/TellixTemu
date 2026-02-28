package sql;

public class SQL {
    private final String seleccionarClientePorNIT =  "SELECT * FROM cliente WHERE nit=?";

    public String getSeleccionarClientePorNIT() {
        return seleccionarClientePorNIT;
    }
}


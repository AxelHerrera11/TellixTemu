package sql;

import javax.swing.*;
import java.sql.*;

public class Conector {

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private final String HOST = "localhost";
    private final String PORT = "1433;instanceName=SQLEXPRESS";
    private final String DATABASE = "tellix";
    private final String usuario = "sa";
    private final String contrasena = "Umg2026";
    private final String URL;

    private Connection link;

    public Conector() {

        this.URL = "jdbc:sqlserver://" + HOST + ":" + PORT +
                ";databaseName=" + DATABASE +
                ";encrypt=false";
    }

    // Conectar
    public boolean conectar() {
        try {
            Class.forName(DRIVER);
            link = DriverManager.getConnection(URL, usuario, contrasena);
            System.out.println("Conectado a SQL Server como " + usuario);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    // Desconectar
    public void desconectar() {
        try {
            if (link != null) {
                link.close();
                System.out.println("Desconectado.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Preparar sentencia normal
    public PreparedStatement preparar(String sql) {
        try {
            return link.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Preparar sentencia con retorno de IDENTITY
    public PreparedStatement prepararConLlaves(String sql) {
        try {
            return link.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Connection getConexion() {
        return link;
    }

    public void mensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipoMensaje);
    }
}
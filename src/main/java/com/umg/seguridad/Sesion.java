package com.umg.seguridad;

import sql.Conector;

public class Sesion {
    private static Conector conexionActiva;
    private static String usuario;
    private static String rol;

    public static void iniciarSesion(Conector con, String user, String role) {
        conexionActiva = con;
        usuario = user;
        rol = role;
    }

    public static Conector getConexion() {
        return conexionActiva;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getRol() {
        return rol;
    }

    public static void cerrarSesion() {
        try {
            if (conexionActiva != null) {
                conexionActiva.desconectar();
                System.out.println("Conexión cerrada correctamente al cerrar sesión.");
                conexionActiva = null;
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar la sesión: " + e.getMessage());
        } finally {
            usuario = null;
            rol = null;
        }
    }
}

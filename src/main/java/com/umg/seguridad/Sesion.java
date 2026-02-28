package com.umg.seguridad;

import sql.Conector;

public class Sesion {

    private static Conector conexion;
    private static String usuario;

    public static void setConexion(Conector c) {
        conexion = c;
    }

    public static Conector getConexion() {
        return conexion;
    }

    public static void setUsuario(String u) {
        usuario = u;
    }

    public static String getUsuario() {
        return usuario;
    }
}

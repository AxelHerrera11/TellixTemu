package com.umg.seguridad;

import java.util.*;

public class PermisosPorNivel {

    private static final Map<Integer, List<String>> permisos = new HashMap<>();

    static {
        // Nivel 1: ADMIN (todo)
        permisos.put(1, Arrays.asList(
                "Inicio", "Clientes", "Ventas", "Productos", "Compras",
                "Cuentas Por Co/Pa", "Cuentas por Cobrar", "Cuentas por Pagar",
                "Usuarios y Roles", "Reportes", "Inventario", "Configuración",
                "Categorías", "Marcas", "Medidas", "Tipos de Cliente",
                "Tipos de Contacto", "Bancos", "Métodos de Liquidación"
        ));

        // Nivel 2: GERENTE
        permisos.put(2, Arrays.asList(
                "Inicio", "Clientes", "Ventas", "Productos", "Compras", "Inventario",
                "Cuentas Por Co/Pa", "Cuentas por Cobrar", "Cuentas por Pagar",
                "Reportes", "Configuración", "Categorías", "Marcas", "Métodos de Liquidación"
        ));

        // Nivel 3: VENDEDOR
        permisos.put(3, Arrays.asList(
                "Inicio", "Clientes", "Ventas", "Productos", "Inventario"
        ));

        // Nivel 4: AUXILIAR
        permisos.put(4, Arrays.asList(
                "Inicio", "Inventario", "Reportes",
                "Cuentas Por Co/Pa", "Cuentas por Cobrar", "Cuentas por Pagar"
        ));

        // Nivel 5: SOPORTE
        permisos.put(5, Arrays.asList(
                "Inicio", "Usuarios y Roles", "Configuración",
                "Bancos", "Métodos de Liquidación"
        ));
    }

    public static boolean tieneAcceso(int nivel, String modulo) {
        List<String> modulos = permisos.get(nivel);
        return modulos != null && modulos.contains(modulo);
    }
}

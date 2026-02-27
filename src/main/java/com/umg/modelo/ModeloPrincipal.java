package com.umg.modelo;

import com.umg.vistas.VistaPrincipal;

public class ModeloPrincipal {
    VistaPrincipal vista;

    public ModeloPrincipal() {
    }

    public ModeloPrincipal(VistaPrincipal vista) {
        this.vista = vista;
    }

    public VistaPrincipal getVista() {
        return vista;
    }

    public void setVista(VistaPrincipal vista) {
        this.vista = vista;
    }
}

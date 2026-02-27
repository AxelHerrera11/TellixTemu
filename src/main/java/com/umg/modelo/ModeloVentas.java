package com.umg.modelo;

import com.umg.vistas.VistaVentas;

public class ModeloVentas {
    VistaVentas vista;

    public ModeloVentas(VistaVentas vista) {
        this.vista = vista;
    }

    public VistaVentas getVista() {
        return vista;
    }

    public void setVista(VistaVentas vista) {
        this.vista = vista;
    }
}

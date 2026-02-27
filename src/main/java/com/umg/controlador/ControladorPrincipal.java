package com.umg.controlador;

import com.umg.modelo.ModeloPrincipal;
import com.umg.vistas.VistaVentas;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ControladorPrincipal implements WindowListener {
    ModeloPrincipal modelo;

    VistaVentas vista = new VistaVentas();

    public ControladorPrincipal(ModeloPrincipal modelo) {
        this.modelo = modelo;
    }

    public void cargarVista(JPanel panel) {
        var vista = modelo.getVista();
        vista.contenedor.removeAll();
        vista.contenedor.add(panel);
        vista.contenedor.revalidate();
        vista.contenedor.repaint();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        cargarVista(vista);
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}

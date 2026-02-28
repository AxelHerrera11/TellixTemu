package com.umg.controlador;

import com.umg.modelo.*;
import com.umg.implementacion.VentaImp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.HashMap;
import java.util.Map;


public class ControladorVentas implements ActionListener, MouseListener {
    ModeloVentas modelo;
    private VentaImp venta = new VentaImp();

    private JPanel btnNuevo, btnActualizar, btnEliminar, btnBuscar, btnLimpiar;
    private JLabel lblNuevo, lblActualizar, lblEliminar, lblBuscar, lblLimpiar;

    private JComboBox<String> cmbMetodoDePago;
    private JComboBox<String> cmbTipoPlazo;

    private Map<JPanel, String> iconosBotones = new HashMap<>();

    public ControladorVentas(ModeloVentas modelo) {
        this.modelo = modelo;

        var vista = modelo.getVista();
        // Inicializar botones y labels
        btnNuevo      = vista.btnNuevo;
        btnActualizar = vista.btnInsertar;
        btnEliminar   = vista.btnEliminar;
        btnBuscar     = vista.btnBuscarProducto;
        btnLimpiar    = vista.btnBuscarCliente;

        lblNuevo      = vista.lblNuevo;
        lblActualizar = vista.lblActualizar;
        lblEliminar   = vista.lblEliminar;
        lblBuscar     = vista.lblBuscarProducto;
        lblLimpiar    = vista.lblBuscarCliente;

        // Dar nombre a los labels para manejar iconos
        lblNuevo.setName("icono");
        lblActualizar.setName("icono");
        lblEliminar.setName("icono");
        lblBuscar.setName("icono");
        lblLimpiar.setName("icono");

        inicializarIconos();
        configurarTabla();

        cmbMetodoDePago = vista.cmbMetodoDePago;
        cmbTipoPlazo    = vista.cmbTipoPlazo;

        cargarMetodosDePagoEnCombo();
        cargarTiposPlazoEnCombo();
    }

    private void cargarMetodosDePagoEnCombo() {
        for (String[] fila : venta.listarMetodosPago()) {
            cmbMetodoDePago.addItem(fila[1]); // descripcion
        }
    }

    private void cargarTiposPlazoEnCombo() {
        for (String[] fila : venta.listarTipoPlazo()) {
            cmbTipoPlazo.addItem(fila[1]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent().equals(modelo.getVista().btnBuscarCliente)) {
            traerCliente();
        } else if (e.getComponent().equals(modelo.getVista().btnNuevo)) {
            agregarProducto();
        } else if (e.getComponent().equals(modelo.getVista().btnBuscarProducto)) {
            traerProducto();
        } else if (e.getComponent().equals(modelo.getVista().btnEliminar)){
        } else if(e.getComponent().equals(modelo.getVista().btnInsertar)){
            agregarVenta();
        } else if (e.getComponent().equals(modelo.getVista().btnNuevo)) {

        }
    }

    @Override public void mousePressed(MouseEvent e) { }

    @Override public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) {
        cambiarIconoBoton((JPanel) e.getSource(), true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        cambiarIconoBoton((JPanel) e.getSource(), false);
    }

    private void inicializarIconos() {
        iconosBotones.put(btnNuevo, "/com/umg/iconos/IconoBoton1.png");
        iconosBotones.put(btnActualizar, "/com/umg/iconos/IconoBoton1.png");
        iconosBotones.put(btnEliminar, "/com/umg/iconos/IconoBoton1.png");
        iconosBotones.put(btnBuscar, "/com/umg/iconos/IconoBoton1.png");
        iconosBotones.put(btnLimpiar, "/com/umg/iconos/IconoBoton1.png");
    }

    private void cambiarIconoBoton(JPanel boton, boolean activo) {
        JLabel icono = obtenerLabelPorNombre(boton, "icono");
        String rutaBase = iconosBotones.get(boton);
        if (rutaBase != null && icono != null) {
            String rutaFinal = activo ? rutaBase.replace(".png", "_oscuro.png") : rutaBase;
            icono.setIcon(new ImageIcon(getClass().getResource(rutaFinal)));
        }
    }

    private JLabel obtenerLabelPorNombre(JPanel boton, String nombre) {
        for (Component comp : boton.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;
                if (nombre.equals(lbl.getName())) return lbl;
            }
        }
        return null;
    }

    public void configurarTabla() {

        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",           // 0 ← OCULTO
                        "Producto",     // 1
                        "Precio bruto", // 2
                        "Descuentos",   // 3
                        "Impuestos",    // 4
                        "Cantidad",     // 5
                        "Subtotal"      // 6
                }, 0
        );

        modelo.getVista().tblProductos.setModel(modeloTabla);

        // Ocultar columna ID
        modelo.getVista().tblProductos.getColumnModel().getColumn(0).setMinWidth(0);
        modelo.getVista().tblProductos.getColumnModel().getColumn(0).setMaxWidth(0);
        modelo.getVista().tblProductos.getColumnModel().getColumn(0).setWidth(0);
    }

    public void traerCliente() {
        String[] cliente = venta.buscarCliente(modelo.getVista().txtNITCliente.getText());

        if(cliente != null){
            modelo.getVista().txtNombreCliente.setText(cliente[1]);
        }
    }

    public void traerProducto() {
        Object[] prod = venta.buscarProducto(
                Integer.parseInt(modelo.getVista().txtBuscarProducto.getText())
        );

        if(prod != null){
            modelo.getVista().txtNombreProducto.setText(prod[1].toString());
            modelo.getVista().txtStockDisponible.setText(prod[2].toString());
            modelo.getVista().txtPrecioProducto.setText(prod[3].toString());
        }
    }

    public void limpiarTodo(){
        modelo.getVista().txtCantidadProducto.setText("");
        modelo.getVista().txtBuscarProducto.setText("");
        modelo.getVista().txtNombreProducto.setText("");
        modelo.getVista().txtStockDisponible.setText("");
        modelo.getVista().txtPrecioProducto.setText("");
        modelo.getVista().tblProductos.setModel(new DefaultTableModel());
        configurarTabla();
        modelo.getVista().txtBuscarCliente.setText("");
        modelo.getVista().txtNITCliente.setText("");
        modelo.getVista().txtNombreCliente.setText("");
        modelo.getVista().txtPlazoCredito.setText("");
        modelo.getVista().cmbMetodoDePago.setSelectedItem(0);
        modelo.getVista().cmbTipoPlazo.setSelectedItem(0);
        modelo.getVista().txtTotalVenta.setText("");
        modelo.getVista().txtNumeroCuenta.setText("");
    }

    public void agregarProducto() {

        DefaultTableModel tabla =
                (DefaultTableModel) modelo.getVista().tblProductos.getModel();

        int codigo = Integer.parseInt(modelo.getVista().txtBuscarProducto.getText());
        String nombre = modelo.getVista().txtNombreProducto.getText();
        double precio = Double.parseDouble(modelo.getVista().txtPrecioProducto.getText());
        int cantidad = Integer.parseInt(modelo.getVista().txtCantidadProducto.getText());

        double descuentos = 0;
        double impuestos = precio * 0.12; // ejemplo IVA
        double subtotal = (precio + impuestos - descuentos) * cantidad;

        tabla.addRow(new Object[]{
                codigo,      // ← ID oculto
                nombre,
                precio,
                descuentos,
                impuestos,
                cantidad,
                subtotal
        });

        recalcularTotal();
    }

    private void recalcularTotal() {
        DefaultTableModel tabla = (DefaultTableModel) modelo.getVista().tblProductos.getModel();

        double total = 0;

        for (int i = 0; i < tabla.getRowCount(); i++) {
            total += Double.parseDouble(tabla.getValueAt(i, 6).toString());
        }

        modelo.getVista().txtTotalVenta.setText(String.valueOf(total));
    }

    public void agregarVenta() {

        if(modelo.getVista().txtNombreCliente.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"Debe seleccionar un cliente");
            return;
        }

        if(modelo.getVista().tblProductos.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Debe agregar productos");
            return;
        }

        ModeloVentaDB ventaModel = new ModeloVentaDB();

        // ===== Datos generales =====
        ventaModel.setCliente(modelo.getVista().txtNITCliente.getText());

        // Usuario logueado desde sesión
        ventaModel.setUsuarioSistema(com.umg.seguridad.Sesion.getUsuario());

        // IMPORTANTE: aquí deberías usar el ID real del combo
        ventaModel.setMetodoPago(cmbMetodoDePago.getSelectedIndex());

        ventaModel.setPlazoCredito(
                modelo.getVista().txtPlazoCredito.getText().isEmpty()
                        ? 0
                        : Integer.parseInt(modelo.getVista().txtPlazoCredito.getText())
        );

        ventaModel.setTipoPlazo(
                cmbTipoPlazo.getSelectedItem() != null
                        ? cmbTipoPlazo.getSelectedItem().toString()
                        : null
        );

        ventaModel.setNumeroCuenta(modelo.getVista().txtNumeroCuenta.getText().isEmpty()
                ? null
                : modelo.getVista().txtNumeroCuenta.getText());

        ventaModel.setTotalVenta(
                Double.parseDouble(modelo.getVista().txtTotalVenta.getText())
        );

        // Fecha límite (si manejas crédito)
        ventaModel.setFechaLimite(null); // puedes calcularla si tienes lógica

        // ===== Detalles =====
        DefaultTableModel tabla =
                (DefaultTableModel) modelo.getVista().tblProductos.getModel();

        java.util.List<ModeloDetalleVenta> detalles = new java.util.ArrayList<>();

        for(int i=0;i<tabla.getRowCount();i++){

            ModeloDetalleVenta det = new ModeloDetalleVenta(
                    Integer.parseInt(tabla.getValueAt(i,0).toString()), // codigoProducto
                    Integer.parseInt(tabla.getValueAt(i,5).toString()), // cantidad
                    Double.parseDouble(tabla.getValueAt(i,2).toString()), // precioBruto
                    Double.parseDouble(tabla.getValueAt(i,3).toString()), // descuentos
                    Double.parseDouble(tabla.getValueAt(i,4).toString())  // impuestos
            );

            detalles.add(det);
        }

        ventaModel.setDetalles(detalles);

        // ===== Guardar =====
        int idVenta = venta.registrarVenta(ventaModel);

        if(idVenta > 0){
            JOptionPane.showMessageDialog(null,"Venta registrada. No. "+idVenta);
            limpiarTodo();
        }else{
            JOptionPane.showMessageDialog(null,"Error al registrar venta");
        }
    }
}
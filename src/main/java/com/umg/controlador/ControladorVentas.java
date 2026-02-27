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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class ControladorVentas implements ActionListener, MouseListener {
    ModeloVentas modelo;


    private JPanel btnNuevo, btnActualizar, btnEliminar, btnBuscar, btnLimpiar;
    private JLabel lblNuevo, lblActualizar, lblEliminar, lblBuscar, lblLimpiar;
    private VentaImp venta = new VentaImp();

    private ModeloVentaDB ventaDB = new ModeloVentaDB();
    private List<ModeloDetalleVentaDB> detalleVentaDB = new ArrayList();

    private JComboBox<String> cmbMetodoDePago;
    private JComboBox<String> cmbTipoPlazo;

    private int[] arrCodMetodosPago;

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
        cargarMetodosPagoEnCombo();

        cmbMetodoDePago = vista.cmbMetodoDePago;
        cmbTipoPlazo    = vista.cmbTipoPlazo;

        cargarMetodosDePagoEnCombo();
        cargarTiposPlazoEnCombo();
    }

    private void cargarMetodosDePagoEnCombo() {
//        if (cmbMetodoDePago == null) return;
//
//        cmbMetodoDePago.removeAllItems();
//        cmbMetodoDePago.addItem("-- Seleccione --");
//
//        try {
//            var lista = svcMetodos.listarOrdenadoPor("DESCRIPCION");
//            for (IMetodosDeLiquidacion.RowMetodo r : lista) {
//                // Mostramos solo la DESCRIPCIÓN (nombre bonito del método)
//                cmbMetodoDePago.addItem(r.descripcion);
//            }
//        } catch (Exception e) {
//            System.out.println("cargarMetodosDePagoEnCombo (Ventas): " + e.getMessage());
//        }
    }

    private void cargarTiposPlazoEnCombo() {
        if (cmbTipoPlazo == null) return;

        cmbTipoPlazo.removeAllItems();
        cmbTipoPlazo.addItem("-- Seleccione --");
        cmbTipoPlazo.addItem("Día");
        cmbTipoPlazo.addItem("Mes");
        cmbTipoPlazo.addItem("Año");
    }

    // Devuelve "D", "M" o "A" según lo seleccionado en cmbTipoPlazo
    private String obtenerCodigoTipoPlazoSeleccionado() {
        if (cmbTipoPlazo == null) return null;

        Object sel = cmbTipoPlazo.getSelectedItem();
        if (sel == null) return null;

        String texto = sel.toString().trim();

        if (texto.equalsIgnoreCase("Día") || texto.equalsIgnoreCase("Dia")) {
            return "D";
        } else if (texto.equalsIgnoreCase("Mes")) {
            return "M";
        } else if (texto.equalsIgnoreCase("Año") || texto.equalsIgnoreCase("Ano")) {
            return "A";
        }

        // "-- Seleccione --" u otra cosa
        return null;
    }

    private void registrarComboBox() {
        var vista = modelo.getVista();

        // 1) Tipo de plazo en formato D / M / A
        String tipoPlazo = obtenerCodigoTipoPlazoSeleccionado();
        if (tipoPlazo == null) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Seleccione un Tipo de Plazo (Día, Mes o Año).",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2) Plazo de crédito numérico (opcional)
        String plazoTxt = vista.txtPlazoCredito.getText().trim();
        Integer plazoCredito = null;
        if (!plazoTxt.isEmpty()) {
            try {
                plazoCredito = Integer.parseInt(plazoTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        vista,
                        "El Plazo de Crédito debe ser numérico.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        ventaDB.setTipoPlazo(tipoPlazo);       // <-- AQUÍ QUEDA SOLO D / M / ¿¿¿A
        ventaDB.setPlazoCredito(plazoCredito); // si tu modelo lo tiene

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
        } else if (e.getComponent().equals(modelo.getVista().btnEliminar)) {

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

    public void traerProducto() {
//        if (!modelo.getVista().txtBuscarProducto.getText().equals("")) {
//            int valor = Integer.parseInt(modelo.getVista().txtBuscarProducto.getText());
//            resumProd = venta.seleccionarProducto(valor);
//            modelo.getVista().txtPrecioProducto.setText(String.valueOf(resumProd.getPrecioFinal()));
//            modelo.getVista().txtNombreProducto.setText(resumProd.getNombre());
//            modelo.getVista().txtStockDisponible.setText(String.valueOf(resumProd.getStockDisponible()));
//        }
    }

    public void configurarTabla() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[]{
                        "Producto",
                        "Precio base",
                        "Impuestos",
                        "Descuentos",
                        "Precio final",
                        "Cantidad",
                        "Subtotal"
                }, 0
        );
        modelo.getVista().tblProductos.setModel(modeloTabla);
    }

    public void agregarProducto() {
//        String txtCant = modelo.getVista().txtCantidadProducto.getText().trim();
//
//        if (txtCant.isEmpty()) {
//            JOptionPane.showMessageDialog(
//                    modelo.getVista(),
//                    "Debe ingresar una cantidad.",
//                    "Validación",
//                    JOptionPane.WARNING_MESSAGE
//            );
//            return;
//        }
//
//        int cantidad;
//        try {
//            cantidad = Integer.parseInt(txtCant);
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(
//                    modelo.getVista(),
//                    "La cantidad debe ser numérica.",
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//            return;
//        }
//
//        if (resumProd == null) {
//            JOptionPane.showMessageDialog(
//                    modelo.getVista(),
//                    "No hay producto seleccionado.",
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//            return;
//        }
//
//        float precioFinal = resumProd.getPrecioFinal();
//        float subtotal = precioFinal * cantidad;
//
//        DefaultTableModel dtm = (DefaultTableModel) modelo.getVista().tblProductos.getModel();
//
//        Object[] fila = new Object[]{
//                resumProd.getNombre(),          // Producto
//                resumProd.getPrecioBase(),      // Precio base
//                resumProd.getTotalImpuestos(),  // Impuestos
//                resumProd.getTotalDescuentos(), // Descuentos
//                resumProd.getPrecioFinal(),     // Precio final
//                cantidad,                       // Cantidad
//                subtotal                        // Subtotal
//        };
//
//        dtm.addRow(fila);
//
//        float totalVenta = 0f;
//        for (int i = 0; i < dtm.getRowCount(); i++) {
//            Object valor = dtm.getValueAt(i, 6); // columna "Subtotal"
//            if (valor instanceof Number) {
//                totalVenta += ((Number) valor).floatValue();
//            } else if (valor != null) {
//                try {
//                    totalVenta += Float.parseFloat(valor.toString());
//                } catch (NumberFormatException ex) {
//                }
//            }
//        }
//
//        ModeloDetalleVentaDB detalle = new ModeloDetalleVentaDB();
//        detalle.setCodigo_producto(resumProd.getCodigo());
//        detalle.setDescuentos(resumProd.getTotalDescuentos());
//        detalle.setImpuestos(resumProd.getTotalImpuestos());
//        detalle.setPrecio_bruto(resumProd.getPrecioBase());
//        detalle.setCantidad(cantidad);
//        agregarDetalle(detalle);
//
//        modelo.getVista().txtTotalVenta.setText(String.valueOf(totalVenta));
//
//        modelo.getVista().txtCantidadProducto.setText("");
//        modelo.getVista().txtBuscarProducto.setText("");
//        modelo.getVista().txtNombreProducto.setText("");
//        modelo.getVista().txtStockDisponible.setText("");
//        modelo.getVista().txtPrecioProducto.setText("");
//        resumProd = null;
    }

    public void traerCliente() {
//        if (!modelo.getVista().txtBuscarCliente.getText().equals("")) {
//            String nit = modelo.getVista().txtBuscarCliente.getText().trim();
//            clienteRes = venta.seleccionarCliente(nit);
//
//            if (clienteRes != null) {
//                modelo.getVista().txtNITCliente.setText(clienteRes.getNit());
//                modelo.getVista().txtNombreCliente.setText(clienteRes.getNombre());
//            } else {
//                JOptionPane.showMessageDialog(
//                        modelo.getVista(),
//                        "No se encontró ningún cliente con el NIT: " + nit,
//                        "Cliente no encontrado",
//                        JOptionPane.WARNING_MESSAGE
//                );
//
//                modelo.getVista().txtNITCliente.setText("");
//                modelo.getVista().txtNombreCliente.setText("");
//            }
//        } else {
//            JOptionPane.showMessageDialog(
//                    modelo.getVista(),
//                    "Debe ingresar un NIT para buscar.",
//                    "Validación",
//                    JOptionPane.INFORMATION_MESSAGE
//            );
//        }
    }

    public void agregarDetalle(ModeloDetalleVentaDB modeloDetalle){
        detalleVentaDB.add(modeloDetalle);
    }

    public void agregarVenta(){
    }

    public boolean validarTodo(){
        return false;
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
        ventaDB = null;
        detalleVentaDB = null;
    }

    public void cargarMetodosPagoEnCombo(){
        var vista = modelo.getVista();
        JComboBox<String> cmbMetodo = vista.cmbMetodoDePago;

        cmbMetodo.removeAllItems();
        cmbMetodo.addItem("-- Seleccione --");

//        listaMetodosPago = venta.seleccionarMetodosPago();
//        arrCodMetodosPago = new int[listaMetodosPago.size()];

//        int i = 0;
//        for (ModeloMetodoPagoDB mp : listaMetodosPago) {
//            cmbMetodo.addItem(mp.getMetodo_pago());
//            arrCodMetodosPago[i] = mp.getCodigo();
//            i++;
//        }
    }
}
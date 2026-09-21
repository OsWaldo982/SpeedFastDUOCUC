package cl.speedfast.vista;

import cl.speedfast.model.Pedido;
import cl.speedfast.service.ControladorDeEnvios;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/** Ventana que muestra los pedidos registrados mediante JTable. */
public class VentanaListaPedidos extends JFrame {

    private final ControladorDeEnvios controlador;
    private final DefaultTableModel modeloTabla;

    public VentanaListaPedidos(ControladorDeEnvios controlador) {
        this.controlador = controlador;

        setTitle("SpeedFast - Lista de Pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        String[] columnas = {
                "ID", "Tipo", "Direccion", "Estado", "Repartidor"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modeloTabla);
        tabla.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scroll, BorderLayout.CENTER);

        JPanel acciones = new JPanel();
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnCerrar = new JButton("Cerrar");
        acciones.add(btnRefrescar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> refrescarTabla());
        btnCerrar.addActionListener(e -> dispose());

        refrescarTabla();
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);

        for (Pedido pedido : controlador.getPedidos()) {
            modeloTabla.addRow(new Object[]{
                    pedido.getIdPedido(),
                    pedido.getClass().getSimpleName(),
                    pedido.getDireccionEntrega(),
                    pedido.getEstado(),
                    pedido.getRepartidorAsignado()
            });
        }
    }
}

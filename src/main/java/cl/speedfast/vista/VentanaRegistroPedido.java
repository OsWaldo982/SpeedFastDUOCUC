package cl.speedfast.vista;

import cl.speedfast.model.Pedido;
import cl.speedfast.model.PedidoComida;
import cl.speedfast.model.PedidoEncomienda;
import cl.speedfast.model.PedidoExpress;
import cl.speedfast.service.ControladorDeEnvios;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/** Formulario para registrar pedidos en memoria. */
public class VentanaRegistroPedido extends JFrame {

    private final ControladorDeEnvios controlador;
    private final Runnable alGuardar;

    private final JTextField txtId = new JTextField();
    private final JTextField txtDireccion = new JTextField();
    private final JComboBox<String> cmbTipo = new JComboBox<>(
            new String[]{"Comida", "Encomienda", "Express"}
    );

    public VentanaRegistroPedido(ControladorDeEnvios controlador, Runnable alGuardar) {
        this.controlador = controlador;
        this.alGuardar = alGuardar;

        setTitle("SpeedFast - Registrar Pedido");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 260);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(3, 2, 10, 10));
        formulario.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);
        formulario.add(new JLabel("Direccion:"));
        formulario.add(txtDireccion);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(cmbTipo);
        add(formulario, BorderLayout.CENTER);

        JPanel acciones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        acciones.add(btnGuardar);
        acciones.add(btnCancelar);
        add(acciones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarPedido());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarPedido() {
        String idTexto = txtId.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (idTexto.isEmpty() || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "ID y direccion son obligatorios.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        final int id;
        try {
            id = Integer.parseInt(idTexto);
            if (id <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "El ID debe ser un numero entero mayor que 0.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String tipo = (String) cmbTipo.getSelectedItem();
        Pedido pedido = crearPedido(id, direccion, tipo);

        try {
            controlador.registrarPedido(pedido);
            JOptionPane.showMessageDialog(
                    this,
                    "Pedido #" + id + " registrado correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (alGuardar != null) {
                alGuardar.run();
            }

            limpiarFormulario();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "No se pudo registrar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Pedido crearPedido(int id, String direccion, String tipo) {
        return switch (tipo) {
            case "Comida" -> new PedidoComida(id, direccion);
            case "Encomienda" -> new PedidoEncomienda(id, direccion);
            case "Express" -> new PedidoExpress(id, direccion);
            default -> throw new IllegalArgumentException("Tipo de pedido no valido.");
        };
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        txtId.requestFocus();
    }
}

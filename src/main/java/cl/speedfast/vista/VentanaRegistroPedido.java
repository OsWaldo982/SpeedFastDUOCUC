package cl.speedfast.vista;

import cl.speedfast.controlador.ControladorDeEnvios;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.PedidoComida;
import cl.speedfast.modelo.PedidoEncomienda;
import cl.speedfast.modelo.PedidoExpress;

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

/**
 * Ventana para registrar pedidos.
 * Los pedidos se guardan en MySQL mediante el controlador.
 */
public class VentanaRegistroPedido extends JFrame {

    private final ControladorDeEnvios controlador;
    private final Runnable alGuardar;

    private final JTextField txtDireccion = new JTextField();

    private final JComboBox<String> cmbTipo = new JComboBox<>(
            new String[]{
                    "Comida",
                    "Encomienda",
                    "Express"
            }
    );

    public VentanaRegistroPedido(
            ControladorDeEnvios controlador,
            Runnable alGuardar
    ) {

        this.controlador = controlador;
        this.alGuardar = alGuardar;

        setTitle("SpeedFast - Registrar Pedido");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 230);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        JPanel formulario =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                10,
                                10
                        )
                );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        25,
                        10,
                        25
                )
        );

        formulario.add(
                new JLabel("Direccion:")
        );

        formulario.add(
                txtDireccion
        );

        formulario.add(
                new JLabel("Tipo:")
        );

        formulario.add(
                cmbTipo
        );

        add(
                formulario,
                BorderLayout.CENTER
        );


        JPanel acciones =
                new JPanel();

        JButton btnGuardar =
                new JButton("Guardar");

        JButton btnCancelar =
                new JButton("Cancelar");

        acciones.add(
                btnGuardar
        );

        acciones.add(
                btnCancelar
        );

        add(
                acciones,
                BorderLayout.SOUTH
        );


        btnGuardar.addActionListener(
                e -> guardarPedido()
        );

        btnCancelar.addActionListener(
                e -> dispose()
        );
    }


    private void guardarPedido() {

        String direccion =
                txtDireccion
                        .getText()
                        .trim();

        if (direccion.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La direccion es obligatoria.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String tipo =
                (String) cmbTipo
                        .getSelectedItem();

        Pedido pedido =
                crearPedido(
                        direccion,
                        tipo
                );


        try {

            controlador.registrarPedido(
                    pedido
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Pedido #"
                            + pedido.getId()
                            + " registrado correctamente en MySQL.",
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


    private Pedido crearPedido(
            String direccion,
            String tipo
    ) {

        return switch (tipo) {

            case "Comida" ->
                    new PedidoComida(
                            0,
                            direccion
                    );

            case "Encomienda" ->
                    new PedidoEncomienda(
                            0,
                            direccion
                    );

            case "Express" ->
                    new PedidoExpress(
                            0,
                            direccion
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Tipo de pedido no valido."
                    );
        };
    }


    private void limpiarFormulario() {

        txtDireccion.setText("");

        cmbTipo.setSelectedIndex(
                0
        );

        txtDireccion.requestFocus();
    }
}
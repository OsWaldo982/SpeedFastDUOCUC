package cl.speedfast.vista;

import cl.speedfast.service.ControladorDeEnvios;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Ventana principal de SpeedFast.
 * Desde aqui se navega al registro, listado e inicio de entregas.
 */
public class VentanaPrincipal extends JFrame {

    private final ControladorDeEnvios controlador;
    private final JLabel lblEstado;
    private final JButton btnIniciarEntrega;

    public VentanaPrincipal() {
        this.controlador = new ControladorDeEnvios();

        setTitle("SpeedFast - Gestion de Entregas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 330);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        JLabel titulo = new JLabel("SPEEDFAST - GESTION DE ENTREGAS", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));
        add(titulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 70, 10, 70));

        JButton btnRegistrar = new JButton("Registrar pedido");
        JButton btnListar = new JButton("Listar pedidos");
        btnIniciarEntrega = new JButton("Asignar repartidor / Iniciar entrega");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnListar);
        panelBotones.add(btnIniciarEntrega);
        add(panelBotones, BorderLayout.CENTER);

        lblEstado = new JLabel("Pedidos registrados: 0", SwingConstants.CENTER);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));
        add(lblEstado, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> abrirRegistro());
        btnListar.addActionListener(e -> abrirListado());
        btnIniciarEntrega.addActionListener(e -> iniciarEntregas());
    }

    private void abrirRegistro() {
        VentanaRegistroPedido ventana = new VentanaRegistroPedido(controlador, this::actualizarEstado);
        ventana.setVisible(true);
    }

    private void abrirListado() {
        new VentanaListaPedidos(controlador).setVisible(true);
    }

    private void actualizarEstado() {
        lblEstado.setText("Pedidos registrados: " + controlador.cantidadPedidos());
    }

    private void iniciarEntregas() {
        if (controlador.cantidadPedidos() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes registrar al menos un pedido.",
                    "Sin pedidos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        btnIniciarEntrega.setEnabled(false);
        lblEstado.setText("Entregas en proceso...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return controlador.iniciarEntregas();
            }

            @Override
            protected void done() {
                btnIniciarEntrega.setEnabled(true);
                try {
                    boolean correcto = get();
                    if (correcto) {
                        lblEstado.setText("Todos los pedidos fueron entregados.");
                        JOptionPane.showMessageDialog(
                                VentanaPrincipal.this,
                                "Todos los pedidos han sido entregados correctamente.",
                                "Proceso finalizado",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        lblEstado.setText("No hay pedidos pendientes o el proceso no finalizo correctamente.");
                        JOptionPane.showMessageDialog(
                                VentanaPrincipal.this,
                                "No hay pedidos pendientes o no fue posible completar todas las entregas.",
                                "Aviso",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    lblEstado.setText("Error al ejecutar entregas.");
                    JOptionPane.showMessageDialog(
                            VentanaPrincipal.this,
                            "Ocurrio un error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }
}

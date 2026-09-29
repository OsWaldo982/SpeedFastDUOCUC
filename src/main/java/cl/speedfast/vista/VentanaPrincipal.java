package cl.speedfast.vista;

import cl.speedfast.controlador.ControladorDeEnvios;

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
 *
 * Permite registrar pedidos, consultar pedidos almacenados
 * en MySQL, registrar repartidores e iniciar entregas.
 */
public class VentanaPrincipal extends JFrame {

    private final ControladorDeEnvios controlador;

    private final JLabel lblEstado;

    private final JButton btnIniciarEntrega;

    public VentanaPrincipal() {

        this.controlador =
                new ControladorDeEnvios();

        setTitle(
                "SpeedFast - Gestion de Entregas"
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(
                540,
                390
        );

        setLocationRelativeTo(
                null
        );

        setResizable(
                false
        );

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );


        // ============================================
        // TITULO
        // ============================================

        JLabel titulo =
                new JLabel(
                        "SPEEDFAST - GESTION DE ENTREGAS",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );

        titulo.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        10,
                        5,
                        10
                )
        );

        add(
                titulo,
                BorderLayout.NORTH
        );


        // ============================================
        // BOTONES PRINCIPALES
        // ============================================

        JPanel panelBotones =
                new JPanel(
                        new GridLayout(
                                4,
                                1,
                                10,
                                10
                        )
                );

        panelBotones.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        70,
                        10,
                        70
                )
        );


        JButton btnRegistrar =
                new JButton(
                        "Registrar pedido"
                );

        JButton btnListar =
                new JButton(
                        "Listar pedidos"
                );

        JButton btnRegistrarRepartidor =
                new JButton(
                        "Registrar repartidor"
                );

        btnIniciarEntrega =
                new JButton(
                        "Asignar repartidor / Iniciar entrega"
                );


        panelBotones.add(
                btnRegistrar
        );

        panelBotones.add(
                btnListar
        );

        panelBotones.add(
                btnRegistrarRepartidor
        );

        panelBotones.add(
                btnIniciarEntrega
        );


        add(
                panelBotones,
                BorderLayout.CENTER
        );


        // ============================================
        // ESTADO
        // ============================================

        lblEstado =
                new JLabel(
                        "SpeedFast conectado a MySQL",
                        SwingConstants.CENTER
                );

        lblEstado.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        10,
                        20,
                        10
                )
        );

        add(
                lblEstado,
                BorderLayout.SOUTH
        );


        // ============================================
        // EVENTOS
        // ============================================

        btnRegistrar.addActionListener(
                e -> abrirRegistro()
        );

        btnListar.addActionListener(
                e -> abrirListado()
        );

        btnRegistrarRepartidor.addActionListener(
                e -> abrirRegistroRepartidor()
        );

        btnIniciarEntrega.addActionListener(
                e -> iniciarEntregas()
        );
    }


    // ============================================
    // REGISTRAR PEDIDO
    // ============================================

    private void abrirRegistro() {

        VentanaRegistroPedido ventana =
                new VentanaRegistroPedido(
                        controlador,
                        this::actualizarEstado
                );

        ventana.setVisible(
                true
        );
    }


    // ============================================
    // LISTAR PEDIDOS DESDE MYSQL
    // ============================================

    private void abrirListado() {

        new VentanaListaPedidos()
                .setVisible(true);
    }


    // ============================================
    // REGISTRAR REPARTIDOR
    // ============================================

    private void abrirRegistroRepartidor() {

        new VentanaRegistroRepartidor()
                .setVisible(true);
    }


    // ============================================
    // ACTUALIZAR ESTADO
    // ============================================

    private void actualizarEstado() {

        lblEstado.setText(
                "Pedidos registrados en esta sesion: "
                        + controlador.cantidadPedidos()
        );
    }


    // ============================================
    // INICIAR ENTREGAS
    // ============================================

    private void iniciarEntregas() {

        if (controlador.cantidadPedidos() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes registrar al menos un pedido en esta sesion.",
                    "Sin pedidos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        btnIniciarEntrega.setEnabled(
                false
        );

        lblEstado.setText(
                "Entregas en proceso..."
        );


        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Boolean doInBackground() {

                        return controlador
                                .iniciarEntregas();
                    }


                    @Override
                    protected void done() {

                        btnIniciarEntrega.setEnabled(
                                true
                        );

                        try {

                            boolean correcto =
                                    get();

                            if (correcto) {

                                lblEstado.setText(
                                        "Todos los pedidos fueron entregados."
                                );

                                JOptionPane.showMessageDialog(
                                        VentanaPrincipal.this,
                                        "Todos los pedidos han sido entregados correctamente.",
                                        "Proceso finalizado",
                                        JOptionPane.INFORMATION_MESSAGE
                                );

                            } else {

                                lblEstado.setText(
                                        "No hay pedidos pendientes o el proceso no finalizo correctamente."
                                );

                                JOptionPane.showMessageDialog(
                                        VentanaPrincipal.this,
                                        "No hay pedidos pendientes o no fue posible completar todas las entregas.",
                                        "Aviso",
                                        JOptionPane.WARNING_MESSAGE
                                );
                            }

                        } catch (Exception ex) {

                            lblEstado.setText(
                                    "Error al ejecutar entregas."
                            );

                            JOptionPane.showMessageDialog(
                                    VentanaPrincipal.this,
                                    "Ocurrio un error: "
                                            + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };


        worker.execute();
    }
}
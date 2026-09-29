package cl.speedfast.vista;

import cl.speedfast.dao.RepartidorDAO;
import cl.speedfast.modelo.Repartidor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Ventana para registrar repartidores directamente en MySQL.
 */
public class VentanaRegistroRepartidor extends JFrame {

    private final RepartidorDAO repartidorDAO;
    private final JTextField txtNombre;

    public VentanaRegistroRepartidor() {

        this.repartidorDAO = new RepartidorDAO();
        this.txtNombre = new JTextField();

        setTitle("SpeedFast - Registrar Repartidor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(430, 200);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        JPanel formulario =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                10,
                                10
                        )
                );

        formulario.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        25,
                        10,
                        25
                )
        );

        formulario.add(
                new JLabel("Nombre:")
        );

        formulario.add(
                txtNombre
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
                e -> guardarRepartidor()
        );

        btnCancelar.addActionListener(
                e -> dispose()
        );
    }

    private void guardarRepartidor() {

        String nombre =
                txtNombre.getText().trim();

        if (nombre.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El nombre del repartidor es obligatorio.",
                    "Validacion",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            Repartidor repartidor =
                    new Repartidor(
                            0,
                            nombre
                    );

            boolean guardado =
                    repartidorDAO.guardar(
                            repartidor
                    );

            if (guardado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Repartidor #"
                                + repartidor.getId()
                                + " registrado correctamente en MySQL.",
                        "Registro exitoso",
                        JOptionPane.INFORMATION_MESSAGE
                );

                txtNombre.setText("");
                txtNombre.requestFocus();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No fue posible guardar el repartidor.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validacion",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
package cl.speedfast.main;

import cl.speedfast.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new VentanaPrincipal().setVisible(true)
        );
    }
}
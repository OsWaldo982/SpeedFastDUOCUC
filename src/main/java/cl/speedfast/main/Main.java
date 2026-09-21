package cl.speedfast.main;

import cl.speedfast.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de SpeedFast Semana 6.
 * SwingUtilities.invokeLater ejecuta la interfaz en el hilo de eventos de Swing.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}

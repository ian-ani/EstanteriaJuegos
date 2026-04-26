package utiles;

import javax.swing.*;

public final class Mensaje {
    public static void mostrarMensajeInfo(String titulo, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarMensajePeligro(String titulo, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                titulo,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void mostrarMensajeError(String titulo, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                titulo,
                JOptionPane.ERROR_MESSAGE);
    }
}

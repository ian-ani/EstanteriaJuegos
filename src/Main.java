import ui.VentanaPrincipal;

import javax.swing.*;

import java.util.ResourceBundle;

import static config.UIConstantes.kALTO_VENTANA_PRINCIPAL;
import static config.UIConstantes.kANCHO_VENTANA_PRINCIPAL;

public class Main {
    public static void main(String[] args) {
        // Idioma
        ResourceBundle rb = ResourceBundle.getBundle("i18n.messages");

        // Clase de la GUI
        JFrame frame = new JFrame(rb.getString("window.title"));

        // Tamano de la ventana
        frame.setSize(kANCHO_VENTANA_PRINCIPAL, kALTO_VENTANA_PRINCIPAL);
        frame.setContentPane(new VentanaPrincipal(rb, rb.getLocale()).getPanelGeneral());

        // Para poder cerrar el programa
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame.pack();

        // Para que no se pueda redimensionar
        frame.setResizable(false);

        // Centra la ventana
        frame.setLocationRelativeTo(null);

        // Muestra la ventana
        frame.setVisible(true);
    }
}

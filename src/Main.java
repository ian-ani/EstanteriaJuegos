import ui.MainWindow;

import javax.swing.*;

import java.util.ResourceBundle;

import static config.UIConstants.kHEIGHT_MAIN_WINDOW;
import static config.UIConstants.kWIDTH_MAIN_WINDOW;

public class Main {
    public static void main(String[] args) {
        // Idioma
        ResourceBundle rb = ResourceBundle.getBundle("i18n.messages");

        // Clase de la GUI
        JFrame frame = new JFrame(rb.getString("window.title"));

        // Tamano de la ventana
        frame.setSize(kWIDTH_MAIN_WINDOW, kHEIGHT_MAIN_WINDOW);
        frame.setContentPane(new MainWindow(rb, rb.getLocale()).getPanelGeneral());

        // Para poder cerrar el programa
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Para que no se pueda redimensionar
        frame.setResizable(false);

        // Centra la ventana
        frame.setLocationRelativeTo(null);

        // Muestra la ventana
        frame.setVisible(true);
    }
}

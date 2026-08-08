package util;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class General {
    public static void blockButtons(JPanel panel, boolean status, JButton ...ignoreBtn) {
        // Componentes del panel
        Component[] components = panel.getComponents();

        // Si es un boton, desactivar
        for (Component c: components) {
            if (c instanceof JButton btn) {
                // Comprobar si es un boton a NO bloquear
                boolean isNoBlockBtn = Arrays.asList(ignoreBtn).contains(btn);

                if (!isNoBlockBtn) {
                    btn.setEnabled(status);
                }
            }
        }
    }
}

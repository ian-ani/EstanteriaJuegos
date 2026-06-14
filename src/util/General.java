package util;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class General {
    public static void bloquearBotones(JPanel panel, boolean estado, JButton ...btnIgnorar) {
        // Componentes del panel
        Component[] componentes = panel.getComponents();

        // Si es un boton, desactivar
        for (Component c: componentes) {
            if (c instanceof JButton btn) {
                // Comprobar si es un boton a NO bloquear
                boolean esNoBloquear = Arrays.asList(btnIgnorar).contains(btn);

                if (!esNoBloquear) {
                    btn.setEnabled(estado);
                }
            }
        }
    }
}

package ui;

import modelo.Estado;
import modelo.Juego;
import modelo.Valoracion;
import utiles.Mensaje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DetalleJuego extends JDialog {
    private Set<String> etiquetasTmp = new HashSet<>();
    private Consumer<Juego> onJuegoCreado; // callback consumer

    private JPanel panelGeneral;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel botonesPanel;
    private JPanel botonesPanelSecundario;
    private JPanel detallePanel;
    private JLabel nombreLabel;
    private JLabel plataformaLabel;
    private JLabel estadoLabel;
    private JLabel etiquetasLabel;
    private JLabel valoracionLabel;
    private JLabel notasLabel;
    private JTextField nombreEntrada;
    private JTextField plataformaEntrada;
    private JRadioButton completadoRadio;
    private JRadioButton jugandoRadio;
    private JRadioButton pendienteRadio;
    private JRadioButton abandonadoRadio;
    private JPanel nombrePanel;
    private JPanel plataformaPanel;
    private JPanel estadoPanel;
    private JPanel etiquetasPanel;
    private JPanel valoracionPanel;
    private JRadioButton gustadoRadio;
    private JRadioButton indiferenteRadio;
    private JRadioButton noGustadoRadio;
    private JPanel notasPanel;
    private JTextArea notasEntrada;
    private JRadioButton noValoradoRadio;
    private JPanel valoracionEstadoPanel;
    private JTextField etiquetaEntrada;
    private JButton anadirEtiquetaButton;
    private JPanel etiquetasAnadidasPanel;

    public DetalleJuego() {
        setContentPane(panelGeneral);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        panelGeneral.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Evento al anadir una etiqueta y pulsar '+'
        anadirEtiquetaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validarLargoEtiquetas()) {
                    if (validarEntrada(15, etiquetaEntrada)) {
                        // TODO hacer que los botones de las etiquetas sean mas pequenos, se ve feo
                        // Anadir panel con campo de texto y un boton de eliminar
                        crearPanelEtiqueta();
                    } else {
                        Mensaje.mostrarMensajeError(
                                "Etiqueta no válida",
                                "La etiqueta no puede estar vacía ni contener más de 15 caracteres."
                        );
                        return;
                    }
                } else {
                    Mensaje.mostrarMensajeError("Límite etiquetas", "No se pueden añadir más de 3 etiquetas.");
                    return;
                }

                // Redibujar panel
                redibujarEtiquetas(etiquetasAnadidasPanel);
            }
        });
    }

    private void redibujarEtiquetas(JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }

    private void crearPanelEtiqueta() {
        // Anadir panel
        JPanel panel = new JPanel();
        etiquetasAnadidasPanel.add(panel);

        // Anadir texto
        JTextField etiqueta = new JTextField(etiquetaEntrada.getText().trim());
        panel.add(etiqueta);

        // Anadir boton
        JButton btn = new JButton("x");
        panel.add(btn);

        // Anadir a la lista
        etiquetasTmp.add(etiqueta.getText());

        // Evento que borra un panel (con su campo de texto y boton)
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                etiquetasAnadidasPanel.remove(panel);

                // Redibujar panel
                redibujarEtiquetas(etiquetasAnadidasPanel);
            }
        });
    }

    private boolean validarLargoEtiquetas() {
        // Numero de etiquetas
        int numEtiquetas = 0;

        // Componentes del panel
        Component[] componentes = etiquetasAnadidasPanel.getComponents();

        // Recorrer componentes, si es un JPanel incrementar el contador
        for (Component c: componentes) {
            if (c instanceof JPanel) {
                numEtiquetas++;
            }
        }

        return (numEtiquetas >= 3);
    }

    // Valida el largo de las entradas
    private boolean validarEntrada(int largo, JTextField campo) {
        if (campo.getText().trim().isEmpty()) {
            return false;
        }

        return campo.getText().trim().length() <= largo;
    }

    // Obtener valor de los radios
    private String getRadio(JPanel panel) {
        Component[] componentes = panel.getComponents();

        for (Component c: componentes) {
            if (c instanceof JRadioButton btn) {
                if (btn.isSelected()) {
                    return btn.getActionCommand();
                }
            }
        }

        return null;
    }

    // Quita la seleccion de los radio del panel pasado y pone como seleccionado un radio especifico
    private void reiniciarRadio(JPanel panel, JRadioButton radio) {
        // Componentes del panel
        Component[] componentes = panel.getComponents();

        // Si es un boton, deseleccionar
        for (Component c: componentes) {
            if (c instanceof JRadioButton btn) {
                btn.setSelected(false);
            }
        }

        radio.setSelected(true);
    }

    private void reiniciarEtiquetas(JPanel panel) {
        // Componentes del panel
        Component[] componentes = panel.getComponents();

        // Recorrer componentes, borrar si es un JPanel
        for (Component c: componentes) {
            if (c instanceof JPanel) {
                panel.remove(c);
            }
        }
    }

    // Limpia el juego de la ventana actual para que no quede basurilla
    private void init() {
        // Vaciar campos de texto
        nombreEntrada.setText("");
        plataformaEntrada.setText("");
        notasEntrada.setText("");

        // Deseleccionar todos los radios y seleccionar uno especifico
        reiniciarRadio(estadoPanel, completadoRadio);
        reiniciarRadio(valoracionPanel, noValoradoRadio);

        // Eliminar lo referente a etiquetas (campo de texto, vaciar lista, vaciar paneles)
        etiquetaEntrada.setText("");
        etiquetasTmp.clear();
        reiniciarEtiquetas(etiquetasAnadidasPanel);
    }

    // Guardar los datos de campos y radios en Juego
    private Juego crearJuego() {
        // Obtener campos
        String nombre = nombreEntrada.getText();
        String plataforma = plataformaEntrada.getText();
        Estado estado = Estado.valueOf(getRadio(estadoPanel));
        Valoracion valoracion = Valoracion.valueOf(getRadio(valoracionPanel));
        String notas = notasEntrada.getText();

        // Instanciar juego
        Juego juego = new Juego(nombre, plataforma, estado, valoracion, notas);

        // Anadir etiquetas guardadas temporalmente en 'etiquetasTmp'
        for (String e: etiquetasTmp) {
            juego.anadirEtiqueta(e);
        }

        return juego;
    }

    // Boton 'OK'
    private void onOK() {
        try {
            Juego juego = crearJuego();

            // Si hay 'alguien' esta a la espera de una senal
            if (onJuegoCreado != null) {
                onJuegoCreado.accept(juego); // ejecuta callback
            }

            // Reinicio de la ventana (limpiar juego)
            init();

            // TODO limpiar panel al anadirlo
            dispose();
        } catch (Exception e) {
            Mensaje.mostrarMensajeError("Error", "No se ha podido guardar el juego.");
        }
    }

    // Boton 'CANCELAR'
    private void onCancel() {
        // Cerrar ventana
        dispose();
    }

    // Registrar callback
    public void setOnJuegoCreado(Consumer<Juego> c) {
        this.onJuegoCreado = c;
    }

    // MAIN
    public static void main(String[] args) {
        DetalleJuego dialog = new DetalleJuego();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    // Crear y editar podrian llamar ambos a esta ventana, pero desde GestionJuegos
    // se podria mandar el objeto de ese juego en especifico
    // Si es null no se ha creado, entonces llama a crear
    // Si no es null ya existe y se llama a editar
    // Si es editar se muestra el contenido en sus respectivos textfields, radios, etc
    // Si se da a OK que de una senal de mostrar toda la tabla de nuevo desde la principal, asi que
    // habra que pasar el objeto tambien previamente? mas o menos es la idea
}

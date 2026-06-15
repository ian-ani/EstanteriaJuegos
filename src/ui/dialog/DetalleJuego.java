package ui.dialog;

import entity.Estado;
import entity.Juego;
import entity.Valoracion;
import util.Mensaje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DetalleJuego extends JDialog {
    private Set<String> etiquetasTmp = new HashSet<>();
    private Consumer<Juego> onJuegoCreado; // callback consumer

    private List<Juego> juegos;
    private Juego juegoSeleccionado;
    private Modo modoSeleccionado;

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

    // enum anidado para el 'Modo' (se pasa al constructor al abrir la ventana)
    public enum Modo {
        VER, CREAR;
    }

    public DetalleJuego(List<Juego> juegos, Juego juegoSeleccionado, Modo modo) {
        this.juegos = juegos;
        this.juegoSeleccionado = juegoSeleccionado;
        this.modoSeleccionado = modo;

        // Configuracion inicial de la ventana (limpieza de registro anterior y texto del boton 'ok')
        init();

        // Mas configuracion de la ventana
        setContentPane(panelGeneral);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // Evento del boton 'OK'
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // Evento del boton 'CANCEL'
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
                        crearPanelEtiqueta(etiquetaEntrada.getText().trim());
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

    private void crearPanelEtiqueta(String _etiqueta) {
        // Si la etiqueta ya existe, no continuar
        if (etiquetaExiste(_etiqueta)) return;

        // Anadir panel
        JPanel panel = new JPanel();
        etiquetasAnadidasPanel.add(panel);

        // Anadir texto
        JTextField etiqueta = new JTextField(_etiqueta);
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

    private boolean etiquetaExiste(String etiqueta) {
        for (String e: etiquetasTmp) {
            if (e.equalsIgnoreCase(etiqueta)) return true;
        }

        return false;
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

    // Selecciona un radio (cuando se pulsa 'ver', es para rellenar con lo que haya en la tabla)
    private void marcarRadio(JPanel panel, String valor) {
        // Componentes del panel
        Component[] componentes = panel.getComponents();

        // Si coin
        for (Component c: componentes) {
            if (c instanceof JRadioButton btn) {
                btn.setSelected(btn.getActionCommand().equalsIgnoreCase(valor));
            }
        }
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

    // Reinicia las etiquetas (las borra)
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

    private void initVer() {
        // Cambiar texto de boton 'OK' a 'Editar'
        buttonOK.setText("Editar");

        // Volcar toda la informacion de Juego a los campos de texto
        nombreEntrada.setText(juegoSeleccionado.getNombre());
        plataformaEntrada.setText(juegoSeleccionado.getPlataforma());
        notasEntrada.setText(juegoSeleccionado.getNotas());

        // Marcar radios
        marcarRadio(estadoPanel, juegoSeleccionado.getEstado().toString());
        marcarRadio(valoracionPanel, juegoSeleccionado.getValoracion().toString());

        // TODO ahora mismo hay un bug donde si una etiqueta se borra y se quiere anadir, no se anade

        // Anadir etiquetas
        for (String e: juegoSeleccionado.getEtiquetas()) {
            crearPanelEtiqueta(e);
        }
    }

    private void initCrear() {
        // Cambiar texto de boton 'OK' a 'Crear'
        buttonOK.setText("Crear");

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

    // Limpia el juego de la ventana actual para que no quede basurilla
    private void init() {
        switch (modoSeleccionado) {
            case Modo.VER -> initVer();
            case Modo.CREAR -> initCrear();
        }
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

    private boolean verificarJuegoExistente(String nombre, String plataforma) {
        for (Juego juego: juegos) {
            if (juego.getNombre().equalsIgnoreCase(nombre) && juego.getPlataforma().equalsIgnoreCase(plataforma)) {
                //System.out.println("El juego ya existe.");
                return true;
            }
        }

        return false;
    }

    // Modificar juego si se ha abierto desde 'ver'
    private void modificarJuego() {
        // Extraer los dos datos para la verificacion de un juego existente
        String nombre = nombreEntrada.getText();
        String plataforma = plataformaEntrada.getText();

        // Verificar si el juego existe (y no efectuar los cambios si es asi)
        boolean existente = verificarJuegoExistente(nombre, plataforma);

        if (!existente) {
            // Cambiar datos
            juegoSeleccionado.setNombre(nombre);
            juegoSeleccionado.setPlataforma(plataforma);
            juegoSeleccionado.setEstado(Estado.valueOf(getRadio(estadoPanel)));
            juegoSeleccionado.setValoracion(Valoracion.valueOf(getRadio(valoracionPanel)));
            juegoSeleccionado.setNotas(notasEntrada.getText());

            // Anadir etiquetas guardadas temporalmente en 'etiquetasTmp'
            for (String e: etiquetasTmp) {
                juegoSeleccionado.anadirEtiqueta(e);
            }

            Mensaje.mostrarMensajeInfo("Juego modificado", "El juego se ha modificado correctamente.");
        } else {
            Mensaje.mostrarMensajePeligro("Juego ya existe",
                    "El juego ya coincide en nombre y plataforma con otro juego.");
        }
    }

    // Boton 'OK'
    private void onOK() {
        try {
            switch (modoSeleccionado) {
                case Modo.VER -> modificarJuego();
                case Modo.CREAR -> {
                    Juego juego = crearJuego();

                    // Si hay 'alguien' esta a la espera de una senal
                    if (onJuegoCreado != null) {
                        onJuegoCreado.accept(juego); // ejecuta callback
                    }
                }
            }
        } catch (Exception e) {
            Mensaje.mostrarMensajeError("Error", "No se ha podido guardar el juego.");
        } finally {
            // Cerrar ventana
            dispose();
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
}

// TODO bug donde al anadir una etiqueta, si se borra luego no se puede volver a anadir?
package ui.dialog;

import dao.DataManager;
import entity.Opinion;
import entity.Status;
import entity.Tag;
import entity.Game;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class DetalleJuego extends JDialog {
    // Etiquetas
    private Set<Tag> etiquetasTmp = new HashSet<>();

    // Consumer
    private Consumer<Game> onJuegoCreado; // callback consumer

    // Lista de juegos y juegos seleccionado
    private List<Game> games;
    private Game juegoSeleccionado;
    private Modo modoSeleccionado;

    // Variables de idioma
    private ResourceBundle rb;
    private Locale locale;

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

    // Enum anidado para el 'Modo' (se pasa al constructor al abrir la ventana)
    public enum Modo {
        VER, CREAR;
    }

    public DetalleJuego(ResourceBundle rb, Locale locale, List<Game> games, Game juegoSeleccionado, Modo modo) {
        // Anadir ResourceBundle
        this.rb = rb;
        this.locale = locale;

        // Juegos, juego seleccionado y modo (ver o anadir)
        this.games = games;
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
                        Message.showMessageError(
                                rb.getString("error.invalid_tag.title"),
                                rb.getString("error.invalid_tag.msg")
                        );
                        return;
                    }
                } else {
                    Message.showMessageError(
                            rb.getString("error.max_limit_tag.title"),
                            rb.getString("error.max_limit_tag.msg")
                    );
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

    private void crearPanelEtiqueta(String nombre) {
        // Si la etiqueta ya existe, no continuar
        if (etiquetaExiste(nombre)) return;

        // Crear etiqueta
        Tag tag = new Tag(nombre);

        // Guardar etiqueta
        if (juegoSeleccionado != null) {
            juegoSeleccionado.addTag(tag);
            etiquetasTmp.add(tag);
        }

        // Anadir panel
        JPanel panel = new JPanel();
        etiquetasAnadidasPanel.add(panel);

        // Anadir texto
        JTextField textoEtiqueta = new JTextField(nombre);
        panel.add(textoEtiqueta);

        // Anadir boton
        JButton btn = new JButton("x");
        panel.add(btn);

        // Evento que borra un panel (con su campo de texto y boton)
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                etiquetasTmp.remove(tag);

                if (juegoSeleccionado != null) {
                    juegoSeleccionado.getTags().remove(tag);
                }

                // Borrar etiqueta (nivel visual)
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
        for (Tag e: etiquetasTmp) {
            if (e.getName().equalsIgnoreCase(etiqueta)) {
                System.out.println(e.getName() + " " + etiqueta);
                return true;
            }
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
        buttonOK.setText(rb.getString("button.edit"));

        // Volcar toda la informacion de Juego a los campos de texto
        nombreEntrada.setText(juegoSeleccionado.getName());
        plataformaEntrada.setText(juegoSeleccionado.getPlatform());
        notasEntrada.setText(juegoSeleccionado.getNotes());

        // Marcar radios
        marcarRadio(estadoPanel, juegoSeleccionado.getStatus().toString());
        marcarRadio(valoracionPanel, juegoSeleccionado.getOpinion().toString());

        // Anadir etiquetas
        for (Tag e: juegoSeleccionado.getTags()) {
            crearPanelEtiqueta(String.valueOf(e));
        }
    }

    private void initCrear() {
        // Cambiar texto de boton 'OK' a 'Crear'
        buttonOK.setText(rb.getString("button.add"));

        // Vaciar campos de texto
        nombreEntrada.setText("");
        plataformaEntrada.setText("");
        notasEntrada.setText("");

        // Deseleccionar todos los radios y seleccionar uno especifico
        reiniciarRadio(estadoPanel, completadoRadio);
        reiniciarRadio(valoracionPanel, noValoradoRadio);

        // Eliminar lo referente a etiquetas (campo de texto, vaciar lista, vaciar paneles)
        etiquetaEntrada.setText("");

        // Vaciar etiquetas temporales
        etiquetasTmp.clear();

        reiniciarEtiquetas(etiquetasAnadidasPanel);
    }

    // Traducir nombre de las propiedades
    private void traducir(JLabel label, String txt) {
        label.setText(rb.getString(txt) + ":");
    }

    private void nombrePropiedades() {
        traducir(nombreLabel, "table.name");
        traducir(plataformaLabel, "table.platform");
        traducir(etiquetasLabel, "table.tags");
        traducir(estadoLabel, "table.status");
        traducir(valoracionLabel, "table.opinion");
        traducir(notasLabel, "table.notes");
    }

    // Traducir nombre de los botones de estado y valoracion
    private void traducir(JRadioButton radio, String txt) {
        radio.setText(rb.getString(txt));
    }

    private void nombreEstado() {
        traducir(completadoRadio, "Status.COMPLETED");
        traducir(jugandoRadio, "Status.PLAYING");
        traducir(pendienteRadio, "Status.TO_PLAY");
        traducir(abandonadoRadio, "Status.DROPPED");
    }

    private void nombreValoracion() {
        traducir(gustadoRadio, "Opinion.LIKED");
        traducir(noGustadoRadio, "Opinion.NOT_LIKED");
        traducir(indiferenteRadio, "Opinion.INDIFFERENT");
        traducir(noValoradoRadio, "Opinion.NOT_RATED");
    }

    // Limpia el juego de la ventana actual para que no quede basurilla
    private void init() {
        nombrePropiedades();
        nombreEstado();
        nombreValoracion();

        switch (modoSeleccionado) {
            case Modo.VER -> initVer();
            case Modo.CREAR -> initCrear();
        }
    }

    // Guardar los datos de campos y radios en Juego
    private Game crearJuego() {
        //Set<Etiqueta> tmp = new HashSet<>();

        // Obtener campos
        String nombre = nombreEntrada.getText();
        String plataforma = plataformaEntrada.getText();
        Status status = Status.valueOf(getRadio(estadoPanel));
        Opinion opinion = Opinion.valueOf(getRadio(valoracionPanel));
        String notas = notasEntrada.getText();

        // Instanciar juego
        Game game = new Game(0, nombre, plataforma, status, opinion, notas);

        // Anadir etiquetas
        for (Component c: etiquetasAnadidasPanel.getComponents()) {
            if (c instanceof JPanel panel) {
                for (Component p: panel.getComponents()) {
                    if (p instanceof JTextField tf) game.addTag(new Tag(tf.getText()));
                }
            }
        }

        /*for (Etiqueta e: juegoSeleccionado.getEtiquetas()) {
            juego.anadirEtiqueta(e);
        }*/

        return game;
    }

    private boolean verificarJuegoExistente(String nombre, String plataforma) {
        for (Game game : games) {
            if (game.getName().equalsIgnoreCase(nombre) && game.getPlatform().equalsIgnoreCase(plataforma)) {
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

        // TODO Verificar si el juego existe (y no efectuar los cambios si es asi)
        //boolean existente = verificarJuegoExistente(nombre, plataforma);

        /*if (existente) {
            Mensaje.mostrarMensajePeligro("Juego existente", "El juego ya existe.");
            return;
        }*/

        // Cambiar datos
        juegoSeleccionado.setName(nombre);
        juegoSeleccionado.setPlatform(plataforma);
        juegoSeleccionado.setStatus(Status.valueOf(getRadio(estadoPanel)));
        juegoSeleccionado.setOpinion(Opinion.valueOf(getRadio(valoracionPanel)));
        juegoSeleccionado.setNotes(notasEntrada.getText());

        // Anadir etiquetas
        /*for (Etiqueta e: juegoSeleccionado.getEtiquetas()) {
            juegoSeleccionado.anadirEtiqueta(e);
        }*/

        if (DataManager.updateGame(juegoSeleccionado)) {
            Message.showMessageInfo(
                    rb.getString("info.edit_game.title"),
                    rb.getString("info.edit_game.msg")
            );
        } else {
            Message.showMessageError(
                    rb.getString("error.edit_game.title"),
                    rb.getString("error.edit_game.msg")
            );
        }
    }

    // Boton 'OK'
    private void onOK() {
        try {
            switch (modoSeleccionado) {
                case Modo.VER -> modificarJuego();
                case Modo.CREAR -> {
                    Game game = crearJuego();

                    // Si hay 'alguien' esta a la espera de una senal
                    if (onJuegoCreado != null) {
                        onJuegoCreado.accept(game); // ejecuta callback
                    }
                }
            }
        } catch (Exception e) {
            Message.showMessageError(
                    rb.getString("error.save_game.title"),
                    rb.getString("error.save_game.msg")
            );
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
    public void setOnJuegoCreado(Consumer<Game> c) {
        this.onJuegoCreado = c;
    }
}

// TODO no admite repetidos en modificar pero en crear si... arreglar eso
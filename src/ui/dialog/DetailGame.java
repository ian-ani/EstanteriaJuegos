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

public class DetailGame extends JDialog {
    // Etiquetas
    private Set<Tag> tagsTmp = new HashSet<>();

    // Consumer
    private Consumer<Game> onCreatedGame; // callback consumer

    // Lista de juegos y juegos seleccionado
    private List<Game> games;
    private Game selectedGame;
    private Mode selectedMode;

    // Variables de idioma
    private ResourceBundle rb;
    private Locale locale;

    private JPanel generalPanel;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel buttonsPanel;
    private JPanel buttonsSecondaryPanel;
    private JPanel detailPanel;
    private JLabel nameLabel;
    private JLabel platformLabel;
    private JLabel statusLabel;
    private JLabel tagsLabel;
    private JLabel opinionLabel;
    private JLabel notesLabel;
    private JTextField nameInput;
    private JTextField platformInput;
    private JRadioButton completedRadio;
    private JRadioButton playingRadio;
    private JRadioButton toPlayRadio;
    private JRadioButton droppedRadio;
    private JPanel namePanel;
    private JPanel platformPanel;
    private JPanel statusPanel;
    private JPanel tagsPanel;
    private JPanel opinionPanel;
    private JRadioButton likedRadio;
    private JRadioButton indifferentRadio;
    private JRadioButton notLikedRadio;
    private JPanel notesPanel;
    private JTextArea notesInput;
    private JRadioButton notRatedRadio;
    private JPanel opinionStatusPanel;
    private JTextField tagInput;
    private JButton addTagButton;
    private JPanel addedTagsPanel;

    // Enum anidado para el 'Modo' (se pasa al constructor al abrir la ventana)
    public enum Mode {
        VIEW, CREATE;
    }

    public DetailGame(ResourceBundle rb, Locale locale, List<Game> games, Game selectedGame, Mode mode) {
        // Anadir ResourceBundle
        this.rb = rb;
        this.locale = locale;

        // Juegos, juego seleccionado y modo (ver o anadir)
        this.games = games;
        this.selectedGame = selectedGame;
        this.selectedMode = mode;

        // Configuracion inicial de la ventana (limpieza de registro anterior y texto del boton 'ok')
        init();

        // Mas configuracion de la ventana
        setContentPane(generalPanel);
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
        generalPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Evento al anadir una etiqueta y pulsar '+'
        addTagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateLengthTags()) {
                    if (validateInput(15, tagInput)) {
                        // TODO hacer que los botones de las etiquetas sean mas pequenos, se ve feo
                        // Anadir panel con campo de texto y un boton de eliminar
                        createTagPanel(tagInput.getText().trim());
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
                redrawTags(addedTagsPanel);
            }
        });
    }

    private void redrawTags(JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }

    private void createTagPanel(String nombre) {
        // Si la etiqueta ya existe, no continuar
        if (tagExists(nombre)) return;

        // Crear etiqueta
        Tag tag = new Tag(nombre);

        // Guardar etiqueta
        if (selectedGame != null) {
            selectedGame.addTag(tag);
            tagsTmp.add(tag);
        }

        // Anadir panel
        JPanel panel = new JPanel();
        addedTagsPanel.add(panel);

        // Anadir texto
        JTextField textTag = new JTextField(nombre);
        panel.add(textTag);

        // Anadir boton
        JButton btn = new JButton("x");
        panel.add(btn);

        // Evento que borra un panel (con su campo de texto y boton)
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tagsTmp.remove(tag);

                if (selectedGame != null) {
                    selectedGame.getTags().remove(tag);
                }

                // Borrar etiqueta (nivel visual)
                addedTagsPanel.remove(panel);

                // Redibujar panel
                redrawTags(addedTagsPanel);
            }
        });
    }

    private boolean validateLengthTags() {
        // Numero de etiquetas
        int numTags = 0;

        // Componentes del panel
        Component[] components = addedTagsPanel.getComponents();

        // Recorrer componentes, si es un JPanel incrementar el contador
        for (Component c: components) {
            if (c instanceof JPanel) {
                numTags++;
            }
        }

        return (numTags >= 3);
    }

    // Valida el largo de las entradas
    private boolean validateInput(int length, JTextField field) {
        if (field.getText().trim().isEmpty()) {
            return false;
        }

        return field.getText().trim().length() <= length;
    }

    private boolean tagExists(String tag) {
        for (Tag e: tagsTmp) {
            if (e.getName().equalsIgnoreCase(tag)) {
                System.out.println(e.getName() + " " + tag);
                return true;
            }
        }

        return false;
    }

    // Obtener valor de los radios
    private String getRadio(JPanel panel) {
        Component[] components = panel.getComponents();

        for (Component c: components) {
            if (c instanceof JRadioButton btn) {
                if (btn.isSelected()) {
                    return btn.getActionCommand();
                }
            }
        }

        return null;
    }

    // Selecciona un radio (cuando se pulsa 'ver', es para rellenar con lo que haya en la tabla)
    private void checkRadio(JPanel panel, String value) {
        // Componentes del panel
        Component[] components = panel.getComponents();

        // Si coin
        for (Component c: components) {
            if (c instanceof JRadioButton btn) {
                btn.setSelected(btn.getActionCommand().equalsIgnoreCase(value));
            }
        }
    }

    // Quita la seleccion de los radio del panel pasado y pone como seleccionado un radio especifico
    private void clearRadio(JPanel panel, JRadioButton radio) {
        // Componentes del panel
        Component[] components = panel.getComponents();

        // Si es un boton, deseleccionar
        for (Component c: components) {
            if (c instanceof JRadioButton btn) {
                btn.setSelected(false);
            }
        }

        radio.setSelected(true);
    }

    // Reinicia las etiquetas (las borra)
    private void clearTags(JPanel panel) {
        // Componentes del panel
        Component[] components = panel.getComponents();

        // Recorrer componentes, borrar si es un JPanel
        for (Component c: components) {
            if (c instanceof JPanel) {
                panel.remove(c);
            }
        }
    }

    private void initView() {
        // Cambiar texto de boton 'OK' a 'Editar'
        buttonOK.setText(rb.getString("button.edit"));

        // Volcar toda la informacion de Juego a los campos de texto
        nameInput.setText(selectedGame.getName());
        platformInput.setText(selectedGame.getPlatform());
        notesInput.setText(selectedGame.getNotes());

        // Marcar radios
        checkRadio(statusPanel, selectedGame.getStatus().toString());
        checkRadio(opinionPanel, selectedGame.getOpinion().toString());

        // Anadir etiquetas
        for (Tag e: selectedGame.getTags()) {
            createTagPanel(String.valueOf(e));
        }
    }

    private void initCreate() {
        // Cambiar texto de boton 'OK' a 'Crear'
        buttonOK.setText(rb.getString("button.add"));

        // Vaciar campos de texto
        nameInput.setText("");
        platformInput.setText("");
        notesInput.setText("");

        // Deseleccionar todos los radios y seleccionar uno especifico
        clearRadio(statusPanel, completedRadio);
        clearRadio(opinionPanel, notRatedRadio);

        // Eliminar lo referente a etiquetas (campo de texto, vaciar lista, vaciar paneles)
        tagInput.setText("");

        // Vaciar etiquetas temporales
        tagsTmp.clear();

        clearTags(addedTagsPanel);
    }

    // Traducir nombre de las propiedades
    private void translate(JLabel label, String txt) {
        label.setText(rb.getString(txt) + ":");
    }

    private void propertiesName() {
        translate(nameLabel, "table.name");
        translate(platformLabel, "table.platform");
        translate(tagsLabel, "table.tags");
        translate(statusLabel, "table.status");
        translate(opinionLabel, "table.opinion");
        translate(notesLabel, "table.notes");
    }

    // Traducir nombre de los botones de estado y valoracion
    private void translate(JRadioButton radio, String txt) {
        radio.setText(rb.getString(txt));
    }

    private void statusName() {
        translate(completedRadio, "Status.COMPLETADO");
        translate(playingRadio, "Status.JUGANDO");
        translate(toPlayRadio, "Status.PENDIENTE");
        translate(droppedRadio, "Status.ABANDONADO");
    }

    private void opinionName() {
        translate(likedRadio, "Opinion.GUSTADO");
        translate(notLikedRadio, "Opinion.NO_GUSTADO");
        translate(indifferentRadio, "Opinion.INDIFERENTE");
        translate(notRatedRadio, "Opinion.NO_VALORADO");
    }

    // Limpia el juego de la ventana actual para que no quede basurilla
    private void init() {
        propertiesName();
        statusName();
        opinionName();

        switch (selectedMode) {
            case Mode.VIEW -> initView();
            case Mode.CREATE -> initCreate();
        }
    }

    // Guardar los datos de campos y radios en Juego
    private Game createGame() {
        // Obtener campos
        String name = nameInput.getText();
        String platform = platformInput.getText();
        Status status = Status.valueOf(getRadio(statusPanel));
        Opinion opinion = Opinion.valueOf(getRadio(opinionPanel));
        String notes = notesInput.getText();

        // Instanciar juego
        Game game = new Game(0, name, platform, status, opinion, notes);

        // Anadir etiquetas
        for (Component c: addedTagsPanel.getComponents()) {
            if (c instanceof JPanel panel) {
                for (Component p: panel.getComponents()) {
                    if (p instanceof JTextField tf) game.addTag(new Tag(tf.getText()));
                }
            }
        }

        return game;
    }

    // Comprobar si el juego existe o no (en base a coincidencia de nombre, plataforma y/o id)
    private boolean verifyExistingGame(String name, String platform) {
        return DataManager.selectGameByNameAndPlatform(name, platform) != null;
    }

    private boolean verifyExistingGame(String name, String platform, int id) {
        return DataManager.selectGameByNameAndPlatformAndDifferentId(name, platform, id) != null;
    }

    // Modificar juego si se ha abierto desde 'ver'
    private void modifyGame() {
        // Cambiar datos
        selectedGame.setName(nameInput.getText());
        selectedGame.setPlatform(platformInput.getText());
        selectedGame.setStatus(Status.valueOf(getRadio(statusPanel)));
        selectedGame.setOpinion(Opinion.valueOf(getRadio(opinionPanel)));
        selectedGame.setNotes(notesInput.getText());

        if (DataManager.updateGame(selectedGame)) {
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
        // Campos para la verificacion
        String name = nameInput.getText();
        String platform = platformInput.getText();

        try {
            switch (selectedMode) {
                case Mode.VIEW -> {
                    int id = selectedGame.getId();

                    if (!verifyExistingGame(name, platform, id)) {
                        modifyGame();
                    } else {
                        Message.showMessageWarning(
                                rb.getString("warning.exist_game.title"),
                                rb.getString("warning.modify.exist_game.msg")
                        );
                    }
                }
                case Mode.CREATE -> {
                    if (!verifyExistingGame(name, platform)) {
                        Game game = createGame();

                        // Si hay 'alguien' esta a la espera de una senal
                        if (onCreatedGame != null) {
                            onCreatedGame.accept(game); // ejecuta callback
                        }
                    } else {
                        Message.showMessageWarning(
                                rb.getString("warning.exist_game.title"),
                                rb.getString("warning.create.exist_game.msg")
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void setOnCreatedGame(Consumer<Game> c) {
        this.onCreatedGame = c;
    }
}

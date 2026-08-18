package ui;

import dao.DataManager;
import entity.Game;
import ui.dialog.DetailGame;
import util.ConvertCsv;
import util.General;
import util.Message;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static config.UIConstants.*;

public class MainWindow {
    /* ATRIBUTOS */

    // Variables de GestionJuegos
    private JPanel generalPanel;
    private JPanel searchPanel;
    private JPanel gamePanel;
    private JPanel buttonsPanel;
    private JButton removeButton;
    private JButton editButton;
    private JButton addButton;
    private JScrollPane tablePanel;
    private JTextField searchInput;
    private JTable table;
    private JPanel gameButtonPanel;
    private JPanel miscPanel;
    private JButton exportButton;
    private JButton clearButton;
    private JComboBox filterComboBox;
    private JButton languageButton;

    // Variables de idioma
    private ResourceBundle rb;
    private Locale locale;

    // Juego seleccionado
    Game selectedGame;

    // Lista donde se van a guardar los juegos temporalmente
    List<Game> games = new ArrayList<>();
    List<Game> showedGames = new ArrayList<>();

    /* CONSTRUCTOR */

    public MainWindow(ResourceBundle rb, Locale locale) {
        // Anadir ResourceBundle
        this.rb = rb;
        this.locale = locale;

        // Inicializacion de propiedades
        init();

        // Llama a la ventana de 'anadir juego'
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });

        // Ver registro seleccionado y permite editar en la nueva ventana de dialogo
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edit();
                General.blockButtons(gameButtonPanel, false, addButton);

                // Volver a mostrar la table
                games = DataManager.getGames();
                showedGames = games;
                createTable(showedGames);
            }
        });

        // Seleccionar fila
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JTable t = (JTable) e.getSource();
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);

                if (e.getClickCount() != -1 && table.getSelectedRow() != -1 && row != -1) {
                    int rowModel = table.convertRowIndexToModel(row);
                    selectedGame = showedGames.get(rowModel);
                    General.blockButtons(gameButtonPanel, true, addButton);
                }
            }
        });

        // Busca un juego (por nombre) al pulsar 'Enter'
        searchInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Si esta vacio, early return
                    if (searchInput.getText().isBlank()) return;

                    // Si no esta vacio, buscar
                    boolean found = search(searchInput.getText().trim());

                    if (!found) {
                        Message.showMessageError(
                                rb.getString("error.not_found.title"),
                                rb.getString("error.not_found.msg")
                        );
                    } else {
                        if (selectedGame != null) {
                            General.blockButtons(gameButtonPanel, true, addButton);
                        }
                    }
                }
            }
        });

        // Reinicia la tabla (despues de haber filtrado)
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tecnicamente no es necesario si se escribe "" y pulsa 'Enter'
                // pero queda un poco mejor de cara a UX?
                clear();
            }
        });

        // Borrar registro seleccionado
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Borrar juego guardado en 'juegoSeleccionado'
                boolean removed = remove();

                // Mensaje al usuario
                if (removed) Message.showMessageInfo(rb.getString("info.delete_game.title"), rb.getString("info.delete_game.msg"));
                else Message.showMessageError(rb.getString("error.delete_game.title"), rb.getString("error.delete_game.msg"));

                // Volver a mostrar la tabla
                showedGames = games;
                createTable(showedGames);

                // Bloquear botones
                General.blockButtons(gameButtonPanel, false, addButton);
            }
        });

        // Exportar a CSV
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int retVal = fileChooser.showSaveDialog(getPanelGeneral());

                if (retVal == JFileChooser.APPROVE_OPTION) {
                    ConvertCsv csv = new ConvertCsv(fileChooser.getSelectedFile().toPath(), games);

                    if (csv.convert()) {
                        Message.showMessageInfo(
                                rb.getString("info.csv_export.title"),
                                rb.getString("info.csv_export.msg")
                        );
                    } else {
                        Message.showMessageError(
                                rb.getString("error.csv_export.title"),
                                rb.getString("error.csv_export.msg")
                        );
                    }
                }
            }
        });

        // Cambiar idioma
        languageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (locale.equals(Locale.ENGLISH)) changeLanguage(new Locale("es"));
                else changeLanguage(Locale.ENGLISH);
            }
        });
    }

    /* GETTERS */

    public JPanel getPanelGeneral() {
        return generalPanel;
    }

    /* OTROS METODOS */

    // Propiedades iniciales de la ventana principal
    private void init() {
        // Obtener registros
        games = DataManager.getGames();

        // Propiedades
        initComboBox();

        // Texto de los botones de acuerdo al idioma
        nameButtons();

        // Mostrar juegos
        showedGames = games;
        createTable(games);

        // Bloquear botones de eliminar y editar hasta que haya un juego seleccionado
        General.blockButtons(gameButtonPanel, false, addButton);
    }

    private void initComboBox() {
        filterComboBox.setModel(new DefaultComboBoxModel<>(
                new String[] {rb.getString("filter.name"), rb.getString("filter.platform")}
        ));
    }

    // Boton de cambiar idioma (ingles o castellano)
    private void changeLanguage(Locale locale) {
        // Idioma
        // TODO poner el nombre del bundle en una constante
        rb = ResourceBundle.getBundle("i18n.messages", locale);

        // Obtener ancestro
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(generalPanel);

        // Crear nueva ventana principal
        MainWindow vp = new MainWindow(rb, locale);

        // Volver a poner el titulo y el panel general en la nueva ventana
        frame.setTitle(rb.getString("window.title"));
        frame.setContentPane(vp.getPanelGeneral());

        // Cambiar textos
        frame.revalidate();
        frame.repaint();

        // Texto de los botones de acuerdo al idioma
        nameButtons();
    }

    // Nombre de los botones de la ventana principal
    private void translate(JButton btn, String txt) {
        btn.setText(rb.getString(txt));
    }

    private void nameButtons() {
        translate(addButton, "button.add");
        translate(editButton, "button.view");
        translate(removeButton, "button.remove");
        translate(languageButton, "button.language");
        translate(exportButton, "button.export");
        translate(clearButton, "button.clear");
    }

    // Tabla de la ventana principal con los registros del juego
    private void createTable(List<Game> list) {
        selectedGame = null;

        Object[][] data = new Object[list.size()][6];
        String[] columns = new String[] {
                rb.getString("table.name"), rb.getString("table.platform"), rb.getString("table.status"),
                rb.getString("table.tags"), rb.getString("table.opinion"), rb.getString("table.notes")
        };

        // Recorrer y guardar datos de los juegos en la table
        for (int i = 0; i < list.size(); i++) {
            Game game = list.get(i);

            data[i][0] = game.getName();
            data[i][1] = game.getPlatform();
            data[i][2] = rb.getString("Status." + game.getStatus());
            data[i][3] = game.getTags();
            data[i][4] = rb.getString("Opinion." + game.getOpinion());
            data[i][5] = game.getNotes();
        }

        // Crear table: datos y cabecera
        table.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        // Permitir ordenar de manera ascendente y descendente por campos
        table.setAutoCreateRowSorter(true);
    }

    // Boton de anadir juego
    private void add() {
        DetailGame detailGameWindow = new DetailGame(this.rb, this.locale, games, null, DetailGame.Mode.CREATE);

        detailGameWindow.setTitle(rb.getString("window.add.title"));
        detailGameWindow.setSize(kWIDTH_WINDOW, kHEIGHT_WINDOW);
        detailGameWindow.setResizable(false);
        detailGameWindow.setLocationRelativeTo(null);

        // callback
        detailGameWindow.setOnCreatedGame((juego) -> {
            DataManager.insertGame(juego);
            games = DataManager.getGames();
            showedGames = games;
            createTable(showedGames);
            Message.showMessageInfo(rb.getString("info.add_game.title"), rb.getString("info.add_game.msg"));
        });

        detailGameWindow.setVisible(true);
    }

    // Boton de borrar juego
    private void translateRemoveButtons() {
        if (!locale.equals(Locale.ENGLISH)) {
            UIManager.put("OptionPane.yesButtonText", "Sí");
        } else {
            UIManager.put("OptionPane.yesButtonText", "Yes");
        }
    }

    private int windowConfirmRemoval() {
         return JOptionPane.showConfirmDialog(
                null,
                rb.getString("info.confirm_delete_game.msg") + " " + selectedGame.getName() + "?",
                rb.getString("info.confirm_delete_game.title"),
                JOptionPane.YES_NO_OPTION
        );
    }

    private boolean remove() {
        // Traducir texto de los botones de 'SI' cuando el idioma seleccionado es el castellano
        translateRemoveButtons();

        // Mostrar una ventana de confirmacion
        int answer = windowConfirmRemoval();

        // Si la respuesta es 'SI', entonces borrar el juego actual
        if (answer == JOptionPane.YES_OPTION) {
            if (DataManager.deleteGame(selectedGame)) {
                games.remove(selectedGame);
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    // Boton de ver juego
    private void edit() {
        DetailGame detailGameWindow = new DetailGame(this.rb, this.locale, games, selectedGame, DetailGame.Mode.VIEW);

        detailGameWindow.setTitle(rb.getString("window.view.title"));
        detailGameWindow.setSize(kWIDTH_WINDOW, kHEIGHT_WINDOW);
        detailGameWindow.setResizable(false);
        detailGameWindow.setLocationRelativeTo(null);

        detailGameWindow.setVisible(true);
    }

    // Busquedas de juego bajo criterio
    private boolean searchName(String nombre) {
        List<Game> tmp = DataManager.selectGameByName(nombre);

        // Si la lista esta vacia
        if (tmp == null || tmp.isEmpty()) return false;

        // Ordenar ascendentemente antes de mostrar
        tmp.sort(Comparator.comparing(Game::getName));

        // Mostrar resultados en la table
        showedGames = tmp;
        createTable(showedGames);

        return !tmp.isEmpty();
    }

    private boolean searchPlatform(String nombre) {
        List<Game> tmp = DataManager.selectGameByPlatform(nombre);

        // Si la lista esta vacia
        if (tmp == null || tmp.isEmpty()) return false;

        // Ordenar ascendentemente antes de mostrar
        tmp.sort(Comparator.comparing(Game::getPlatform));

        // Mostrar resultados en la table
        showedGames = tmp;
        createTable(showedGames);

        return !tmp.isEmpty();
    }

    private boolean search(String nombre) {
        // Obtener criterio de busqueda
        String filter = (String) filterComboBox.getSelectedItem();

        // Volver a bloquear botones de edicion y borrado
        General.blockButtons(gameButtonPanel, false, addButton);

        // Busqueda segun criterio
        if (rb.getString("filter.name").equals(filter)) {
            return searchName(nombre);
        } else if (rb.getString("filter.platform").equals(filter)) {
            return searchPlatform(nombre);
        }

        return false;
    }

    private void clear() {
        searchInput.setText("");

        // Obtener registros
        games = DataManager.getGames();

        showedGames = games;
        createTable(showedGames);

        General.blockButtons(gameButtonPanel, false, addButton);
    }

    // TODO Anadir icono del programa
}

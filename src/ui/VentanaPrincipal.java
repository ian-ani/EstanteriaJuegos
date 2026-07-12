package ui;

import dao.DataManager;
import entity.Juego;
import ui.dialog.DetalleJuego;
import util.General;
import util.Mensaje;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static config.UIConstantes.*;

public class VentanaPrincipal {
    /* ATRIBUTOS */

    // Variables de GestionJuegos
    private JPanel panelGeneral;
    private JPanel busquedaPanel;
    private JPanel juegoPanel;
    private JPanel botonesPanel;
    private JButton eliminarButton;
    private JButton verButton;
    private JButton anadirButton;
    private JScrollPane tablaPanel;
    private JTextField busquedaEntrada;
    private JTable tabla;
    private JPanel juegoBotonPanel;
    private JPanel miscPanel;
    private JButton exportarButton;
    private JButton reiniciarButton;
    private JComboBox filtroComboBox;
    private JButton idiomaButton;

    // Variables de idioma
    private ResourceBundle rb;
    private Locale locale;

    // Juego seleccionado
    Juego juegoSeleccionado;

    // Lista donde se van a guardar los juegos temporalmente
    List<Juego> juegos = new ArrayList<>();
    List<Juego> juegosMostrados = new ArrayList<>();

    /* CONSTRUCTOR */

    public VentanaPrincipal(ResourceBundle rb, Locale locale) {
        // Anadir ResourceBundle
        this.rb = rb;
        this.locale = locale;

        // Inicializacion de propiedades
        init();

        // Llama a la ventana de 'anadir juego'
        anadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anadir();
            }
        });

        // Ver registro seleccionado y permite editar en la nueva ventana de dialogo
        verButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ver();
                General.bloquearBotones(juegoBotonPanel, false, anadirButton);

                // Volver a mostrar la tabla
                juegos = DataManager.getGames();
                juegosMostrados = juegos;
                crearTabla(juegosMostrados);
            }
        });

        // Seleccionar fila
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JTable t = (JTable) e.getSource();
                Point p = e.getPoint();
                int fila = tabla.rowAtPoint(p);

                if (e.getClickCount() != -1 && tabla.getSelectedRow() != -1 && fila != -1) {
                    int filaModelo = tabla.convertRowIndexToModel(fila);
                    juegoSeleccionado = juegosMostrados.get(filaModelo);
                    General.bloquearBotones(juegoBotonPanel, true, anadirButton);
                }
            }
        });

        // Busca un juego (por nombre) al pulsar 'Enter'
        busquedaEntrada.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Si esta vacio, early return
                    if (busquedaEntrada.getText().isBlank()) return;

                    // Si no esta vacio, buscar
                    boolean encontrado = buscar(busquedaEntrada.getText().trim());

                    if (!encontrado) {
                        Mensaje.mostrarMensajeError(
                                rb.getString("error.not_found.title"),
                                rb.getString("error.not_found.msg")
                        );
                    } else {
                        if (juegoSeleccionado != null) {
                            General.bloquearBotones(juegoBotonPanel, true, anadirButton);
                        }
                    }
                }
            }
        });

        // Reinicia la tabla (despues de haber filtrado)
        reiniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tecnicamente no es necesario si se escribe "" y pulsa 'Enter'
                // pero queda un poco mejor de cara a UX?
                reiniciar();
            }
        });

        // Borrar registro seleccionado
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Borrar juego guardado en 'juegoSeleccionado'
                boolean borrado = eliminar();

                // Mensaje al usuario
                if (borrado) Mensaje.mostrarMensajeInfo(rb.getString("info.delete_game.title"), rb.getString("info.delete_game.msg"));
                else Mensaje.mostrarMensajeError(rb.getString("error.delete_game.title"), rb.getString("error.delete_game.msg"));

                // Volver a mostrar la tabla
                juegosMostrados = juegos;
                crearTabla(juegosMostrados);

                // Bloquear botones
                General.bloquearBotones(juegoBotonPanel, false, anadirButton);
            }
        });

        // Exportar a CSV
        exportarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });

        // Cambiar idioma
        idiomaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (locale.equals(Locale.ENGLISH)) cambiarIdioma(new Locale("es"));
                else cambiarIdioma(Locale.ENGLISH);
            }
        });
    }

    /* GETTERS */

    public JPanel getPanelGeneral() {
        return panelGeneral;
    }

    /* OTROS METODOS */

    private void init() {
        // Obtener registros
        juegos = DataManager.getGames();

        // Propiedades
        initComboBox();

        // Mostrar juegos
        juegosMostrados = juegos;
        crearTabla(juegos);

        // Bloquear botones de eliminar y editar hasta que haya un juego seleccionado
        General.bloquearBotones(juegoBotonPanel, false, anadirButton);
    }

    private void initComboBox() {
        filtroComboBox.setModel(new DefaultComboBoxModel<>(
                new String[] {rb.getString("filter.name"), rb.getString("filter.platform")}
        ));
    }

    private void cambiarIdioma(Locale locale) {
        // Idioma
        // TODO poner el nombre del bundle en una constante
        rb = ResourceBundle.getBundle("i18n.messages", locale);

        // Obtener ancestro
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panelGeneral);

        // Crear nueva ventana principal
        VentanaPrincipal vp = new VentanaPrincipal(rb, locale);

        // Volver a poner el titulo y el panel general en la nueva ventana
        frame.setTitle(rb.getString("window.title"));
        frame.setContentPane(vp.getPanelGeneral());

        // Cambiar textos
        frame.revalidate();
        frame.repaint();
    }

    private void crearTabla(List<Juego> lista) {
        juegoSeleccionado = null;

        Object[][] datos = new Object[lista.size()][6];
        String[] columnas = new String[]{
                rb.getString("table.name"), rb.getString("table.platform"), rb.getString("table.status"),
                rb.getString("table.tags"), rb.getString("table.opinion"), rb.getString("table.notes")
        };

        // Recorrer y guardar datos de los juegos en la tabla
        for (int i = 0; i < lista.size(); i++) {
            Juego juego = lista.get(i);

            datos[i][0] = juego.getNombre();
            datos[i][1] = juego.getPlataforma();
            datos[i][2] = juego.getEstado();
            datos[i][3] = juego.getEtiquetas();
            datos[i][4] = juego.getValoracion();
            datos[i][5] = juego.getNotas();
        }

        // Crear tabla: datos y cabecera
        tabla.setModel(new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private void anadir() {
        DetalleJuego detalleJuegoWindow = new DetalleJuego(juegos, null, DetalleJuego.Modo.CREAR);

        detalleJuegoWindow.setTitle(rb.getString("window.add.title"));
        detalleJuegoWindow.setSize(kANCHO_VENTANA, kALTO_VENTANA);
        detalleJuegoWindow.setResizable(false);
        detalleJuegoWindow.setLocationRelativeTo(null);

        // callback
        detalleJuegoWindow.setOnJuegoCreado((juego) -> {
                if (!juegos.contains(juego)) {
                    DataManager.insertGame(juego);
                    juegos = DataManager.getGames();
                    juegosMostrados = juegos;
                    crearTabla(juegosMostrados);
                    Mensaje.mostrarMensajeInfo(rb.getString("info.add_game.title"), rb.getString("info.add_game.msg"));
                } else {
                    Mensaje.mostrarMensajePeligro(rb.getString("warning.exist_game.title"), rb.getString("warning.exist_game.msg"));
                }
            }
        );

        detalleJuegoWindow.setVisible(true);
    }

    private boolean eliminar() {
        // Traducir texto de los botoners de 'SI' y 'NO'
        // TODO estos mensajes
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");

        // Mostrar una ventana de confirmacion
        int respuesta = JOptionPane.showConfirmDialog(
                null,
                rb.getString("info.confirm_delete_game.title") + " " + juegoSeleccionado.getNombre() + "?",
                rb.getString("info.confirm_delete_game.msg"),
                JOptionPane.YES_NO_OPTION
        );

        // Si la respuesta es 'SI', entonces borrar el juego actual
        if (respuesta == JOptionPane.YES_OPTION) {
            if (DataManager.deleteGame(juegoSeleccionado)) {
                juegos.remove(juegoSeleccionado);
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    private void ver() {
        DetalleJuego detalleJuegoWindow = new DetalleJuego(juegos, juegoSeleccionado, DetalleJuego.Modo.VER);

        detalleJuegoWindow.setTitle(rb.getString("window.view.title"));
        detalleJuegoWindow.setSize(kANCHO_VENTANA, kALTO_VENTANA);
        detalleJuegoWindow.setResizable(false);
        detalleJuegoWindow.setLocationRelativeTo(null);

        detalleJuegoWindow.setVisible(true);

        //juegosMostrados = juegos;
        //crearTabla(juegosMostrados);
    }

    private boolean buscarNombre(String nombre) {
        List<Juego> tmp = DataManager.selectGameByName(nombre);

        // Si la lista esta vacia
        if (tmp == null || tmp.isEmpty()) return false;

        // Ordenar ascendentemente antes de mostrar
        tmp.sort(Comparator.comparing(Juego::getNombre));

        // Mostrar resultados en la tabla
        juegosMostrados = tmp;
        crearTabla(juegosMostrados);

        return !tmp.isEmpty();
    }

    private boolean buscarPlataforma(String nombre) {
        List<Juego> tmp = DataManager.selectGameByPlatform(nombre);

        // Si la lista esta vacia
        if (tmp == null || tmp.isEmpty()) return false;

        // Ordenar ascendentemente antes de mostrar
        tmp.sort(Comparator.comparing(Juego::getPlataforma));

        // Mostrar resultados en la tabla
        juegosMostrados = tmp;
        crearTabla(juegosMostrados);

        return !tmp.isEmpty();
    }

    private boolean buscar(String nombre) {
        // Obtener criterio de busqueda
        String filtro = (String) filtroComboBox.getSelectedItem();

        // Volver a bloquear botones de edicion y borrado
        General.bloquearBotones(juegoBotonPanel, false, anadirButton);

        // Busqueda segun criterio
        if (rb.getString("filter.name").equals(filtro)) {
            return buscarNombre(nombre);
        } else if (rb.getString("filter.platform").equals(filtro)) {
            return buscarPlataforma(nombre);
        }

        return false;
    }

    private void reiniciar() {
        busquedaEntrada.setText("");

        // Obtener registros
        juegos = DataManager.getGames();

        juegosMostrados = juegos;
        crearTabla(juegosMostrados);

        General.bloquearBotones(juegoBotonPanel, false, anadirButton);
    }

    // Seguramente pulsando a algo de la cabecera podria filtrar por asc desc en base a esa columna?
    // Anadir icono del programa
    // Estaria mejor que si el juego existe, te deje cambiar en lugar de cerrar la ventana de crear/ver
    // Anadir documentacion con javadoc
    // Exportar a CSV que no deberia de ser muy dificil
    // Validaciones??
    // Personalizacion?? Aunque eso es lo de menos
    // Algunas partes del codigo (obviando comentarios) estan en un idioma y otras en otro, pasar todas al ingles
    // Seguir con la internacionalizacion (ESTOY CON ESTO)
    // Si el juego ya existe en editar, lo deja pasar, deberia comprobar si existe en la base de datos directamente
}

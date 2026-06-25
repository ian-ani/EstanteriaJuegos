package ui;

import entity.Estado;
import entity.Juego;
import entity.Valoracion;
import ui.dialog.DetalleJuego;
import util.General;
import util.Mensaje;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
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
    private JLabel busquedaLabel;
    private JTable tabla;
    private JPanel juegoBotonPanel;
    private JPanel persistenciaPanel;
    private JButton Guardar;
    private JButton reiniciarButton;

    // Juego seleccionado
    Juego juegoSeleccionado;

    // Lista donde se van a guardar los juegos temporalmente
    List<Juego> juegos = new ArrayList<>();
    List<Juego> juegosMostrados = new ArrayList<>();

    /* CONSTRUCTOR */

    public VentanaPrincipal() {
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
                //crearTabla(juegos);
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
                                "No encontrado",
                                "No se ha encontrado ningún juego con ese nombre."
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
                if (borrado) Mensaje.mostrarMensajeInfo("Juego borrado", "Juego borrado correctamente.");
                else Mensaje.mostrarMensajeError("Juego no borrado", "No se ha podido borrar el juego seleccionado.");

                // Volver a mostrar la tabla
                juegosMostrados = juegos;
                crearTabla(juegosMostrados);

                // Bloquear botones
                General.bloquearBotones(juegoBotonPanel, false, anadirButton);
            }
        });
    }

    /* GETTERS */

    public JPanel getPanelGeneral() {
        return panelGeneral;
    }

    /* OTROS METODOS */

    private void init() {
        // TODO obtener registros desde la base de datos

        // TODO borrar test
        test();

        juegosMostrados = juegos;

        // Propiedades
        crearTabla(juegos);

        // Bloquear botones de eliminar y editar hasta que haya un juego seleccionado
        General.bloquearBotones(juegoBotonPanel, false, anadirButton);
    }

    // TODO borrar luego!!
    private void test() {
        juegos.add(new Juego(0, "Diablo III", "PlayStation 3", Estado.COMPLETADO,
                Valoracion.GUSTADO, "Cosas."));
        juegos.get(0).anadirEtiqueta("arpg");

        juegos.add(new Juego(0,"The Longest Journey", "PC", Estado.COMPLETADO,
                Valoracion.GUSTADO, "Comentario muuuuuuuuuuuuuuuuuuuuuy largo."));
        juegos.get(1).anadirEtiqueta("point n click");
        juegos.get(1).anadirEtiqueta("puzzles");

        juegos.add(new Juego(0,"Ninja Gaiden Sigma", "PlayStation 3", Estado.PENDIENTE,
                Valoracion.NO_VALORADO, "Cosas."));
        juegos.get(2).anadirEtiqueta("hack n slash");
    }

    private void crearTabla(List<Juego> lista) {
        juegoSeleccionado = null;

        Object[][] datos = new Object[lista.size()][6];
        String[] columnas = new String[]{"Nombre", "Plataforma", "Estado", "Etiquetas", "Valoración", "Comentarios"};

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

        detalleJuegoWindow.setTitle("Añadir juego...");
        detalleJuegoWindow.setSize(kANCHO_VENTANA, kALTO_VENTANA);
        detalleJuegoWindow.setResizable(false);
        detalleJuegoWindow.setLocationRelativeTo(null);

        // callback
        detalleJuegoWindow.setOnJuegoCreado((juego) -> {
                if (!juegos.contains(juego)) {
                    juegos.add(juego);
                    juegosMostrados = juegos;
                    crearTabla(juegosMostrados);
                }
            }
        );

        detalleJuegoWindow.setVisible(true);
    }

    private boolean eliminar() {
        // Traducir texto de los botoners de 'SI' y 'NO'
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");

        // Mostrar una ventana de confirmacion
        int respuesta = JOptionPane.showConfirmDialog(
                null,
                "¿Estás seguro de querer borrar el juego " + juegoSeleccionado.getNombre() + "?",
                "Confirmar borrado",
                JOptionPane.YES_NO_OPTION
        );

        // Si la respuesta es 'SI', entonces borrar el juego actual
        if (respuesta == JOptionPane.YES_OPTION) {
            // TODO esto luego hay que cambiarlo incluyendo el DataManager
            try {
                // Borrar juego
                juegos.remove(juegoSeleccionado);

                // Desactivar boton de eliminar y editar
                //General.bloquearBotones(juegoBotonPanel, false, anadirButton);

                return true;
            } catch (Exception e) {
                System.err.println("No se ha podido borrar el juego seleccionado: " + e.getMessage());
                return false;
            }
        }

        return false;
    }

    private void ver() {
        DetalleJuego detalleJuegoWindow = new DetalleJuego(juegos, juegoSeleccionado, DetalleJuego.Modo.VER);

        detalleJuegoWindow.setTitle("Ver juego...");
        detalleJuegoWindow.setSize(kANCHO_VENTANA, kALTO_VENTANA);
        detalleJuegoWindow.setResizable(false);
        detalleJuegoWindow.setLocationRelativeTo(null);

        detalleJuegoWindow.setVisible(true);

        //juegosMostrados = juegos;
        //crearTabla(juegosMostrados);
    }

    private boolean buscarNombre(String nombre) {
        // TODO luego se pasa a DataManager
        List<Juego> tmp = new ArrayList<>();

        // Si la lista esta vacia
        //if (tmp == null || tmp.isEmpty()) return false;

        for (Juego j: juegos) {
            if (j.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                tmp.add(j);
            }
        }

        // Ordenar ascendentemente antes de mostrar
        tmp.sort(Comparator.comparing(Juego::getNombre));

        // Mostrar resultados en la tabla
        juegosMostrados = tmp;
        crearTabla(juegosMostrados);

        return !tmp.isEmpty();
    }

    private boolean buscar(String nombre) {
        // TODO se pueden anadir busquedas por mas campos, no es dificil, lo puedo hacer como el ejercicio del hospital

        // Volver a bloquear botones de edicion y borrado
        General.bloquearBotones(juegoBotonPanel, false, anadirButton);
        // TODO no se si esto va a ser realmente necesario en este caso

        // Buscar por nombre
        // TODO poner mas filtros y separar con condicionales
        return buscarNombre(nombre);
    }

    private void reiniciar() {
        busquedaEntrada.setText("");

        juegosMostrados = juegos;
        crearTabla(juegosMostrados);

        General.bloquearBotones(juegoBotonPanel, false, anadirButton);
    }

    // Guardar juegos en archivo para persistencia!
    // Anadir busqueda por mas campos
    // Seguramente pulsando a algo de la cabecera podria filtrar por asc desc en base a esa columna?
    // Anadir icono del programa
    // Borrar los sout, poner un logger cuando haya base de datos
    // Pasar nombres al ingles :) lo mismo podria poner un archivo de localizacion tambien!
    // Pasar la logica a 'service', dejar aqui solo la de presentacion como crearTabla, bloquearBotones, etc
    // y pasar a service la creacion, edicion, etc y llamarla desde aqui
    // Anadir documentacion con javadoc
}

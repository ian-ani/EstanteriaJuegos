package ui;

import modelo.Estado;
import modelo.Juego;
import modelo.Valoracion;
import utiles.Mensaje;

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

    // Ventana secundaria
    //private DetalleJuego detalleJuegoWindow;

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

    /* CONSTRUCTOR */

    public VentanaPrincipal() {
        // TODO borrar test
        test();

        crearTabla(juegos);

        // Llama a la ventana de 'anadir juego'
        anadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anadir();
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
                    juegoSeleccionado = juegos.get(filaModelo);
                    eliminarButton.setEnabled(true);
                    verButton.setEnabled(true);
                }
            }
        });

        // Busca un juego (por nombre) al pulsar 'Enter'
        busquedaEntrada.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    boolean encontrado = buscar(busquedaEntrada.getText());

                    if (!encontrado) Mensaje.mostrarMensajeError(
                            "No encontrado",
                            "No se ha encontrado ningún juego con ese nombre."
                    );
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
                crearTabla(juegos);
            }
        });

        // Ver registro seleccionado y permite editar en la nueva ventana de dialogo
        verButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ver();

                // Volver a mostrar la tabla
                crearTabla(juegos);
            }
        });
    }

    /* GETTERS */

    public JPanel getPanelGeneral() {
        return panelGeneral;
    }

    /* OTROS METODOS */

    // TODO borrar luego!!
    private void test() {
        juegos.add(new Juego("Diablo III", "PlayStation 3", Estado.COMPLETADO,
                Valoracion.GUSTADO, "Cosas."));
        juegos.get(0).anadirEtiqueta("arpg");

        juegos.add(new Juego("The Longest Journey", "PC", Estado.COMPLETADO,
                Valoracion.GUSTADO, "Comentario muuuuuuuuuuuuuuuuuuuuuy largo."));
        juegos.get(1).anadirEtiqueta("point n click");
        juegos.get(1).anadirEtiqueta("puzzles");

        juegos.add(new Juego("Ninja Gaiden Sigma", "PlayStation 3", Estado.PENDIENTE,
                Valoracion.NO_VALORADO, "Cosas."));
        juegos.get(2).anadirEtiqueta("hack n slash");
    }

    private void crearTabla(List<Juego> lista) {
        // Matriz de datos para guardar los juegos
        Object[][] datos = new Object[lista.size()][6];

        // Fila
        for (int i = 0; i < datos.length; i++) {
            // Columna
            for (int j = 0; j < datos[0].length; j++) {
                datos[i][0] = lista.get(i).getNombre();
                datos[i][1] = lista.get(i).getPlataforma();
                datos[i][2] = lista.get(i).getEstado();
                datos[i][3] = lista.get(i).getEtiquetas();
                datos[i][4] = lista.get(i).getValoracion();
                datos[i][5] = lista.get(i).getNotas();
            }
        }

        // Crear tabla: datos y cabecera
        tabla.setModel(new DefaultTableModel(
                datos,
                new String[]{
                        "Nombre",
                        "Plataforma",
                        "Estado",
                        "Etiquetas",
                        "Valoración",
                        "Comentarios",
                }
        ));
    }

    private void anadir() {
        DetalleJuego detalleJuegoWindow = new DetalleJuego(null, DetalleJuego.Modo.CREAR);

        detalleJuegoWindow.setTitle("Añadir juego...");
        detalleJuegoWindow.setSize(kANCHO_VENTANA, kALTO_VENTANA);
        detalleJuegoWindow.setResizable(false);
        detalleJuegoWindow.setLocationRelativeTo(null);

        detalleJuegoWindow.setOnJuegoCreado((juego) -> {
                juegos.add(juego);
                crearTabla(juegos);
            }
        );

        detalleJuegoWindow.setVisible(true);
    }

    private boolean eliminar() {
        try {
            // Borrar juego
            juegos.remove(juegoSeleccionado);

            // Desactivar boton de eliminar
            eliminarButton.setEnabled(false);

            return true;
        } catch (Exception e) {
            System.err.println("No se ha podido borrar el juego seleccionado: " + e.getMessage());
            return false;
        }
    }

    private void ver() {
        DetalleJuego detalleJuegoWindow = new DetalleJuego(juegoSeleccionado, DetalleJuego.Modo.VER);

        detalleJuegoWindow.setTitle("Ver juego...");
        detalleJuegoWindow.setSize(kANCHO_VENTANA, kALTO_VENTANA);
        detalleJuegoWindow.setResizable(false);
        detalleJuegoWindow.setLocationRelativeTo(null);

        detalleJuegoWindow.setVisible(true);
    }

    private boolean buscar(String nombre) {
        List<Juego> tmp = new ArrayList<>();

        for (Juego j: juegos) {
            if (j.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                tmp.add(j);
            }
        }

        // Ordenar ascendentemente antes de mostrar
        tmp.sort(Comparator.comparing(Juego::getNombre));

        // Muestra de nuevo la tabla con los datos coincidentes
        crearTabla(tmp);

        return !tmp.isEmpty();
    }

    private void reiniciar() {
        busquedaEntrada.setText("");
        crearTabla(juegos);
    }

    // Guardar juegos en archivo para persistencia!
    // Anadir busqueda por mas campos
    // Seguramente pulsando a algo de la cabecera podria filtrar por asc desc en base a esa columna?
    // Anadir icono del programa
    // Si no hay nada seleccionado, deberian bloquearse 'borrar' y 'ver', eso sigue mal
}

package ui;

import modelo.Estado;
import modelo.Juego;
import modelo.Valoracion;
import utiles.Mensaje;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static config.UIConstantes.*;

public class VentanaPrincipal {
    /* ATRIBUTOS */

    // Ventana secundaria
    private DetalleJuego detalleJuegoWindow;

    // Variables de GestionJuegos
    private JPanel panelGeneral;
    private JPanel busquedaPanel;
    private JPanel juegoPanel;
    private JPanel botonesPanel;
    private JButton eliminarButton;
    private JButton editarButton;
    private JButton anadirButton;
    private JScrollPane tablaPanel;
    private JTextField busquedaEntrada;
    private JLabel busquedaLabel;
    private JTable tabla;
    private JPanel juegoBotonPanel;
    private JPanel persistenciaPanel;
    private JButton Guardar;
    private JButton reiniciarButton;

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
        if (detalleJuegoWindow == null) {
            detalleJuegoWindow = new DetalleJuego();

            detalleJuegoWindow.setTitle("Añadir juego...");

            detalleJuegoWindow.setSize(kANCHO_VENTANA, kALTO_VENTANA);

            detalleJuegoWindow.setResizable(false);

            detalleJuegoWindow.setLocationRelativeTo(null);
        }

        detalleJuegoWindow.setOnJuegoCreado((juego) -> {
                juegos.add(juego);
                crearTabla(juegos);
            }
        );

        detalleJuegoWindow.setVisible(true);
    }

    private void eliminar() {
        // Borrar juego seleccionado
    }

    private void editar() {

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
    // Permitir exportar? si eso al final
    // Editar debe abrir una nueva ventana
    // En principio permitir buscar solo por nombre, luego podria anadir busqueda por mas campos
    // Seguramente pulsando a algo de la cabecera podria filtrar por asc desc en base a esa columna?
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GestionJuegos {
    private DetalleJuego detalleJuegoWindow;

    private JPanel panelGeneral;
    private JLabel tituloLabel;
    private JPanel tituloPanel;
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

    ArrayList<Juego> juegos = new ArrayList<>();

    public static void main(String[] args) {
        // Clase de la GUI
        JFrame frame = new JFrame("Biblioteca de juegos");

        // Tamano de la ventana
        frame.setSize(800, 600);

        frame.setContentPane(new GestionJuegos().panelGeneral);

        // Para poder cerrar el programa
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame.pack();

        // Para que no se pueda redimensionar
        frame.setResizable(false);

        // Centra la ventana
        frame.setLocationRelativeTo(null);

        // Muestra la ventana
        frame.setVisible(true);
    }

    public GestionJuegos() {
        crearTabla();
        anadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anadir();
            }
        });
    }

    private void crearTabla() {
        // Matriz de datos para guardar los juegos
        Object[][] datos = new Object[juegos.size()][6];

        // Fila
        for (int i = 0; i < datos.length; i++) {
            // Columna
            for (int j = 0; j < datos[0].length; j++) {
                datos[i][0] = juegos.get(i).getNombre();
                datos[i][1] = juegos.get(i).getPlataforma();
                datos[i][2] = juegos.get(i).getEstado();
                datos[i][3] = juegos.get(i).getEtiquetas();
                datos[i][4] = juegos.get(i).getValoracion();
                datos[i][5] = juegos.get(i).getNotas();
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

            detalleJuegoWindow.setSize(800, 400);

            detalleJuegoWindow.setResizable(false);

            detalleJuegoWindow.setLocationRelativeTo(null);
        }

        detalleJuegoWindow.setVisible(true);
    }

    private void eliminar() {
        // Borrar juego seleccionado
    }

    private void editar() {

    }

    // Guardar juegos en archivo para persistencia!
    // Permitir exportar? si eso al final
    // Editar debe abrir una nueva ventana
    // En principio permitir buscar solo por nombre, luego podria anadir busqueda por mas campos
    // Seguramente pulsando a algo de la cabecera podria filtrar por asc desc en base a esa columna?
}

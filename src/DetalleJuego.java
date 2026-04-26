import javax.swing.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class DetalleJuego extends JDialog {
    private Set<String> etiquetasTmp = new HashSet<>();

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
    }

    private void onOK() {
        // TODO intentar anadir juego
        // TODO es decir, la creacion del objeto

        nombreEntrada.getText();
        plataformaEntrada.getText();
        // estado es un radio
        // etiquetas debe ser un text field con tags/chips
        // valoracion es un radio
        notasEntrada.getText();
    }

    private void onCancel() {
        // Cerrar ventana
        dispose();
    }

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

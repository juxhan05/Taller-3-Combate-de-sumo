package pa.taller3.servidor.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class PanelLuchador extends JPanel {

    private static final Color C_FONDO = new Color(12, 8, 6);
    private static final Color C_SUPERFICIE = new Color(22, 14, 10);
    private static final Color C_DORADO = new Color(212, 175, 55);
    private static final Color C_DORADO_SUV = new Color(140, 110, 35);
    private static final Color C_MARFIL = new Color(245, 235, 210);
    private static final Color C_GRIS = new Color(120, 105, 90);
    private static final Color C_VERDE = new Color(80, 160, 80);
    private static final Color C_ORO_VIC = new Color(212, 175, 55);

    private final Color colorAcento;
    private final String rutaGif;
    private String nombreActual;

    // Componentes
    private JLabel lblTitulo;
    private JLabel lblNombre;
    private JLabel lblPeso;
    private JLabel lblVictorias;
    private JLabel lblEstado;
    private JLabel lblGif;
    private JPanel panelGif;
    private JPanel panelDatos;

    /**
     * Construye el panel con titulo, color de acento y ruta del GIF.
     *
     * @param titulo Titulo del panel.
     * @param colorAcento Color de acento para el borde superior.
     * @param rutaGif Ruta al GIF animado del luchador.
     */
    public PanelLuchador(String titulo, Color colorAcento, String rutaGif) {
        this.colorAcento = colorAcento;
        this.rutaGif = rutaGif;
        setLayout(new BorderLayout(0, 0));
        setBackground(C_FONDO);
        setPreferredSize(new Dimension(220, 380));
        inicializar(titulo);
    }

    private void inicializar(String titulo) {
        // Borde superior de color acento
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, colorAcento),
                new EmptyBorder(0, 0, 0, 0)
        ));

        // Panel datos (siempre visible)
        panelDatos = new JPanel(new GridLayout(5, 1, 0, 6));
        panelDatos.setBackground(C_FONDO);
        panelDatos.setBorder(new EmptyBorder(14, 16, 14, 16));

        lblTitulo = new JLabel("▪  " + titulo, SwingConstants.LEFT);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitulo.setForeground(colorAcento);

        lblNombre = crearDato("Esperando luchador...", C_MARFIL,
                new Font("Serif", Font.BOLD, 15));
        lblPeso = crearDato("Peso: —", C_GRIS, new Font("SansSerif", Font.PLAIN, 11));
        lblVictorias = crearDato("Victorias: —", C_GRIS, new Font("SansSerif", Font.PLAIN, 11));
        lblEstado = crearDato("⏳  En espera", C_DORADO_SUV,
                new Font("SansSerif", Font.ITALIC, 11));

        panelDatos.add(lblTitulo);
        panelDatos.add(lblNombre);
        panelDatos.add(lblPeso);
        panelDatos.add(lblVictorias);
        panelDatos.add(lblEstado);

        // Panel GIF (oculto hasta que inicie el combate)
        panelGif = new JPanel(new BorderLayout());
        panelGif.setBackground(C_FONDO);
        panelGif.setVisible(false);

        lblGif = new JLabel("", SwingConstants.CENTER);
        lblGif.setPreferredSize(new Dimension(220, 200));
        lblGif.setBackground(C_FONDO);
        lblGif.setOpaque(true);

        // Separador dorado
        JPanel sep = new JPanel();
        sep.setBackground(C_DORADO);
        sep.setPreferredSize(new Dimension(0, 1));

        panelGif.add(sep, BorderLayout.NORTH);
        panelGif.add(lblGif, BorderLayout.CENTER);

        add(panelDatos, BorderLayout.NORTH);
        add(panelGif, BorderLayout.CENTER);
    }

    private JLabel crearDato(String texto, Color color, Font fuente) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        return lbl;
    }

    // ── API publica ───────────────────────────────────────────────────────────
    /**
     * Carga y muestra los datos de un luchador.
     *
     * @param luchador Luchador a mostrar.
     */
    public void setLuchador(String nombre, double peso, int victorias) {
        this.nombreActual = nombre;
        lblNombre.setText(nombre);
        lblPeso.setText("Peso: " + peso + " kg");
        lblVictorias.setText("Victorias: " + victorias);
        lblEstado.setText("✓  En el Dohyo");
        lblEstado.setForeground(C_VERDE);
        repaint();
    }

    /**
     * Muestra el GIF animado al iniciar el combate.
     */
    public void mostrarGif() {
        cargarGif();
        panelGif.setVisible(true);
        revalidate();
        repaint();
    }

    private void cargarGif() {
        try {
            java.net.URL url = getClass().getClassLoader().getResource(rutaGif);
            ImageIcon icon;
            if (url != null) {
                icon = new ImageIcon(url);
            } else {
                java.io.File f = new java.io.File(rutaGif);
                if (f.exists()) {
                    icon = new ImageIcon(rutaGif);
                } else {
                    lblGif.setText("🥋");
                    lblGif.setFont(new Font("SansSerif", Font.PLAIN, 60));
                    lblGif.setForeground(C_MARFIL);
                    return;
                }
            }
            Image img = icon.getImage().getScaledInstance(200, 190, Image.SCALE_DEFAULT);
            lblGif.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblGif.setText("🥋");
            lblGif.setFont(new Font("SansSerif", Font.PLAIN, 60));
            lblGif.setForeground(C_MARFIL);
        }
    }

    /**
     * Resalta el panel cuando el luchador ataca.
     *
     * @param fueExitoso true si saco al rival.
     */
    public void resaltarAtaque(boolean fueExitoso) {
        setBackground(fueExitoso ? new Color(60, 10, 10) : C_FONDO);
        repaint();
    }

    /**
     * Marca como ganador.
     */
    public void marcarGanador(int victorias) {
        setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, C_ORO_VIC));
        setBackground(new Color(30, 22, 5));
        lblEstado.setText("★  GANADOR");
        lblEstado.setForeground(C_DORADO);
        lblVictorias.setText("Victorias: " + victorias);
        repaint();
    }

    /**
     * Marca como perdedor.
     */
    public void marcarPerdedor() {
        setBackground(new Color(25, 8, 8));
        lblEstado.setText("✗  Fuera del Dohyo");
        lblEstado.setForeground(new Color(160, 60, 60));
        repaint();
    }

    /**
     * @return true si no tiene luchador asignado.
     */
    public boolean estaVacio() {
        return nombreActual == null;
    }

    /**
     * @param luchador Luchador a comparar.
     * @return true si este panel corresponde al luchador dado.
     */
    public boolean correspondeA(String nombre) {
        return nombreActual != null && nombreActual.equals(nombre);
    }
}

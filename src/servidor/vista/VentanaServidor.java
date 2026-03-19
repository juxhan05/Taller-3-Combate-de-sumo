package servidor.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class VentanaServidor extends JFrame {

    private static final Color C_FONDO = new Color(10, 6, 4);
    private static final Color C_SUPERFICIE = new Color(18, 12, 8);
    private static final Color C_ROJO = new Color(150, 18, 18);
    private static final Color C_DORADO = new Color(212, 175, 55);
    private static final Color C_DORADO_SUV = new Color(140, 110, 35);
    private static final Color C_MARFIL = new Color(245, 235, 210);
    private static final Color C_LOG_BG = new Color(8, 5, 3);
    private static final Color C_LOG_TEXT = new Color(160, 200, 130);

    private JLabel lblEstado;
    private PanelLuchador panelLuchador1;
    private PanelLuchador panelLuchador2;
    private JTextArea areaLog;
    private JLabel lblDohyoTexto;

    /**
     * Construye la ventana del servidor.
     */
    public VentanaServidor() {
        configurarVentana();
        construirUI();
    }

    private void configurarVentana() {
        setTitle("相撲  Dohyo — Servidor de Combate");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(C_FONDO);
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(C_FONDO);
        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        raiz.add(construirCentro(), BorderLayout.CENTER);
        raiz.add(construirLog(), BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private JPanel construirEncabezado() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_DORADO);
                g.fillRect(0, getHeight() - 2, getWidth(), 1);
                g.setColor(C_ROJO);
                g.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        panel.setBackground(new Color(16, 8, 6));
        panel.setBorder(new EmptyBorder(14, 28, 12, 28));

        JLabel kanji = new JLabel("相撲");
        kanji.setFont(new Font("Serif", Font.BOLD, 42));
        kanji.setForeground(C_ROJO);

        JPanel derecha = new JPanel(new GridLayout(2, 1, 0, 4));
        derecha.setBackground(new Color(16, 8, 6));

        JLabel titulo = new JLabel("SERVIDOR DOHYO — COMBATE DE SUMO");
        titulo.setFont(new Font("Serif", Font.BOLD, 20));
        titulo.setForeground(C_MARFIL);
        titulo.setHorizontalAlignment(SwingConstants.RIGHT);

        lblEstado = new JLabel("Esperando luchadores...", SwingConstants.RIGHT);
        lblEstado.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblEstado.setForeground(C_DORADO_SUV);

        derecha.add(titulo);
        derecha.add(lblEstado);

        panel.add(kanji, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirCentro() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(C_FONDO);

        panelLuchador1 = new PanelLuchador("LUCHADOR 1",
                new Color(65, 105, 180), "data/pelea_transparent.gif");
        panelLuchador2 = new PanelLuchador("LUCHADOR 2",
                new Color(180, 40, 40), "data/sumo_transparent.gif");

        JPanel panelDohyo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int r = Math.min(getWidth(), getHeight()) / 2 - 20;
                g2.setColor(new Color(40, 28, 18));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(C_DORADO_SUV);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                int r2 = r - 12;
                g2.setColor(new Color(C_ROJO.getRed(), C_ROJO.getGreen(),
                        C_ROJO.getBlue(), 60));
                g2.fillOval(cx - r2, cy - r2, r2 * 2, r2 * 2);
                g2.setColor(C_ROJO);
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(cx - r2, cy - r2, r2 * 2, r2 * 2);
            }
        };
        panelDohyo.setBackground(C_FONDO);
        panelDohyo.setPreferredSize(new Dimension(360, 380));

        lblDohyoTexto = new JLabel(
                "<html><center><font color='#D4AF37' size='5'>DOHYO</font><br>"
                + "<font color='#786050' size='2'>Esperando luchadores</font></center></html>",
                SwingConstants.CENTER);
        panelDohyo.add(lblDohyoTexto, BorderLayout.CENTER);

        panel.add(panelLuchador1, BorderLayout.WEST);
        panel.add(panelDohyo, BorderLayout.CENTER);
        panel.add(panelLuchador2, BorderLayout.EAST);
        return panel;
    }

    private JPanel construirLog() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_DORADO);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        panel.setBackground(C_SUPERFICIE);
        panel.setBorder(new EmptyBorder(8, 16, 8, 16));

        JLabel lbl = new JLabel("▪  LOG DEL COMBATE");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(C_DORADO);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));

        areaLog = new JTextArea(7, 0);
        areaLog.setEditable(false);
        areaLog.setBackground(C_LOG_BG);
        areaLog.setForeground(C_LOG_TEXT);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaLog.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createLineBorder(C_DORADO_SUV, 1));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── API publica — solo recibe Strings y primitivos ────────────────────────
    /**
     * Muestra la llegada de un luchador.
     *
     * @param nombre Nombre del luchador.
     * @param peso Peso del luchador.
     * @param victorias Victorias del luchador.
     */
    public void mostrarLuchadorLlego(String nombre, double peso, int victorias) {
        if (panelLuchador1.estaVacio()) {
            panelLuchador1.setLuchador(nombre, peso, victorias);
        } else {
            panelLuchador2.setLuchador(nombre, peso, victorias);
        }
        lblEstado.setText(nombre + " conectado. Esperando rival...");
        agregarLog("▸ " + nombre + " llego al Dohyo.");
    }

    /**
     * Muestra el inicio del combate y activa los GIFs.
     *
     * @param n1 Nombre del luchador 1.
     * @param n2 Nombre del luchador 2.
     */
    public void mostrarInicioCombate(String n1, String n2) {
        lblEstado.setText("⚔  TACHI-AI — El combate ha comenzado");
        lblEstado.setForeground(C_DORADO);
        lblDohyoTexto.setText(
                "<html><center><font color='#C81414' size='6'>⚔</font><br>"
                + "<font color='#D4AF37' size='4'>COMBATE</font><br>"
                + "<font color='#F5EBD2' size='2'>EN CURSO</font></center></html>");
        panelLuchador1.mostrarGif();
        panelLuchador2.mostrarGif();
        agregarLog("━━━━  TACHI-AI: " + n1 + " VS " + n2 + "  ━━━━");
    }

    /**
     * Muestra el resultado de un turno.
     *
     * @param nombreLuchador Nombre del luchador que ataco.
     * @param tecnica Nombre de la tecnica usada.
     * @param saco true si saco al rival.
     */
    public void mostrarTurno(String nombreLuchador, String tecnica, boolean saco) {
        String msg = saco
                ? "★ " + nombreLuchador + " usa [" + tecnica + "] → SACA AL RIVAL"
                : "  " + nombreLuchador + " usa [" + tecnica + "] → rival se mantiene";
        agregarLog(msg);
        if (panelLuchador1.correspondeA(nombreLuchador)) {
            panelLuchador1.resaltarAtaque(saco);
        } else {
            panelLuchador2.resaltarAtaque(saco);
        }
    }

    /**
     * Muestra al ganador con estilo destacado.
     *
     * @param nombreGanador Nombre del ganador.
     * @param victorias Total de victorias.
     */
    public void mostrarGanador(String nombreGanador, int victorias) {
        lblEstado.setText("★  " + nombreGanador.toUpperCase()
                + " GANA  |  Victorias: " + victorias);
        lblEstado.setForeground(C_DORADO);
        lblDohyoTexto.setText(
                "<html><center><font color='#D4AF37' size='5'>★</font><br>"
                + "<font color='#D4AF37' size='3'>" + nombreGanador + "</font><br>"
                + "<font color='#F5EBD2' size='2'>GANADOR</font></center></html>");
        if (panelLuchador1.correspondeA(nombreGanador)) {
            panelLuchador1.marcarGanador(victorias);
            panelLuchador2.marcarPerdedor();
        } else {
            panelLuchador2.marcarGanador(victorias);
            panelLuchador1.marcarPerdedor();
        }
        agregarLog("★  GANADOR: " + nombreGanador
                + "  |  Victorias totales: " + victorias);
    }

    /**
     * Registra que un cliente confirmo y se desconecto.
     *
     * @param nombre Nombre del luchador desconectado.
     */
    public void mostrarClienteDesconectado(String nombre) {
        agregarLog("✓ " + nombre + " recibio resultado y se desconecto.");
    }

    /**
     * Muestra un error en el log.
     *
     * @param mensaje Descripcion del error.
     */
    public void mostrarError(String mensaje) {
        agregarLog("✗ ERROR: " + mensaje);
    }

    private void agregarLog(String texto) {
        areaLog.append(texto + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
}

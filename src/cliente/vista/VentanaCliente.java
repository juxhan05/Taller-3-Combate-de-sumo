package cliente.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;

/**
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class VentanaCliente extends JFrame {

    // ── Paleta tradicional japonesa ───────────────────────────────────────────
    private static final Color C_FONDO = new Color(12, 8, 6);
    private static final Color C_SUPERFICIE = new Color(22, 14, 10);
    private static final Color C_ROJO = new Color(160, 20, 20);
    private static final Color C_ROJO_VIB = new Color(200, 35, 35);
    private static final Color C_DORADO = new Color(212, 175, 55);
    private static final Color C_DORADO_SUV = new Color(160, 130, 40);
    private static final Color C_MARFIL = new Color(245, 235, 210);
    private static final Color C_GRIS_SUV = new Color(120, 105, 90);
    private static final Color C_CAMPO = new Color(30, 20, 15);

    // ── Fuentes ───────────────────────────────────────────────────────────────
    private static final Font F_KANJI = new Font("Serif", Font.BOLD, 48);
    private static final Font F_TITULO = new Font("Serif", Font.BOLD, 22);
    private static final Font F_SECCION = new Font("Serif", Font.BOLD, 13);
    private static final Font F_LABEL = new Font("SansSerif", Font.BOLD, 11);
    private static final Font F_CAMPO = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font F_BOTON = new Font("Serif", Font.BOLD, 15);
    private static final Font F_ESTADO = new Font("SansSerif", Font.ITALIC, 11);

    // ── Campos ────────────────────────────────────────────────────────────────
    private JTextField txtNombre;
    private JSpinner spinnerPeso;
    private JList<String> listaKimarites;
    private DefaultListModel<String> modeloLista;
    private JLabel lblArchivo;
    private JButton btnCargar;
    private JButton btnCombatir;
    private JButton btnSalir;
    private JLabel lblEstado;
    private JPanel panelResultado;
    private JLabel lblResultadoTitulo;
    private JLabel lblResultadoDetalle;

    /**
     * Construye la ventana del cliente con estilo tradicional japones.
     */
    public VentanaCliente() {
        configurarVentana();
        construirUI();
    }

    private void configurarVentana() {
        setTitle("相撲 — Dohyo de Sumo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 860);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(C_FONDO);
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(C_FONDO);
        raiz.setBorder(new EmptyBorder(0, 0, 0, 0));

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        raiz.add(construirCuerpo(), BorderLayout.CENTER);
        raiz.add(construirPie(), BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    // ── Encabezado ────────────────────────────────────────────────────────────
    private JPanel construirEncabezado() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Linea dorada inferior decorativa
                g2.setColor(C_DORADO);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                // Linea roja debajo del dorado
                g2.setColor(C_ROJO);
                g2.fillRect(0, getHeight() - 4, getWidth(), 2);
            }
        };
        panel.setBackground(C_SUPERFICIE);
        panel.setBorder(new EmptyBorder(20, 30, 18, 30));

        // Kanji izquierda
        JLabel kanji = new JLabel("相撲");
        kanji.setFont(F_KANJI);
        kanji.setForeground(C_ROJO);

        // Titulo y subtitulo derecha
        JPanel txtPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        txtPanel.setBackground(C_SUPERFICIE);

        JLabel titulo = new JLabel("REGISTRO DE RIKISHI");
        titulo.setFont(F_TITULO);
        titulo.setForeground(C_MARFIL);
        titulo.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel subtitulo = new JLabel("Prepara tu luchador · Entra al Dohyo");
        subtitulo.setFont(F_ESTADO);
        subtitulo.setForeground(C_DORADO_SUV);
        subtitulo.setHorizontalAlignment(SwingConstants.RIGHT);

        txtPanel.add(titulo);
        txtPanel.add(subtitulo);

        panel.add(kanji, BorderLayout.WEST);
        panel.add(txtPanel, BorderLayout.CENTER);
        return panel;
    }

    // ── Cuerpo ────────────────────────────────────────────────────────────────
    private JPanel construirCuerpo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(C_FONDO);
        panel.setBorder(new EmptyBorder(12, 32, 8, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 12, 0);
        g.weightx = 1.0;
        g.gridx = 0;

        g.weighty = 0.0;
        g.gridy = 0;
        panel.add(construirFilaDato("NOMBRE DEL RIKISHI", txtNombre = crearCampo()), g);
        g.gridy = 1;
        panel.add(construirFilaSpinner(), g);
        g.weighty = 1.0;
        g.fill = GridBagConstraints.BOTH;
        g.gridy = 2;
        panel.add(construirFilaTecnicas(), g);

        return panel;
    }

    private JPanel construirFilaDato(String etiqueta, JTextField campo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(C_FONDO);

        JLabel lbl = crearLabel(etiqueta);
        campo.setBackground(C_CAMPO);
        campo.setForeground(C_MARFIL);
        campo.setCaretColor(C_DORADO);
        campo.setFont(F_CAMPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, C_DORADO_SUV),
                new EmptyBorder(8, 4, 8, 4)
        ));

        p.add(lbl, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JPanel construirFilaSpinner() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(C_FONDO);

        JLabel lbl = crearLabel("PESO (kg)");
        spinnerPeso = new JSpinner(new SpinnerNumberModel(90.0, 50.0, 300.0, 0.5));
        spinnerPeso.setFont(F_CAMPO);
        JComponent editor = spinnerPeso.getEditor();
        editor.setBackground(C_CAMPO);
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(C_CAMPO);
            ((JSpinner.DefaultEditor) editor).getTextField().setForeground(C_MARFIL);
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(F_CAMPO);
            ((JSpinner.DefaultEditor) editor).getTextField().setBorder(
                    new EmptyBorder(8, 4, 8, 4));
        }
        spinnerPeso.setBorder(new MatteBorder(0, 0, 1, 0, C_DORADO_SUV));
        spinnerPeso.setBackground(C_CAMPO);

        p.add(lbl, BorderLayout.NORTH);
        p.add(spinnerPeso, BorderLayout.CENTER);
        return p;
    }

    private JPanel construirFilaTecnicas() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(C_FONDO);
        p.setPreferredSize(new Dimension(0, 0));

        // Cabecera seccion
        JPanel cabecera = new JPanel(new BorderLayout(10, 0));
        cabecera.setBackground(C_FONDO);

        JLabel lbl = crearLabel("TECNICAS KIMARITE  —  Selecciona las que domina");

        btnCargar = new JButton("CARGAR .properties");
        btnCargar.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnCargar.setBackground(C_SUPERFICIE);
        btnCargar.setForeground(C_DORADO);
        btnCargar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_DORADO_SUV, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        btnCargar.setFocusPainted(false);
        btnCargar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        lblArchivo = new JLabel("Ningun archivo");
        lblArchivo.setFont(F_ESTADO);
        lblArchivo.setForeground(C_GRIS_SUV);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.setBackground(C_FONDO);
        btnPanel.add(lblArchivo);
        btnPanel.add(btnCargar);

        cabecera.add(lbl, BorderLayout.WEST);
        cabecera.add(btnPanel, BorderLayout.EAST);

        // Lista
        modeloLista = new DefaultListModel<>();
        listaKimarites = new JList<>(modeloLista);
        listaKimarites.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaKimarites.setBackground(C_CAMPO);
        listaKimarites.setForeground(C_MARFIL);
        listaKimarites.setFont(F_CAMPO);
        listaKimarites.setFixedCellHeight(38);

        JScrollPane scroll = new JScrollPane(listaKimarites);
        scroll.setMinimumSize(new Dimension(0, 200));
        scroll.setBorder(new MatteBorder(1, 0, 1, 0, C_DORADO_SUV));
        scroll.getViewport().setBackground(C_CAMPO);
        scroll.setBackground(C_FONDO);

        JLabel tip = new JLabel("Ctrl+Click para seleccion multiple");
        tip.setFont(new Font("SansSerif", Font.ITALIC, 10));
        tip.setForeground(C_GRIS_SUV);

        p.add(cabecera, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(tip, BorderLayout.SOUTH);
        return p;
    }

    // ── Pie ───────────────────────────────────────────────────────────────────
    private JPanel construirPie() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(C_DORADO);
                g2.fillRect(0, 0, getWidth(), 1);
            }
        };
        panel.setBackground(C_SUPERFICIE);
        panel.setBorder(new EmptyBorder(16, 32, 20, 32));

        // Boton principal
        btnCombatir = new JButton("⚔  ENTRAR AL DOHYO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(C_ROJO);
                } else if (getModel().isRollover()) {
                    g2.setColor(C_ROJO_VIB);
                } else {
                    g2.setColor(C_ROJO);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 4, 4));
                g2.setColor(C_DORADO);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 4, 4));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnCombatir.setFont(F_BOTON);
        btnCombatir.setForeground(C_MARFIL);
        btnCombatir.setOpaque(false);
        btnCombatir.setContentAreaFilled(false);
        btnCombatir.setBorderPainted(false);
        btnCombatir.setFocusPainted(false);
        btnCombatir.setPreferredSize(new Dimension(0, 52));
        btnCombatir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Estado
        lblEstado = new JLabel("Registra tu rikishi y entra al combate.", SwingConstants.CENTER);
        lblEstado.setFont(F_ESTADO);
        lblEstado.setForeground(C_GRIS_SUV);

        // Panel resultado (oculto)
        panelResultado = new JPanel(new GridLayout(2, 1, 0, 4));
        panelResultado.setBackground(C_SUPERFICIE);
        panelResultado.setVisible(false);

        lblResultadoTitulo = new JLabel("", SwingConstants.CENTER);
        lblResultadoTitulo.setFont(new Font("Serif", Font.BOLD, 30));

        lblResultadoDetalle = new JLabel("", SwingConstants.CENTER);
        lblResultadoDetalle.setFont(F_ESTADO);
        lblResultadoDetalle.setForeground(C_GRIS_SUV);

        panelResultado.add(lblResultadoTitulo);
        panelResultado.add(lblResultadoDetalle);

        // Boton Salir (oculto hasta que termine el combate)
        btnSalir = new JButton("SALIR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(80, 60, 20) : new Color(40, 30, 10));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 4, 4));
                g2.setColor(C_DORADO_SUV);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 4, 4));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnSalir.setFont(new Font("Serif", Font.BOLD, 13));
        btnSalir.setForeground(C_DORADO);
        btnSalir.setOpaque(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setPreferredSize(new Dimension(0, 36));
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setVisible(false);

        JPanel botonesPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        botonesPanel.setBackground(C_SUPERFICIE);
        botonesPanel.add(btnCombatir);
        botonesPanel.add(btnSalir);

        panel.add(botonesPanel, BorderLayout.NORTH);
        panel.add(lblEstado, BorderLayout.CENTER);
        panel.add(panelResultado, BorderLayout.SOUTH);
        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_DORADO);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private JTextField crearCampo() {
        JTextField f = new JTextField();
        f.setBackground(C_CAMPO);
        f.setForeground(C_MARFIL);
        f.setCaretColor(C_DORADO);
        f.setFont(F_CAMPO);
        f.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, C_DORADO_SUV),
                new EmptyBorder(8, 4, 8, 4)
        ));
        return f;
    }

    // ── API publica ───────────────────────────────────────────────────────────
    /**
     * Registra listener del boton cargar properties.
     */
    public void agregarListenerCargarProperties(ActionListener l) {
        btnCargar.addActionListener(l);
    }

    /**
     * Registra listener del boton combatir.
     */
    public void agregarListenerCombatir(ActionListener l) {
        btnCombatir.addActionListener(l);
    }

    /**
     * Registra listener del boton salir.
     */
    public void agregarListenerSalir(ActionListener l) {
        btnSalir.addActionListener(l);
    }

    /**
     * @return Nombre ingresado.
     */
    public String getNombreLuchador() {
        return txtNombre.getText().trim();
    }

    /**
     * @return Peso seleccionado.
     */
    public double getPesoLuchador() {
        return (Double) spinnerPeso.getValue();
    }

    /**
     * @return Kimarites seleccionados.
     */
    public List<String> getKimaritesSeleccionados() {
        return listaKimarites.getSelectedValuesList();
    }

    /**
     * Carga kimarites en la lista.
     */
    public void cargarKimaritesEnLista(List<String> nombresKimarites) {
        modeloLista.clear();
        nombresKimarites.forEach(modeloLista::addElement);
    }

    /**
     * Actualiza la etiqueta del archivo seleccionado.
     */
    public void setArchivoProperties(String nombre) {
        lblArchivo.setText(nombre);
        lblArchivo.setForeground(C_DORADO);
    }

    /**
     * Actualiza el mensaje de estado.
     */
    public void setEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    /**
     * Muestra el resultado final del combate y activa boton Salir.
     */
    public void mostrarResultado(boolean gano, String nombreGanador, int victorias) {
        btnCombatir.setEnabled(false);
        btnSalir.setVisible(true);
        panelResultado.setVisible(true);
        if (gano) {
            lblResultadoTitulo.setText("★  VICTORIA  ★");
            lblResultadoTitulo.setForeground(C_DORADO);
        } else {
            lblResultadoTitulo.setText("DERROTA");
            lblResultadoTitulo.setForeground(C_ROJO);
        }
        lblResultadoDetalle.setText("Ganador: " + nombreGanador + "  |  Victorias: " + victorias);
        revalidate();
        repaint();
    }

    /**
     * Habilita o deshabilita el boton combatir.
     */
    public void setBtnCombatirHabilitado(boolean h) {
        btnCombatir.setEnabled(h);
    }

    /**
     * Muestra mensaje de advertencia.
     */
    public void mostrarMensaje(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Abre selector de archivo .properties.
     */
    public File seleccionarArchivoProperties() {
        JFileChooser fc = new JFileChooser("./data");
        fc.setDialogTitle("Seleccionar kimarites (.properties)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de propiedades (*.properties)", "properties"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }
}

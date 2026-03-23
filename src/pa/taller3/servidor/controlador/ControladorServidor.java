package pa.taller3.servidor.controlador;

import pa.taller3.servidor.dao.ConexionDB;
import pa.taller3.servidor.dao.IRikishiDAO;
import pa.taller3.servidor.dao.RikishiDAOImpl;
import pa.taller3.servidor.modelo.Dohyo;
import pa.taller3.servidor.modelo.ResultadoTurno;
import pa.taller3.servidor.modelo.Rikishi;
import pa.taller3.servidor.vista.VentanaServidor;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Controlador principal del servidor de sumo.
 *
 * <p>Inicializa la BD via Singleton, crea el DAO e inyecta
 * todo al ServidorSumo.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class ControladorServidor implements EventoCombateListener {

    /** Vista del servidor. */
    private final VentanaServidor vista;

    /**
     * El dohyō donde ocurre el combate.
     * No es final: solo se asigna si la BD conecta correctamente.
     */
    private Dohyo dohyo;

    /**
     * El servidor de sockets.
     * No es final: solo se asigna si la BD conecta correctamente.
     */
    private ServidorSumo servidor;

    /**
     * DAO de luchadores.
     * No es final: solo se asigna si la BD conecta correctamente.
     */
    private IRikishiDAO rikishiDAO;

    /**
     * Construye el controlador, inicializa la BD y arranca el servidor.
     */
    public ControladorServidor() {
        this.vista = new VentanaServidor();
        this.vista.setVisible(true);

        String rutaProperties = seleccionarProperties();
        if (rutaProperties == null) {
            vista.mostrarError("No se seleccionó archivo de configuración.");
            return;
        }

        try {
            ConexionDB.getInstance(rutaProperties);
            this.rikishiDAO = new RikishiDAOImpl(ConexionDB.getInstance());
            vista.mostrarLog("✅ Conexión a base de datos establecida.");
        } catch (RuntimeException e) {
            vista.mostrarError("Error al conectar con la BD: " + e.getMessage());
            return;
        }

        this.dohyo    = new Dohyo();
        this.servidor = new ServidorSumo(dohyo, this, rikishiDAO);
        servidor.setDaemon(false);
        servidor.start();
        vista.mostrarLog("🌐 Servidor iniciado en puerto " + Protocolo.PUERTO);
    }

    /**
     * Abre JFileChooser para seleccionar db.properties.
     *
     * @return ruta del archivo o null si canceló
     */
    private String seleccionarProperties() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar db.properties");
        chooser.setFileFilter(
            new FileNameExtensionFilter("Archivos .properties", "properties"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    @Override
    public void onLuchadorLlego(Rikishi luchador) {
        String nombre = luchador.getNombre();
        double peso   = luchador.getPeso();
        int vic       = luchador.getVictorias();
        SwingUtilities.invokeLater(() -> vista.mostrarLuchadorLlego(nombre, peso, vic));
    }

    @Override
    public void onCombateInicia(Rikishi luchador1, Rikishi luchador2) {
        String n1 = luchador1.getNombre();
        String n2 = luchador2.getNombre();
        SwingUtilities.invokeLater(() -> vista.mostrarInicioCombate(n1, n2));
    }

    @Override
    public void onTurnoEjecutado(Rikishi luchador, ResultadoTurno resultado) {
        String nombre  = luchador.getNombre();
        String tecnica = resultado.getTecnicaUsada() != null
            ? resultado.getTecnicaUsada().getNombre() : "???";
        boolean saco   = resultado.isSacoAlOponente();
        SwingUtilities.invokeLater(() -> vista.mostrarTurno(nombre, tecnica, saco));
    }

    @Override
    public void onCombateTermino(Rikishi ganador) {
        String nombre = ganador.getNombre();
        int vic       = ganador.getVictorias();
        SwingUtilities.invokeLater(() -> vista.mostrarGanador(nombre, vic));
    }

    @Override
    public void onClienteConfirmo(Rikishi luchador) {
        String nombre = luchador.getNombre();
        SwingUtilities.invokeLater(() -> vista.mostrarClienteDesconectado(nombre));
    }

    @Override
    public void onError(String mensaje) {
        SwingUtilities.invokeLater(() -> vista.mostrarError(mensaje));
    }
}

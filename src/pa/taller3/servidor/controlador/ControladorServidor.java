package pa.taller3.servidor.controlador;

import pa.taller3.servidor.dao.ConexionDB;
import pa.taller3.servidor.dao.IRikishiDAO;
import pa.taller3.servidor.dao.RikishiDAOImpl;
import pa.taller3.servidor.modelo.Dohyo;
import pa.taller3.servidor.modelo.ResultadoTurno;
import pa.taller3.servidor.modelo.Rikishi;
import pa.taller3.servidor.vista.VentanaServidor;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Controlador principal del servidor de sumo.
 *
 * <p>
 * Implementa {@link EventoCombateListener} para recibir eventos desde los hilos
 * de red y actualizarlos <b>de forma segura</b> en la GUI usando
 * {@code SwingUtilities.invokeLater()}.</p>
 *
 * <p>
 * Responsabilidades de inicialización:</p>
 * <ol>
 * <li>Pide al usuario seleccionar {@code db.properties} con
 * {@link JFileChooser}.</li>
 * <li>Inicializa el Singleton {@link ConexionDB} con esa ruta.</li>
 * <li>Crea el {@link RikishiDAOImpl} inyectándole la conexión.</li>
 * <li>Crea el {@link Dohyo} y el {@link ServidorSumo}, inyectándoles el
 * DAO.</li>
 * </ol>
 *
 * <p>
 * <b>SOLID - D:</b> Depende de {@link IRikishiDAO} y
 * {@link EventoCombateListener} (abstracciones), no de implementaciones.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class ControladorServidor implements EventoCombateListener {

    /**
     * Vista del servidor.
     */
    private final VentanaServidor vista;

    /**
     * El dohyō donde ocurre el combate. No es final: solo se asigna si la BD
     * conecta correctamente.
     */
    private Dohyo dohyo;

    /**
     * El servidor de sockets. No es final: solo se asigna si la BD conecta
     * correctamente.
     */
    private ServidorSumo servidor;

    /**
     * DAO de luchadores (abstracción). No es final: solo se asigna si la BD
     * conecta correctamente.
     */
    private IRikishiDAO rikishiDAO;

    /**
     * Construye el controlador:
     * <ol>
     * <li>Muestra la ventana.</li>
     * <li>Pide seleccionar {@code db.properties} via {@link JFileChooser}.</li>
     * <li>Inicializa la BD y el DAO.</li>
     * <li>Arranca el {@link ServidorSumo} con el DAO inyectado.</li>
     * </ol>
     */
    public ControladorServidor() {
        // 1. Mostrar la vista
        this.vista = new VentanaServidor();
        this.vista.setVisible(true);

        // 2. Pedir ruta de db.properties con JFileChooser
        String rutaProperties = seleccionarProperties();
        if (rutaProperties == null) {
            vista.mostrarError("No se seleccionó archivo de configuración. El servidor no puede iniciar.");
            return;
        }

        // 3. Inicializar el Singleton de BD y crear el DAO (inyección de dependencias)
        try {
            ConexionDB.getInstance(rutaProperties);
            this.rikishiDAO = new RikishiDAOImpl(ConexionDB.getInstance());
            vista.mostrarLog("✅ Conexión a base de datos establecida.");
        } catch (RuntimeException e) {
            vista.mostrarError("Error al conectar con la BD: " + e.getMessage());
            return;
        }

        // 4. Crear modelo y servidor, inyectando el DAO
        this.dohyo = new Dohyo();
        this.servidor = new ServidorSumo(dohyo, this, rikishiDAO);
        servidor.setDaemon(false);
        servidor.start();
        vista.mostrarLog("🌐 Servidor iniciado. Esperando luchadores en puerto "
                + Protocolo.PUERTO + "...");
    }

    // ── Método auxiliar privado ───────────────────────────────────────────────
    /**
     * Abre un {@link JFileChooser} para que el usuario seleccione el archivo
     * {@code db.properties}.
     *
     * @return ruta absoluta del archivo seleccionado, o {@code null} si canceló
     */
    private String seleccionarProperties() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar db.properties");
        chooser.setFileFilter(
                new FileNameExtensionFilter("Archivos de propiedades (*.properties)", "properties"));
        int resultado = chooser.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // ── Implementación EventoCombateListener ─────────────────────────────────
    /**
     * Recibe el evento de llegada de un luchador y lo pasa a la vista. Siempre
     * en el EDT de Swing vía {@code invokeLater}.
     *
     * @param luchador El luchador que llegó.
     */
    @Override
    public void onLuchadorLlego(Rikishi luchador) {
        String nombre = luchador.getNombre();
        double peso = luchador.getPeso();
        int vic = luchador.getVictorias();
        SwingUtilities.invokeLater(() -> vista.mostrarLuchadorLlego(nombre, peso, vic));
    }

    /**
     * Recibe el evento de inicio de combate.
     *
     * @param luchador1 Primer luchador.
     * @param luchador2 Segundo luchador.
     */
    @Override
    public void onCombateInicia(Rikishi luchador1, Rikishi luchador2) {
        String n1 = luchador1.getNombre();
        String n2 = luchador2.getNombre();
        SwingUtilities.invokeLater(() -> vista.mostrarInicioCombate(n1, n2));
    }

    /**
     * Recibe el evento de ejecución de un turno.
     *
     * @param luchador El luchador que atacó.
     * @param resultado Resultado del turno.
     */
    @Override
    public void onTurnoEjecutado(Rikishi luchador, ResultadoTurno resultado) {
        String nombre = luchador.getNombre();
        String tecnica = resultado.getTecnicaUsada() != null
                ? resultado.getTecnicaUsada().getNombre() : "???";
        boolean saco = resultado.isSacoAlOponente();
        SwingUtilities.invokeLater(() -> vista.mostrarTurno(nombre, tecnica, saco));
    }

    /**
     * Recibe el evento de fin de combate.
     *
     * @param ganador El luchador ganador.
     */
    @Override
    public void onCombateTermino(Rikishi ganador) {
        String nombre = ganador.getNombre();
        int vic = ganador.getVictorias();
        SwingUtilities.invokeLater(() -> vista.mostrarGanador(nombre, vic));
    }

    /**
     * Recibe la confirmación de un cliente.
     *
     * @param luchador El luchador cuyo cliente confirmó.
     */
    @Override
    public void onClienteConfirmo(Rikishi luchador) {
        String nombre = luchador.getNombre();
        SwingUtilities.invokeLater(() -> vista.mostrarClienteDesconectado(nombre));
    }

    /**
     * Recibe un error de la red.
     *
     * @param mensaje Descripción del error.
     */
    @Override
    public void onError(String mensaje) {
        SwingUtilities.invokeLater(() -> vista.mostrarError(mensaje));
    }
}

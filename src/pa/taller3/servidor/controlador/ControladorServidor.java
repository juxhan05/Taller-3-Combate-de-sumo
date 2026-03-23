package pa.taller3.servidor.controlador;

import Pa.taller3.servidor.modelo.Dohyo;
import Pa.taller3.servidor.modelo.ResultadoTurno;
import Pa.taller3.servidor.modelo.Rikishi;
import pa.taller3.servidor.vista.VentanaServidor;

import javax.swing.SwingUtilities;

/**
 * Controlador principal del servidor de sumo.
 *
 * <p>Implementa {@link EventoCombateListener} para recibir eventos
 * desde los hilos de red y actualizarlos <b>de forma segura</b> en la
 * GUI usando {@code SwingUtilities.invokeLater()}.</p>
 *
 * <ul>
 *   <li>Crea el {@link Dohyo} (modelo)</li>
 *   <li>Crea el {@link ServidorSumo} (red/thread)</li>
 *   <li>Actualiza la {@link VentanaServidor} (vista) con cada evento</li>
 * </ul>
 *
 *
 * <p><b>Principio DIP:</b> depende de {@link EventoCombateListener}
 * (abstracción), no de los hilos concretos.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class ControladorServidor implements EventoCombateListener {

    /** Vista del servidor. */
    private final VentanaServidor vista;

    /** El dohyō donde ocurre el combate. */
    private final Dohyo dohyo;

    /** El servidor de sockets. */
    private final ServidorSumo servidor;

    /**
     * Construye el controlador, crea la vista, el dohyō y arranca el servidor.
     * El Launcher solo instancia este controlador sin pasar objetos.
     */
    public ControladorServidor() {
        this.vista   = new VentanaServidor();
        this.vista.setVisible(true);
        this.dohyo   = new Dohyo();
        this.servidor = new ServidorSumo(dohyo, this);
        servidor.setDaemon(false);
        servidor.start();
    }

    // ── Implementación EventoCombateListener ─────────────────────────────────

    /**
     * Recibe el evento de llegada de un luchador y lo pasa a la vista.
     * Siempre en el EDT de Swing vía {@code invokeLater}.
     *
     * @param luchador El luchador que llegó.
     */
    @Override
    public void onLuchadorLlego(Rikishi luchador) {
        String nombre = luchador.getNombre();
        double peso   = luchador.getPeso();
        int vic       = luchador.getVictorias();
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
        String nombre  = luchador.getNombre();
        String tecnica = resultado.getTecnicaUsada() != null
            ? resultado.getTecnicaUsada().getNombre() : "???";
        boolean saco   = resultado.isSacoAlOponente();
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
        int vic       = ganador.getVictorias();
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
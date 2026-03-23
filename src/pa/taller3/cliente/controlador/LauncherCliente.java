package pa.taller3.cliente.controlador;


import javax.swing.SwingUtilities;

/**
 * Punto de entrada del cliente de sumo.
 *
 *
 * <p>Para simular dos luchadores, iniciar dos instancias de este launcher.
 * Ejecutar: {@code java cliente.launcher.LauncherCliente}</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class LauncherCliente {

    /**
     * Inicia el cliente en el Event Dispatch Thread de Swing.
     *
     * @param args Argumentos de línea de comandos (no se usan).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControladorCliente());
    }
}
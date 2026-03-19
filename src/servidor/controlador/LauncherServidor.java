package servidor.controlador;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del servidor de sumo.
 *
 *
 * <p>
 * Para ejecutar: {@code java servidor.launcher.LauncherServidor}</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class LauncherServidor {

    /**
     * Inicia el servidor en el Event Dispatch Thread de Swing.
     *
     * @param args Argumentos de línea de comandos (no se usan).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControladorServidor());
    }
}

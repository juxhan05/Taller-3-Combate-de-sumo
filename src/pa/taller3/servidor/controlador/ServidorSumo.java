package pa.taller3.servidor.controlador;

import Pa.taller3.servidor.modelo.Dohyo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor de sumo que acepta exactamente dos conexiones de clientes.
 *
 * <p>
 * Se ejecuta en su propio {@link Thread} para no bloquear la GUI del servidor
 * mientras espera conexiones. Crea un único {@link Dohyo} compartido y un
 * {@link HiloLuchador} por cada cliente conectado.</p>
 *
 * <p>
 * El {@link Dohyo} es el punto de sincronización entre los dos hilos. Su método
 * {@code synchronized} garantiza que ninguno de los dos hilos modifica el
 * estado del combate al mismo tiempo.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class ServidorSumo extends Thread {

    /**
     * Dohyō compartido donde se realizará el combate.
     */
    private final Dohyo dohyo;

    /**
     * Observador de eventos para actualizar la GUI del servidor.
     */
    private final EventoCombateListener listener;

    /**
     * Socket del servidor. Se guarda para poderlo cerrar si es necesario.
     */
    private ServerSocket serverSocket;

    /**
     * Construye el servidor con el dohyō y el listener de eventos.
     *
     * @param dohyo Dohyō donde se realizará el combate.
     * @param listener Observador que actualiza la GUI.
     */
    public ServidorSumo(Dohyo dohyo, EventoCombateListener listener) {
        this.dohyo = dohyo;
        this.listener = listener;
    }

    /**
     * Abre el puerto, acepta dos conexiones, crea los hilos y espera que ambos
     * terminen antes de cerrar.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Protocolo.PUERTO);

            // Aceptar primer luchador y crear su hilo dedicado
            Socket socket1 = serverSocket.accept();
            HiloLuchador hilo1 = new HiloLuchador(socket1, dohyo, listener);
            hilo1.start();

            // Aceptar segundo luchador y crear su hilo dedicado
            Socket socket2 = serverSocket.accept();
            HiloLuchador hilo2 = new HiloLuchador(socket2, dohyo, listener);
            hilo2.start();

            // Esperar a que ambos hilos terminen (ambos clientes confirmaron)
            hilo1.join();
            hilo2.join();
            // Cerrar el servidor automaticamente
            System.exit(0);

        } catch (IOException e) {
            if (!isInterrupted()) {
                listener.onError("Error en servidor: " + e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            cerrarServidor();
        }
    }

    /**
     * Cierra el {@link ServerSocket} de forma segura.
     */
    private void cerrarServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
    }
}

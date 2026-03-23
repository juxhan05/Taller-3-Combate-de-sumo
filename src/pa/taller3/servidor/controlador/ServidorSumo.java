package pa.taller3.servidor.controlador;

import pa.taller3.servidor.dao.IRikishiDAO;
import pa.taller3.servidor.modelo.Dohyo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor de sumo que acepta exactamente dos conexiones de clientes.
 *
 * <p>Se ejecuta en su propio {@link Thread} para no bloquear la GUI del
 * servidor mientras espera conexiones. Crea un único {@link Dohyo}
 * compartido y un {@link HiloLuchador} por cada cliente conectado.</p>
 *
 * <p><b>SOLID - D:</b> Recibe {@link IRikishiDAO} por constructor y lo
 * pasa a cada {@link HiloLuchador} para que persista en BD.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class ServidorSumo extends Thread {

    /** Dohyō compartido donde se realizará el combate. */
    private final Dohyo dohyo;

    /** Observador de eventos para actualizar la GUI del servidor. */
    private final EventoCombateListener listener;

    /**
     * DAO de luchadores inyectado por constructor.
     * Se pasa a cada HiloLuchador para guardar en BD.
     */
    private final IRikishiDAO rikishiDAO;

    /** Socket del servidor. Se guarda para cerrarlo si es necesario. */
    private ServerSocket serverSocket;

    /**
     * Construye el servidor con el dohyō, el listener y el DAO.
     *
     * @param dohyo      Dohyō donde se realizará el combate.
     * @param listener   Observador que actualiza la GUI.
     * @param rikishiDAO DAO para persistir luchadores en BD.
     */
    public ServidorSumo(Dohyo dohyo, EventoCombateListener listener,
                        IRikishiDAO rikishiDAO) {
        this.dohyo      = dohyo;
        this.listener   = listener;
        this.rikishiDAO = rikishiDAO;
    }

    /**
     * Abre el puerto, acepta dos conexiones, crea los hilos y espera
     * que ambos terminen antes de cerrar el servidor.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Protocolo.PUERTO);

            // Aceptar primer luchador — le pasa el DAO para guardar en BD
            Socket socket1 = serverSocket.accept();
            HiloLuchador hilo1 = new HiloLuchador(socket1, dohyo, listener, rikishiDAO);
            hilo1.start();

            // Aceptar segundo luchador
            Socket socket2 = serverSocket.accept();
            HiloLuchador hilo2 = new HiloLuchador(socket2, dohyo, listener, rikishiDAO);
            hilo2.start();

            // Esperar a que ambos hilos terminen (ambos clientes confirmaron)
            hilo1.join();
            hilo2.join();

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

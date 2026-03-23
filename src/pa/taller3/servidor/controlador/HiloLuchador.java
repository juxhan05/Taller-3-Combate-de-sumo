package pa.taller3.servidor.controlador;

import pa.taller3.servidor.dao.IRikishiDAO;
import pa.taller3.servidor.modelo.Dohyo;
import pa.taller3.servidor.modelo.ResultadoTurno;
import pa.taller3.servidor.modelo.Rikishi;
import pa.taller3.servidor.modelo.Kimarite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Hilo del servidor que atiende la comunicacion con un cliente.
 *
 * <p>Flujo: recibe datos del luchador → persiste en BD → sube al dohyō →
 * combate por turnos → notifica resultado → espera confirmación.</p>
 *
 * <p>Formato recibido: nombre,peso,k1:d1|k2:d2|k3:d3</p>
 * <p>Formato enviado:  GANADOR,nombreGanador,victorias</p>
 *
 * <p><b>SOLID - D:</b> Recibe {@link IRikishiDAO} por constructor,
 * nunca instancia la implementación concreta directamente.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class HiloLuchador extends Thread {

    /** Socket de la conexion con el cliente. */
    private final Socket socket;

    /** Dohyo compartido entre los dos hilos. */
    private final Dohyo dohyo;

    /** Listener para notificar eventos a la GUI del servidor. */
    private final EventoCombateListener listener;

    /**
     * DAO para persistir el luchador recibido en la base de datos.
     * Inyectado por constructor (SOLID - D).
     */
    private final IRikishiDAO rikishiDAO;

    /**
     * Construye el hilo para un cliente.
     *
     * @param socket     Socket TCP del cliente.
     * @param dohyo      Dohyo compartido.
     * @param listener   Observador de eventos para la GUI.
     * @param rikishiDAO DAO para guardar al luchador en BD.
     */
    public HiloLuchador(Socket socket, Dohyo dohyo,
                        EventoCombateListener listener, IRikishiDAO rikishiDAO) {
        this.socket     = socket;
        this.dohyo      = dohyo;
        this.listener   = listener;
        this.rikishiDAO = rikishiDAO;
    }

    /**
     * Ciclo completo: recibir datos → guardar en BD → combatir →
     * notificar resultado → confirmar.
     */
    @Override
    public void run() {
        PrintWriter    salida  = null;
        BufferedReader entrada = null;
        try {
            salida  = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 1. Recibir datos del luchador como String y construir objeto
            String datos     = entrada.readLine();
            Rikishi luchador = parsearRikishi(datos);

            // 2. Persistir en la base de datos
            boolean guardado = rikishiDAO.guardar(luchador);
            if (guardado) {
                listener.onLuchadorLlego(luchador);
            } else {
                listener.onError("No se pudo guardar a " + luchador.getNombre() + " en BD.");
            }

            // 3. Subir al dohyo y esperar al rival
            dohyo.subirAlDohyo(luchador);

            // Pequeña pausa para que ambos hilos estén listos
            Thread.sleep(300);

            // Solo luchador1 dispara el evento de inicio
            if (dohyo.getLuchador1() == luchador) {
                listener.onCombateInicia(dohyo.getLuchador1(), dohyo.getLuchador2());
            }

            Thread.sleep(500);

            // 4. Bucle del combate
            while (!dohyo.isCombateTerminado()) {
                ResultadoTurno resultado = dohyo.ejecutarTurno(luchador);
                if (resultado.getTecnicaUsada() != null) {
                    listener.onTurnoEjecutado(luchador, resultado);
                }
                Thread.sleep(1200);
            }

            Thread.sleep(500);

            // 5. Actualizar victorias y ha_combatido en BD
            luchador.setHaCombatido(true);
            rikishiDAO.actualizar(luchador);

            // 6. Notificar resultado al cliente
            boolean esGanador   = (dohyo.getGanador() == luchador);
            Rikishi ganador     = dohyo.getGanador();
            String msgResultado = (esGanador ? Protocolo.MSG_GANADOR : Protocolo.MSG_PERDEDOR)
                    + Protocolo.SEP_CAMPO + ganador.getNombre()
                    + Protocolo.SEP_CAMPO + ganador.getVictorias();
            salida.println(msgResultado);

            // Solo luchador1 dispara el evento de fin (evitar duplicado)
            if (dohyo.getLuchador1() == luchador) {
                listener.onCombateTermino(ganador);
            }

            // 7. Esperar confirmacion del cliente
            String confirmacion = entrada.readLine();
            if (Protocolo.MSG_CLIENTE_LISTO.equals(confirmacion)) {
                listener.onClienteConfirmo(luchador);
            }

        } catch (IOException e) {
            listener.onError("Error de red: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            listener.onError("Hilo interrumpido: " + e.getMessage());
        } finally {
            cerrarRecursos(salida, entrada);
        }
    }

    /**
     * Parsea el String recibido del cliente y construye un {@link Rikishi}.
     * Formato: {@code nombre,peso,k1:d1|k2:d2|k3:d3}
     *
     * @param datos String con los datos del luchador.
     * @return Rikishi construido con los datos recibidos.
     */
    private Rikishi parsearRikishi(String datos) {
        String[] partes = datos.split(Protocolo.SEP_CAMPO, 3);
        String   nombre = partes[0];
        double   peso   = Double.parseDouble(partes[1]);
        Rikishi  r      = new Rikishi(nombre, peso, 0);

        if (partes.length > 2 && !partes[2].isEmpty()) {
            String[] kimarites = partes[2].split("\\|");
            for (String k : kimarites) {
                String[] kp    = k.split(Protocolo.SEP_DESC, 2);
                String kNombre = kp[0];
                String kDesc   = kp.length > 1 ? kp[1] : "";
                r.agregarKimarite(new Kimarite(kNombre, kDesc));
            }
        }
        return r;
    }

    /**
     * Cierra streams y socket de forma segura.
     *
     * @param salida  Stream de salida.
     * @param entrada Stream de entrada.
     */
    private void cerrarRecursos(PrintWriter salida, BufferedReader entrada) {
        try { if (salida  != null) salida.close();  } catch (Exception ignored) { }
        try { if (entrada != null) entrada.close(); } catch (Exception ignored) { }
        try { if (socket  != null && !socket.isClosed()) socket.close(); }
        catch (IOException ignored) { }
    }
}

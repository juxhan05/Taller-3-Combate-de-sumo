package pa.taller3.cliente.controlador;

import pa.taller3.cliente.vista.VentanaCliente;
import pa.taller3.cliente.modelo.Kimarite;
import pa.taller3.cliente.modelo.Rikishi;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Controlador principal del cliente de sumo.
 *
 * <p>
 * Gestiona la interacción entre {@link VentanaCliente} (vista), el modelo
 * ({@link Rikishi}/{@link Kimarite}) y la red (sockets).</p>
 *
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class ControladorCliente {

    /**
     * Dirección del servidor.
     */
    private static final String HOST = "localhost";

    /**
     * Vista del cliente.
     */
    private final VentanaCliente vista;

    /**
     * Ruta del archivo .properties seleccionado.
     */
    private String rutaProperties;

    /**
     * Kimarites cargados desde el properties para construir el Rikishi.
     */
    private java.util.List<Kimarite> kimaritesDisponibles = new java.util.ArrayList<>();

    /**
     * Socket activo para poder confirmarlo al cerrar.
     */
    private volatile Socket socketActivo;
    private volatile java.io.PrintWriter salidaActiva;

    /**
     * Construye el controlador, crea la vista y registra los listeners. El
     * Launcher solo instancia este controlador sin pasar objetos.
     */
    public ControladorCliente() {
        this.vista = new VentanaCliente();
        this.vista.setVisible(true);
        registrarListeners();
    }

    /**
     * Registra los listeners en los botones de la vista. Punto de separación
     * evento/listener del enunciado.
     */
    private void registrarListeners() {
        vista.agregarListenerCargarProperties(new ListenerCargarProperties());
        vista.agregarListenerCombatir(new ListenerCombatir());
        vista.agregarListenerSalir(new ListenerSalir());
    }

    // ── Lógica de acciones ────────────────────────────────────────────────────
    /**
     * Permite al usuario seleccionar un archivo .properties y cargar los
     * kimarites.
     *
     * <p>
     * Flujo:</p>
     * <ul>
     * <li>Abre un selector de archivos.</li>
     * <li>Si se selecciona un archivo, guarda la ruta y actualiza la
     * vista.</li>
     * <li>Lee los kimarites desde el archivo.</li>
     * <li>Si no hay datos, muestra un mensaje de error.</li>
     * <li>Si hay datos, los carga en la interfaz.</li>
     * </ul>
     */
    private void accionCargarProperties() {
        // 1. Delegamos la parte visual a la vista
        File archivo = vista.seleccionarArchivoProperties();

        if (archivo != null) {
            // 2. Actualizamos datos en la vista y guardamos ruta
            this.rutaProperties = archivo.getAbsolutePath();
            vista.setArchivoProperties(archivo.getName());

            // 3. Leer datos crudos (el lector solo devuelve strings)
            List<String[]> datos = LectorKimarites.cargarDesdeProperties(rutaProperties);

            // 4. Guardar kimarites en el controlador y pasar solo nombres a la vista
            kimaritesDisponibles = new java.util.ArrayList<>();
            for (String[] d : datos) {
                kimaritesDisponibles.add(new Kimarite(d[0], d[1]));
            }
            java.util.List<String> nombres = new java.util.ArrayList<>();
            for (Kimarite k : kimaritesDisponibles) {
                nombres.add(k.getNombre());
            }

            // 5. Mostrar resultado
            if (nombres.isEmpty()) {
                vista.mostrarMensaje(
                        "No se encontraron kimarites en el archivo.",
                        "Archivo invalido"
                );
            } else {
                vista.cargarKimaritesEnLista(nombres);
                vista.setEstado(nombres.size() + " tecnicas cargadas. Selecciona las que domina tu rikishi.");
            }
        }
    }

    /**
     * Valida datos, construye el Rikishi y lo envía al servidor en hilo
     * separado.
     */
    private void accionCombatir() {
        String nombre = vista.getNombreLuchador();
        if (nombre.isEmpty()) {
            vista.mostrarMensaje("Ingresa el nombre del luchador.",
                    "Datos incompletos");
            return;
        }
        List<String> nombresSeleccionados = vista.getKimaritesSeleccionados();
        if (nombresSeleccionados.isEmpty()) {
            vista.mostrarMensaje("Selecciona al menos una tecnica.", "Datos incompletos");
            return;
        }

        Rikishi luchador = new Rikishi(nombre, vista.getPesoLuchador(), 0);
        // Buscar Kimarite completo desde la lista disponible
        for (String n : nombresSeleccionados) {
            for (Kimarite k : kimaritesDisponibles) {
                if (k.getNombre().equals(n)) {
                    luchador.agregarKimarite(k);
                    break;
                }
            }
        }

        vista.setBtnCombatirHabilitado(false);
        vista.setEstado("⏳ Conectando al servidor...");

        Thread hiloRed = new Thread(() -> enviarAlServidor(luchador));
        hiloRed.setDaemon(true);
        hiloRed.start();
    }

    /**
     * Maneja la comunicacion completa con el servidor usando texto plano.
     *
     * Envia los datos del luchador como String con formato:
     * nombre,peso,k1:d1|k2:d2|k3:d3
     *
     * Recibe el resultado como String con formato:
     * GANADOR,nombreGanador,victorias o PERDEDOR,nombreGanador,victorias
     *
     * @param luchador El luchador a enviar.
     */
    private void enviarAlServidor(Rikishi luchador) {
        Socket socket = null;
        PrintWriter salida = null;
        BufferedReader entrada = null;
        try {
            socket = new Socket(HOST, Protocolo.PUERTO);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketActivo = socket;
            salidaActiva = salida;

            // Construir mensaje del luchador como String
            StringBuilder sb = new StringBuilder();
            sb.append(luchador.getNombre())
                    .append(Protocolo.SEP_CAMPO)
                    .append(luchador.getPeso())
                    .append(Protocolo.SEP_CAMPO);
            for (int i = 0; i < luchador.getKimarites().size(); i++) {
                pa.taller3.cliente.modelo.Kimarite k = luchador.getKimarites().get(i);
                sb.append(k.getNombre()).append(Protocolo.SEP_DESC).append(k.getDescripcion());
                if (i < luchador.getKimarites().size() - 1) {
                    sb.append("|");
                }
            }
            salida.println(sb.toString());

            final String estadoMsg = "Luchador " + luchador.getNombre() + " en el dohyo. Combate en curso!";
            SwingUtilities.invokeLater(() -> vista.setEstado(estadoMsg));

            // Esperar resultado
            String respuesta = entrada.readLine();
            String[] partes = respuesta.split(Protocolo.SEP_CAMPO, 3);
            boolean esGanador = Protocolo.MSG_GANADOR.equals(partes[0]);
            String nomG = partes.length > 1 ? partes[1] : "Desconocido";
            int vicG = partes.length > 2 ? Integer.parseInt(partes[2]) : 0;

            SwingUtilities.invokeLater(() -> vista.mostrarResultado(esGanador, nomG, vicG));

            // La confirmacion la envia el boton Salir cuando el usuario lo presiona
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                vista.mostrarMensaje(
                        "Error de conexion con el servidor: " + e.getMessage()
                        + " Verifica que el servidor este activo en el puerto " + Protocolo.PUERTO,
                        "Error de red");
                vista.setBtnCombatirHabilitado(true);
                vista.setEstado("Error al conectar. Verifica el servidor.");
            });
        } finally {
            try {
                if (salida != null) {
                    salida.close();
                }
            } catch (Exception ignored) {
            }
            try {
                if (entrada != null) {
                    entrada.close();
                }
            } catch (Exception ignored) {
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    // ── Listeners internos ────────────────────────────────────────────────────
    /**
     * Listener del botón "Cargar Properties". Delega al método
     * {@link #accionCargarProperties()}.
     */
    private class ListenerCargarProperties implements ActionListener {

        /**
         * @param e Evento de clic.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            accionCargarProperties();
        }
    }

    /**
     * Listener del boton Combatir. Delega al metodo accionCombatir.
     */
    private class ListenerCombatir implements ActionListener {

        /**
         * @param e Evento de clic.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            accionCombatir();
        }
    }

    /**
     * Listener del boton Salir. Envia confirmacion al servidor y cierra el
     * cliente.
     */
    private class ListenerSalir implements ActionListener {

        /**
         * @param e Evento de clic.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            accionSalir();
        }
    }

    /**
     * Envia confirmacion al servidor y cierra la aplicacion.
     */
    private void accionSalir() {
        try {
            if (salidaActiva != null) {
                salidaActiva.println(Protocolo.MSG_CLIENTE_LISTO);
            }
        } catch (Exception ignored) {
        }
        System.exit(0);
    }
}

package pa.taller3.cliente.controlador;

/**
 * Constantes del protocolo de comunicacion cliente-servidor.
 *
 * Toda la comunicacion se realiza mediante texto plano (Strings)
 * a traves de BufferedReader y PrintWriter, evitando la serializacion
 * de objetos y sus problemas de compatibilidad entre paquetes distintos.
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public final class Protocolo {

    public static final int PUERTO = 1052;
    public static final String SEP_CAMPO = ",";
    public static final String SEP_KIMARITE = "|";
    public static final String SEP_DESC = ":";
    public static final String MSG_GANADOR = "GANADOR";
    public static final String MSG_PERDEDOR = "PERDEDOR";
    public static final String MSG_CLIENTE_LISTO = "CLIENTE_LISTO";
    private Protocolo() { }
}
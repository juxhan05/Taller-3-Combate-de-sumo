package servidor.controlador;

/**
 * Constantes del protocolo de comunicacion cliente-servidor.
 *
 * Toda la comunicacion se realiza mediante texto plano (Strings)
 * a traves de BufferedReader y PrintWriter, evitando la serializacion
 * de objetos y sus problemas de compatibilidad entre paquetes distintos.
 *
 * Flujo:
 *   CLIENTE envia:  nombre,peso,k1:d1|k2:d2
 *   SERVIDOR envia: GANADOR,nombreGanador,victorias
 *                o: PERDEDOR,nombreGanador,victorias
 *   CLIENTE envia:  CLIENTE_LISTO
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public final class Protocolo {

    /** Puerto TCP en el que escucha el servidor. */
    public static final int PUERTO = 1052;

    /** Separador entre campos del mensaje. */
    public static final String SEP_CAMPO = ",";

    /** Separador entre kimarites. */
    public static final String SEP_KIMARITE = "|";

    /** Separador entre nombre y descripcion de un kimarite. */
    public static final String SEP_DESC = ":";

    /** Prefijo indicando que este cliente gano. */
    public static final String MSG_GANADOR = "GANADOR";

    /** Prefijo indicando que este cliente perdio. */
    public static final String MSG_PERDEDOR = "PERDEDOR";

    /** Mensaje del cliente confirmando que recibio el resultado. */
    public static final String MSG_CLIENTE_LISTO = "CLIENTE_LISTO";

    /** Constructor privado: clase de constantes, no instanciable. */
    private Protocolo() { }
}
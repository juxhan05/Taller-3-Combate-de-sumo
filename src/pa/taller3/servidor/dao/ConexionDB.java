package pa.taller3.servidor.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexión Singleton a la base de datos MySQL.
 *
 * <p>Lee la URL, usuario y contraseña desde el archivo
 * {@code data/db.properties} para no dejar credenciales en el código.
 * Garantiza que exista una única instancia de conexión durante
 * toda la ejecución del servidor.</p>
 *
 * <p><b>SOLID - S:</b> Solo gestiona la conexión a BD.<br>
 * <b>SOLID - D:</b> Implementa {@link IConexion} para que los DAOs
 * dependan de la abstracción, no de esta clase concreta.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class ConexionDB implements IConexion {

    /** Única instancia (patrón Singleton). */
    private static ConexionDB instancia;

    /** Conexión JDBC activa. */
    private Connection conexion;

    // ── Constructor privado ───────────────────────────────────────────────────

    /**
     * Lee el archivo {@code .properties} y abre la conexión JDBC.
     *
     * @param rutaProperties ruta absoluta al archivo {@code db.properties}
     * @throws RuntimeException si no se puede conectar
     */
    private ConexionDB(String rutaProperties) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(rutaProperties)) {
            props.load(fis);
            String url      = props.getProperty("db.url");
            String usuario  = props.getProperty("db.usuario");
            String password = props.getProperty("db.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(url, usuario, password);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(
                "Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }

    // ── Métodos Singleton ─────────────────────────────────────────────────────

    /**
     * Crea e inicializa la única instancia usando la ruta indicada.
     * Debe llamarse una sola vez al arrancar el servidor.
     *
     * @param rutaProperties ruta al archivo {@code db.properties}
     * @return instancia única de {@code ConexionDB}
     */
    public static synchronized ConexionDB getInstance(String rutaProperties) {
        if (instancia == null) {
            instancia = new ConexionDB(rutaProperties);
        }
        return instancia;
    }

    /**
     * Retorna la instancia ya inicializada.
     * Requiere haber llamado {@link #getInstance(String)} primero.
     *
     * @return instancia única de {@code ConexionDB}
     * @throws IllegalStateException si aún no fue inicializada
     */
    public static ConexionDB getInstance() {
        if (instancia == null) {
            throw new IllegalStateException(
                "ConexionDB no inicializada. Llame primero getInstance(ruta).");
        }
        return instancia;
    }

    // ── Implementación IConexion ──────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Verifica que la conexión siga abierta antes de retornarla.
     */
    @Override
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                throw new RuntimeException("La conexión a la BD está cerrada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar conexión: " + e.getMessage(), e);
        }
        return conexion;
    }

    /**
     * Cierra la conexión y destruye el Singleton para permitir reconexión.
     */
    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
            instancia = null;
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}

package pa.taller3.servidor.dao;

import java.sql.Connection;

/**
 * Abstracción de la conexión a base de datos.
 *
 * <p><b>SOLID - D (Inversión de dependencias):</b> Los DAOs dependen de esta
 * interfaz, nunca de la clase concreta {@link ConexionDB}. Esto permite
 * cambiar el motor de BD sin tocar ningún DAO.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public interface IConexion {

    /**
     * Retorna la conexión activa a la base de datos.
     *
     * @return objeto {@link Connection} válido y abierto
     */
    Connection getConexion();
}

package pa.taller3.servidor.dao;

import pa.taller3.servidor.modelo.Rikishi;

import java.util.List;

/**
 * Interfaz DAO para las operaciones de persistencia de luchadores.
 *
 * <p><b>SOLID - I (Segregación):</b> Solo declara operaciones de
 * {@link Rikishi}. No mezcla responsabilidades con otros modelos.<br>
 * <b>SOLID - O (Abierto/Cerrado):</b> Se puede crear una nueva
 * implementación (p. ej. para PostgreSQL) sin tocar la existente.<br>
 * <b>SOLID - D:</b> El controlador depende de esta interfaz,
 * nunca de {@code RikishiDAOImpl} directamente.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public interface IRikishiDAO {

    /**
     * Persiste un nuevo luchador y sus kimarites en la base de datos.
     *
     * @param rikishi luchador a guardar
     * @return {@code true} si se guardó correctamente
     */
    boolean guardar(Rikishi rikishi);

    /**
     * Retorna todos los luchadores registrados en la BD.
     *
     * @return lista completa de luchadores con sus kimarites
     */
    List<Rikishi> buscarTodos();

    /**
     * Retorna solo los luchadores que aún no han combatido.
     *
     * @return lista de luchadores disponibles para combatir
     */
    List<Rikishi> buscarDisponibles();

    /**
     * Busca un luchador por su ID en la BD.
     *
     * @param id identificador del luchador
     * @return luchador encontrado o {@code null} si no existe
     */
    Rikishi buscarPorId(int id);

    /**
     * Actualiza las victorias y el estado {@code ha_combatido}
     * de un luchador en la BD.
     *
     * @param rikishi luchador con datos actualizados
     * @return {@code true} si se actualizó correctamente
     */
    boolean actualizar(Rikishi rikishi);
}

package pa.taller3.servidor.dao;

import pa.taller3.servidor.modelo.Kimarite;
import pa.taller3.servidor.modelo.Rikishi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación MySQL de {@link IRikishiDAO}.
 *
 * <p>Gestiona toda la persistencia de luchadores: guarda el luchador
 * y su arreglo de kimarites en una transacción atómica, y los
 * reconstruye completos al consultar.</p>
 *
 * <p><b>SOLID - S:</b> Solo gestiona persistencia de {@link Rikishi}.<br>
 * <b>SOLID - L:</b> Puede reemplazar cualquier referencia a
 * {@link IRikishiDAO} sin romper el contrato.<br>
 * <b>SOLID - D:</b> Recibe {@link IConexion} por constructor
 * (inyección de dependencias), nunca la instancia directamente.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class RikishiDAOImpl implements IRikishiDAO {

    /** Conexión a BD inyectada por constructor. */
    private final IConexion conexion;

    /**
     * Constructor con inyección de dependencia.
     *
     * @param conexion implementación de {@link IConexion} a usar
     */
    public RikishiDAOImpl(IConexion conexion) {
        this.conexion = conexion;
    }

    // ── guardar ───────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * <p>Ejecuta dos operaciones en una transacción:</p>
     * <ol>
     *   <li>INSERT en {@code luchadores} → obtiene el ID generado.</li>
     *   <li>INSERT en {@code kimarites_luchador} por cada técnica del arreglo.</li>
     * </ol>
     * Si algo falla hace rollback completo.
     */
    @Override
    public boolean guardar(Rikishi rikishi) {
        String sqlLuchador  = "INSERT INTO luchadores (nombre, peso, victorias, ha_combatido) "
                            + "VALUES (?, ?, ?, ?)";
        String sqlKimarite  = "INSERT INTO kimarites_luchador (id_luchador, nombre_kimarite, descripcion) "
                            + "VALUES (?, ?, ?)";
        Connection con = conexion.getConexion();
        try {
            con.setAutoCommit(false);

            // 1. Insertar luchador y capturar ID generado
            try (PreparedStatement ps = con.prepareStatement(
                    sqlLuchador, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, rikishi.getNombre());
                ps.setDouble(2, rikishi.getPeso());
                ps.setInt(3, rikishi.getVictorias());
                ps.setBoolean(4, false);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        rikishi.setId(rs.getInt(1));
                    }
                }
            }

            // 2. Insertar cada kimarite del arreglo del luchador
            if (rikishi.getKimarites() != null && !rikishi.getKimarites().isEmpty()) {
                try (PreparedStatement psK = con.prepareStatement(sqlKimarite)) {
                    for (Kimarite k : rikishi.getKimarites()) {
                        psK.setInt(1, rikishi.getId());
                        psK.setString(2, k.getNombre());
                        psK.setString(3, k.getDescripcion());
                        psK.addBatch();
                    }
                    psK.executeBatch();
                }
            }

            con.commit();
            con.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException ex) { /* ignorar */ }
            System.err.println("Error al guardar luchador: " + e.getMessage());
            return false;
        }
    }

    // ── buscarTodos ───────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Retorna todos los luchadores con su arreglo de kimarites reconstruido.
     */
    @Override
    public List<Rikishi> buscarTodos() {
        List<Rikishi> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, peso, victorias, ha_combatido FROM luchadores";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Rikishi r = mapearFila(rs);
                cargarKimarites(r);
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar luchadores: " + e.getMessage());
        }
        return lista;
    }

    // ── buscarDisponibles ─────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Solo retorna luchadores con {@code ha_combatido = false}.
     */
    @Override
    public List<Rikishi> buscarDisponibles() {
        List<Rikishi> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, peso, victorias, ha_combatido "
                   + "FROM luchadores WHERE ha_combatido = FALSE";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Rikishi r = mapearFila(rs);
                cargarKimarites(r);
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar disponibles: " + e.getMessage());
        }
        return lista;
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    public Rikishi buscarPorId(int id) {
        String sql = "SELECT id, nombre, peso, victorias, ha_combatido "
                   + "FROM luchadores WHERE id = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rikishi r = mapearFila(rs);
                    cargarKimarites(r);
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por id: " + e.getMessage());
        }
        return null;
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Actualiza victorias y estado {@code ha_combatido}.
     */
    @Override
    public boolean actualizar(Rikishi rikishi) {
        String sql = "UPDATE luchadores SET victorias = ?, ha_combatido = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, rikishi.getVictorias());
            ps.setBoolean(2, rikishi.isHaCombatido());
            ps.setInt(3, rikishi.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar luchador: " + e.getMessage());
            return false;
        }
    }

    // ── Métodos auxiliares privados ───────────────────────────────────────────

    /**
     * Mapea una fila del {@link ResultSet} a un objeto {@link Rikishi}.
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return Rikishi con los datos de la fila (sin kimarites aún)
     * @throws SQLException si hay error al leer la fila
     */
    private Rikishi mapearFila(ResultSet rs) throws SQLException {
        Rikishi r = new Rikishi(
            rs.getString("nombre"),
            rs.getDouble("peso"),
            rs.getInt("victorias")
        );
        r.setId(rs.getInt("id"));
        r.setHaCombatido(rs.getBoolean("ha_combatido"));
        return r;
    }

    /**
     * Consulta los kimarites del luchador en la BD y los agrega a su arreglo.
     *
     * @param rikishi luchador al que se le cargarán los kimarites
     */
    private void cargarKimarites(Rikishi rikishi) {
        String sql = "SELECT nombre_kimarite, descripcion "
                   + "FROM kimarites_luchador WHERE id_luchador = ?";
        try (PreparedStatement ps = conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, rikishi.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rikishi.agregarKimarite(new Kimarite(
                        rs.getString("nombre_kimarite"),
                        rs.getString("descripcion")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar kimarites de "
                + rikishi.getNombre() + ": " + e.getMessage());
        }
    }
}

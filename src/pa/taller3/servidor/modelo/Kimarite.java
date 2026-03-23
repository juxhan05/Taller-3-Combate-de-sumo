package pa.taller3.servidor.modelo;


/**
 * Representa una técnica de combate en el sumo (Kimarite).
 *
 * <p>Un Kimarite tiene nombre y descripción. Las técnicas son cargadas
 * desde un archivo {@code .properties} y asignadas al luchador según
 * su especialidad. Las tecnicas son cargadas desde un archivo .properties.</p>
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class Kimarite {

    /** UID de serialización para compatibilidad entre JVM. */
    /** Nombre de la técnica, ej: "Yorikiri". */
    private final String nombre;

    /** Descripción de cómo se aplica la técnica. */
    private final String descripcion;

    /**
     * Crea un Kimarite con nombre y descripción.
     *
     * @param nombre      Nombre de la técnica de sumo.
     * @param descripcion Descripción breve de la técnica.
     */
    public Kimarite(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el nombre de la técnica.
     *
     * @return Nombre del kimarite.
     */
    public String getNombre() { return nombre; }

    /**
     * Obtiene la descripción de la técnica.
     *
     * @return Descripción del kimarite.
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Representación de texto del kimarite mostrando su nombre.
     *
     * @return Nombre de la técnica.
     */
    @Override
    public String toString() { return nombre; }
}

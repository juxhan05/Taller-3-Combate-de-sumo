package cliente.controlador;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Utilidad para cargar kimarites desde un archivo {@code .properties}.
 *
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public final class LectorKimarites {

    /**
     * Constructor privado: clase utilitaria, no instanciable.
     */
    private LectorKimarites() {
    }

    /**
     * Carga la lista de kimarites desde un archivo {@code .properties}.
     *
     * <p>
     * Lee entradas numeradas desde {@code kimarite.1} hasta que no encuentre
     * más. Si el archivo no existe o está vacío, devuelve lista vacía.</p>
     *
     * @param rutaArchivo Ruta absoluta al archivo {@code .properties}.
     * @return Lista de arreglos {nombre, descripcion}. Vacía si hubo error.
     */
    public static List<String[]> cargarDesdeProperties(String rutaArchivo) {
        List<String[]> lista = new ArrayList<>();
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(rutaArchivo)) {
            props.load(fis);

            int i = 1;
            while (props.containsKey("kimarite." + i + ".nombre")) {
                String nombre = props.getProperty("kimarite." + i + ".nombre", "").trim();
                String desc = props.getProperty("kimarite." + i + ".descripcion", "").trim();
                if (!nombre.isEmpty()) {
                    lista.add(new String[]{nombre, desc});
                }
                i++;
            }
        } catch (IOException e) {
            // El error se propaga como lista vacía; el controlador
            // lo detectará y mostrará el mensaje apropiado en la GUI.
            lista.clear();
        }

        return lista;
    }
}

package cliente.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa un luchador de sumo (Rikishi).
 *
 * <p>
 * Es la entidad principal del sistema. Contiene: nombre, peso, victorias, lista
 * de kimarites que domina, referencia a su rival, y estado (dentro o fuera del
 * dohyō).</p>
 *
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class Rikishi {

    private String nombre;
    private double peso;
    private int victorias;
    private List<Kimarite> kimarites;
    private volatile boolean dentroDohyo;
    private transient Rikishi rival;
    private transient Random random;
    public Rikishi(String nombre, double peso, int victorias) {
        this.nombre = nombre;
        this.peso = peso;
        this.victorias = victorias;
        this.kimarites = new ArrayList<>();
        this.dentroDohyo = true;
        this.random = new Random();
    }

    /**
     * Selecciona aleatoriamente una técnica del repertorio del luchador.
     *
     * <p>
     * Genera un índice aleatorio entre 0 y {@code kimarites.size()-1}. Esto
     * asegura que no se usa siempre la misma técnica, como exige el
     * enunciado.</p>
     *
     * @return Un {@link Kimarite} aleatorio, o {@code null} si no hay técnicas.
     */
    public Kimarite seleccionarKimariteAleatorio() {
        if (kimarites == null || kimarites.isEmpty()) {
            return null;
        }
        return kimarites.get(random.nextInt(kimarites.size()));
    }

    /**
     * Agrega una técnica al repertorio del luchador.
     *
     * @param k La técnica a agregar. Se ignora si es null.
     */
    public void agregarKimarite(Kimarite k) {
        if (k != null) {
            kimarites.add(k);
        }
    }

    /**
     * Incrementa en uno el contador de victorias.
     */
    public void incrementarVictorias() {
        victorias++;
    }

    // ── Getters y Setters ────────────────────────────────────────────────────
 
    public String getNombre() {
        return nombre;
    }

    public double getPeso() {
        return peso;
    }

    public int getVictorias() {
        return victorias;
    }

    public List<Kimarite> getKimarites() {
        return kimarites;
    }

    public boolean isDentroDohyo() {
        return dentroDohyo;
    }

    public void setDentroDohyo(boolean dentroDohyo) {
        this.dentroDohyo = dentroDohyo;
    }

    public Rikishi getRival() {
        return rival;
    }

    public void setRival(Rikishi rival) {
        this.rival = rival;
    }

    /**
     * Reinicializa campos transient después de deserialización por socket.
     *
     * <p>
     * Java llama este método automáticamente al reconstruir el objeto. Sin
     * esto, {@code random} queda {@code null} y
     * {@link #seleccionarKimariteAleatorio()} lanza NullPointerException en el
     * servidor.</p>
     *
     * @param ois Stream de deserialización.
     * @throws IOException Si hay error de lectura.
     * @throws ClassNotFoundException Si falta alguna clase.
     */
    /**
     * Representación en texto del luchador para logs.
     *
     * @return Cadena con nombre, peso y victorias.
     */
    @Override
    public String toString() {
        return String.format("Rikishi[%s | %.1f kg | %d victorias]",
                nombre, peso, victorias);
    }
}

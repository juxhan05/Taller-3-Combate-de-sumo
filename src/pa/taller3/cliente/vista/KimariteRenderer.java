package pa.taller3.cliente.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Renderer personalizado para mostrar kimarites en la JList. Recibe Strings con
 * el nombre de cada tecnica.
 *
 * @author Julian, Miguel, Andres
 * @version 1.0
 */
public class KimariteRenderer extends DefaultListCellRenderer {

    private static final Color C_PAR = new Color(30, 20, 15);
    private static final Color C_IMPAR = new Color(22, 14, 10);
    private static final Color C_SELECCION = new Color(120, 15, 15);
    private static final Color C_NOMBRE = new Color(245, 235, 210);
    private static final Color C_BORDE_SEL = new Color(212, 175, 55);

    /**
     * Renderiza cada celda con estilo japones oscuro.
     *
     * @param list Lista contenedora.
     * @param value String con el nombre del kimarite.
     * @param index Indice de la celda.
     * @param isSelected Si esta seleccionado.
     * @param hasFocus Si tiene foco.
     * @return Componente configurado.
     */
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index,
            boolean isSelected, boolean hasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

        setText("<html><b style=\'color:#F5EBD2\'>" + value + "</b></html>");

        setBackground(isSelected ? C_SELECCION : (index % 2 == 0 ? C_PAR : C_IMPAR));

        if (isSelected) {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, C_BORDE_SEL),
                    new EmptyBorder(6, 10, 6, 10)
            ));
        } else {
            setBorder(new EmptyBorder(6, 10, 6, 10));
        }
        return this;
    }
}

package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.function.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class AKCustomListCellRenderer<T> extends DefaultListCellRenderer {

    private static final Logger LOGGER = Logger.getLogger(AKCustomListCellRenderer.class);

    private final Class<T> clazz;
    private final Function<T, String> render;

    public AKCustomListCellRenderer(Class<T> clazz, Function<T, String> render) {
        super();
        this.clazz = clazz;
        this.render = render;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value == null) {
            return label;
        }

        final String renderedValue = renderObject(value);
        final String valueToShow = StringUtils.defaultIfBlank(renderedValue, " ");
        label.setText(valueToShow);
        return label;
    }

    private String renderObject(Object o) {
        try {
            final T valueAsT = clazz.cast(o);
            return render.apply(valueAsT);
        }
        catch (Exception e) {
            // TODO(thomasfr, 2016.01): Exception anstatt Fehler im Log verstecken
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}

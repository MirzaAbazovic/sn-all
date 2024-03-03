/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2004 10:27:51
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.lang.reflect.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Implementierung eines ListCellRenderer, der unterschiedliche Objekte anzeigen kann. Der anzuzeigende Wert wird ueber
 * Reflection ermittelt.
 *
 *
 */
public class AKReflectionListCellRenderer extends DefaultListCellRenderer {

    private static final Logger LOGGER = Logger.getLogger(AKReflectionListCellRenderer.class);

    private Class<?> type = null;
    private Method method = null;

    /**
     * Konstruktor fuer den ListCellRenderer.
     *
     * @param type   Angabe des Typs, der 'gerendert' werden soll.
     * @param methodName Methodenname, ueber den der anzuzeigende Text ermittelt werden soll.
     */
    public AKReflectionListCellRenderer(Class<?> type, String methodName) {
        super();
        this.type = type;
        try {
            this.method = type.getMethod(methodName, new Class[] { });
        }
        catch (SecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }
        catch (NoSuchMethodException e) {
            LOGGER.warn("Methode " + methodName + " ist nicht verfuegbar!");
        }
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean,
     * boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                Object retVal = null;
                try {
                    retVal = method.invoke(value, new Object[] { });
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if ((retVal != null) && StringUtils.isNotEmpty(retVal.toString())) {
                    label.setText(retVal.toString());
                }
                else {
                    label.setText(" ");
                }
            }
            else {
                LOGGER.warn("Anzuzeigendes Objekt ist nicht vom erwarteten Typ " + type.getName() + " sondern von " +
                        value.getClass().getName());
            }
        }

        return label;
    }
}

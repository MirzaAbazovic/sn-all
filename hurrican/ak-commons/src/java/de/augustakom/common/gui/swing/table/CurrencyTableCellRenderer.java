/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2005 13:55:12
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.common.tools.lang.CurrencyTools;


/**
 * TableCellRenderer zur Darstellung von Waehrungen.
 *
 *
 */
public class CurrencyTableCellRenderer extends DefaultTableCellRenderer {

    private Locale locale = null;

    /**
     * Default-Konstruktor.
     */
    public CurrencyTableCellRenderer() {
        super();
    }

    /**
     * Konstruktor mit Angabe des zu verwendenden Locales.
     */
    public CurrencyTableCellRenderer(Locale locale) {
        super();
        this.locale = locale;
    }

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
     * boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Number) {
            l.setText(CurrencyTools.getAsCurrency((Number) value, locale));
            l.setHorizontalAlignment(JLabel.RIGHT);
        }

        return l;
    }
}



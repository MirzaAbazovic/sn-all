/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2004 10:02:42
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.common.tools.lang.DateTools;


/**
 * TableCellRenderer, um Date-Objekte in einer Tabelle darzustellen.
 *
 *
 */
public class DateTableCellRenderer extends DefaultTableCellRenderer {

    private String pattern = null;

    /**
     * Konstruktor mit Angabe des Format-Patterns fuer das Date-Objekt. <br> Als Pattern sollte eine Konstante aus
     * <code>DateTools</code> verwendet werden.
     */
    public DateTableCellRenderer(String pattern) {
        super();
        this.pattern = pattern;
    }

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
     * boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Date) {
            l.setText(DateTools.formatDate((Date) value, pattern));
        }

        return l;
    }
}



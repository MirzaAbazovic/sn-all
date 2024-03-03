/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2007 13:49:42
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;


/**
 * TableCellRenderer, um Float-Objekte gemäß einem Pattern in einer Tabelle darzustellen.
 *
 *
 */
public class FloatTableCellRenderer extends DefaultTableCellRenderer {

    private String pattern = null;

    /**
     * Konstruktor mit Angabe des Format-Patterns fuer das Float-Objekt. <br>
     */
    public FloatTableCellRenderer(String pattern) {
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

        if (value instanceof Float) {
            DecimalFormat df = new DecimalFormat(pattern);
            l.setText(df.format(value));
            l.setHorizontalAlignment(JLabel.RIGHT);
        }

        return l;
    }
}



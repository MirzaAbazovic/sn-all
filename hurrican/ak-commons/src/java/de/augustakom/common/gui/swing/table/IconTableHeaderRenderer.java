/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2004 14:48:44
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;


/**
 * Renderer fuer TableHeaders, um nur ein Icon darzustellen. <br> Der Renderer kann einem Header wie folgt zugeordnet
 * werden: <br> <code> table.getColumnModel().getColumn(0).setHeaderRenderer(new IconTableHeaderRenderer()); </code>
 * <br><br> Ein Icon wird dem Renderer wie folgt uebergeben: <br> <code> table.getColumnModel().getColumn(0).setHeaderValue(icon);
 * </code>
 *
 *
 */
public class IconTableHeaderRenderer extends DefaultTableCellRenderer {

    /**
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
     * boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Inherit the colors and font from the header component
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }

        if (value instanceof Icon) {
            setIcon((Icon) value);
        }
        else {
            setIcon(null);
        }

        setText(" ");
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(JLabel.CENTER);
        return this;
    }
}


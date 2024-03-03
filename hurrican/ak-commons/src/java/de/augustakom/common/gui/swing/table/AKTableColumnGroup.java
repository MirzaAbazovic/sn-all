/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2004 10:39:27
 */

package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Implementierung einer ColumnGroup fuer JTableHeaders. <br> Download unter: <br>
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html <br><br> Beispiel: see
 * de.augustakom.common.gui.swing.table.AKGroupableTableHeader.java
 */
public class AKTableColumnGroup {

    protected TableCellRenderer renderer;
    protected Vector v;
    protected String text;
    protected int margin = 0;

    /**
     * Konstruktor mit Angabe eines Textes fuer die Column-Group.
     *
     * @param text
     */
    public AKTableColumnGroup(String text) {
        this(null, text);
    }

    /**
     * Konstruktor mit Angabe von Text und Renderer
     *
     * @param renderer
     * @param text
     */
    public AKTableColumnGroup(TableCellRenderer renderer, String text) {
        if (renderer == null) {
            this.renderer = new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable table,
                        Object value,
                        boolean isSelected,
                        boolean hasFocus,
                        int row,
                        int column) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    setText((value == null) ? "" : value.toString());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    return this;
                }
            };
        }
        else {
            this.renderer = renderer;
        }
        this.text = text;
        v = new Vector();
    }

    /**
     * @param obj TableColumn or ColumnGroup
     */
    public void add(Object obj) {
        if (obj == null) {
            return;
        }
        v.addElement(obj);
    }

    /**
     * @param c TableColumn
     * @param v ColumnGroups
     */
    public Vector getColumnGroups(TableColumn c, Vector g) {
        g.addElement(this);
        if (v.contains(c)) {
            return g;
        }
        Enumeration enumeration = v.elements();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof AKTableColumnGroup) {
                Vector groups = ((AKTableColumnGroup) obj).getColumnGroups(c, (Vector) g.clone());
                if (groups != null) {
                    return groups;
                }
            }
        }
        return null;
    }

    public TableCellRenderer getHeaderRenderer() {
        return renderer;
    }

    public void setHeaderRenderer(TableCellRenderer renderer) {
        if (renderer != null) {
            this.renderer = renderer;
        }
    }

    public Object getHeaderValue() {
        return text;
    }

    public Dimension getSize(JTable table) {
        Component comp =
                renderer.getTableCellRendererComponent(
                        table,
                        getHeaderValue(),
                        false,
                        false,
                        -1,
                        -1);
        int height = comp.getPreferredSize().height;
        int width = 0;
        Enumeration enumeration = v.elements();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof TableColumn) {
                TableColumn aColumn = (TableColumn) obj;
                width += aColumn.getWidth();
                width += margin;
            }
            else {
                width += ((AKTableColumnGroup) obj).getSize(table).width;
            }
        }
        return new Dimension(width, height);
    }

    public void setColumnMargin(int margin) {
        this.margin = margin;
        Enumeration enumeration = v.elements();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof AKTableColumnGroup) {
                ((AKTableColumnGroup) obj).setColumnMargin(margin);
            }
        }
    }
}

/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2004 10:41:45
 */

package de.augustakom.common.gui.swing.table;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

/**
 * UI-Klasse fuer einen gruppierbaren TableHeader. Download unter: <br> http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html
 * <br><br> Beispiel: see de.augustakom.common.gui.swing.table.AKGroupableTableHeader.java
 */
public class AKGroupableTableHeaderUI extends BasicTableHeaderUI {


    public void paint(Graphics g, JComponent jcomponent) {
        Rectangle rectangle = g.getClipBounds();
        if (super.header.getColumnModel() == null) {
            return;
        }
        int i = 0;
        Dimension dimension = super.header.getSize();
        Rectangle rect = new Rectangle(0, 0, dimension.width, dimension.height);
        Map<AKTableColumnGroup, Rectangle> hashtable = new HashMap<>();
        int colMargin = super.header.getColumnModel().getColumnMargin();
        for (Enumeration enumeration = super.header.getColumnModel().getColumns(); enumeration.hasMoreElements(); ) {
            rect.height = dimension.height;
            rect.y = 0;
            TableColumn tablecolumn = (TableColumn) enumeration.nextElement();
            Enumeration enumeration1 = ((AKGroupableTableHeader) super.header).getColumnGroups(tablecolumn);
            if (enumeration1 != null) {
                int k = 0;
                while (enumeration1.hasMoreElements()) {
                    AKTableColumnGroup columngroup = (AKTableColumnGroup) enumeration1.nextElement();
                    Rectangle rectangle2 = (Rectangle) hashtable.get(columngroup);
                    if (rectangle2 == null) {
                        rectangle2 = new Rectangle(rect);
                        Dimension dimension1 = columngroup.getSize(super.header.getTable());
                        rectangle2.width = dimension1.width;
                        rectangle2.width -= colMargin;
                        rectangle2.height = dimension1.height;
                        hashtable.put(columngroup, rectangle2);
                    }
                    paintCell(g, rectangle2, columngroup);
                    k += rectangle2.height;
                    rect.height = dimension.height - k;
                    rect.y = k;
                }
            }
            rect.width = tablecolumn.getWidth() - colMargin;
            if (rect.intersects(rectangle)) {
                paintCell(g, rect, i);
            }
            rect.x += tablecolumn.getWidth();
            i++;
        }

    }

    private void paintCell(Graphics g, Rectangle rectangle, int i) {
        TableColumn tablecolumn = super.header.getColumnModel().getColumn(i);
        TableCellRenderer obj = header.getDefaultRenderer();
        Component component = obj.getTableCellRendererComponent(super.header.getTable(), tablecolumn.getHeaderValue(), false, false, -1, i);
        super.rendererPane.add(component);
        super.rendererPane.paintComponent(g, component, super.header, rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
    }

    private void paintCell(Graphics g, Rectangle rectangle, AKTableColumnGroup columngroup) {
        TableCellRenderer tablecellrenderer = columngroup.getHeaderRenderer();
        Component component = tablecellrenderer.getTableCellRendererComponent(super.header.getTable(), columngroup.getHeaderValue(), false, false, -1, -1);
        super.rendererPane.add(component);
        super.rendererPane.paintComponent(g, component, super.header, rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
    }

    private int getHeaderHeight() {
        int i = 0;
        TableColumnModel tablecolumnmodel = super.header.getColumnModel();
        for (int j = 0; j < tablecolumnmodel.getColumnCount(); j++) {
            TableColumn tablecolumn = tablecolumnmodel.getColumn(j);
            TableCellRenderer obj = header.getDefaultRenderer();
            Component component = obj.getTableCellRendererComponent(super.header.getTable(), tablecolumn.getHeaderValue(), false, false, -1, j);
            int k = component.getPreferredSize().height;
            Enumeration enumeration = ((AKGroupableTableHeader) super.header).getColumnGroups(tablecolumn);
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    AKTableColumnGroup columngroup = (AKTableColumnGroup) enumeration.nextElement();
                    k += columngroup.getSize(super.header.getTable()).height;
                }
            }
            i = Math.max(i, k);
        }

        return i;
    }

    private Dimension getPreferedSizeHelper(long width) {
        long width1 = width;
        TableColumnModel tablecolumnmodel = super.header.getColumnModel();
        width1 += tablecolumnmodel.getColumnMargin() * tablecolumnmodel.getColumnCount();
        if (width1 > Integer.MAX_VALUE) {
            width1 = Integer.MAX_VALUE;
        }
        return new Dimension((int) width1, getHeaderHeight());
    }

    public Dimension getPreferredSize(JComponent jcomponent) {
        long width = 0;
        for (Enumeration enumeration = super.header.getColumnModel().getColumns(); enumeration.hasMoreElements(); ) {
            TableColumn tablecolumn = (TableColumn) enumeration.nextElement();
            width += tablecolumn.getPreferredWidth();
        }

        return getPreferedSizeHelper(width);
    }
}

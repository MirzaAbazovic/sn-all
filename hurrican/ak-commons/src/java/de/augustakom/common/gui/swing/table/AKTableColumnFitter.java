/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.04.2006 10:21:13
 */
package de.augustakom.common.gui.swing.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

/**
 * Hilfsklasse, um die Spalten einer Tabelle auf die benoetigte Groesse zu setzen. <br> Es wird von jeder Spalte der
 * laengste Inhalt ermittelt und dieser als Spaltengroesse gesetzt.
 *
 *
 */
public class AKTableColumnFitter {

    private static final int DEFAULT_COLUMN_PADDING = 10;

    /*
     * @param JTable table, the JTable to autoresize the columns on
     * @param boolean includeColumnHeaderWidth, use the Column Header width as a minimum width
     * @returns The table width, just in case the caller wants it...
     */
    public static int autoResizeTable(JTable table, boolean includeColumnHeaderWidth) {
        return autoResizeTable(table, includeColumnHeaderWidth, DEFAULT_COLUMN_PADDING);
    }

    /*
     * @param JTable table, the JTable to autoresize the columns on
     * @param boolean includeColumnHeaderWidth, use the Column Header width as a minimum width
     * @param int columnPadding, how many extra pixels do you want on the end of each column
     * @returns The table width, just in case the caller wants it...
     */
    public static int autoResizeTable(JTable table, boolean includeColumnHeaderWidth, int columnPadding) {
        int columnCount = table.getColumnCount();
        int tableWidth = 0;

        Dimension cellSpacing = table.getIntercellSpacing();
        if (columnCount > 0) {
            // STEP ONE : Work out the column widths
            int columnWidth[] = new int[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnWidth[i] = getMaxColumnWidth(table, i, true, columnPadding);
                tableWidth += columnWidth[i];
            }

            // account for cell spacing too
            tableWidth += ((columnCount - 1) * cellSpacing.width);

            // STEP TWO : Dynamically resize each column

            // try changing the size of the column names area
            JTableHeader tableHeader = table.getTableHeader();
            Dimension headerDim = tableHeader.getPreferredSize();

            headerDim.width = tableWidth;
            tableHeader.setPreferredSize(headerDim);

            Dimension interCellSpacing = table.getIntercellSpacing();
            Dimension dim = new Dimension();
            int rowHeight = table.getRowHeight();

            if (rowHeight == 0) {
                rowHeight = 16;    // default rowheight
            }

            dim.height = headerDim.height + ((rowHeight + interCellSpacing.height) * table.getRowCount());
            dim.width = tableWidth;

            TableColumnModel tableColumnModel = table.getColumnModel();
            TableColumn tableColumn;

            for (int i = 0; i < columnCount; i++) {
                tableColumn = tableColumnModel.getColumn(i);
                tableColumn.setPreferredWidth(columnWidth[i]);
            }

            table.invalidate();
            table.doLayout();
            table.repaint();
        }
        return tableWidth;
    }

    /*
     * @param JTable table, the JTable to autoresize the columns on
     * @param int columnNo, the column number, starting at zero, to calculate the maximum width on
     * @param boolean includeColumnHeaderWidth, use the Column Header width as a minimum width
     * @param int columnPadding, how many extra pixels do you want on the end of each column
     * @returns The table width, just in case the caller wants it...
     */
    private static int getMaxColumnWidth(JTable table, int columnNo,
            boolean includeColumnHeaderWidth, int columnPadding) {
        TableColumn column = table.getColumnModel().getColumn(columnNo);
        Component comp = null;
        int maxWidth = 0;

        if (includeColumnHeaderWidth) {
            TableCellRenderer headerRenderer = column.getHeaderRenderer();
            if (headerRenderer != null) {
                comp = headerRenderer.getTableCellRendererComponent(
                        table, column.getHeaderValue(), false, false, 0, columnNo);
                if (comp instanceof JTextComponent) {
                    JTextComponent jtextComp = (JTextComponent) comp;

                    String text = jtextComp.getText();
                    Font font = jtextComp.getFont();
                    FontMetrics fontMetrics = jtextComp.getFontMetrics(font);

                    maxWidth = SwingUtilities.computeStringWidth(fontMetrics, text);
                }
                else {
                    maxWidth = comp.getPreferredSize().width;
                }
            }
            else {
                try {
                    String headerText = (String) column.getHeaderValue();
                    JLabel defaultLabel = new JLabel(headerText);

                    Font font = defaultLabel.getFont();
                    FontMetrics fontMetrics = defaultLabel.getFontMetrics(font);

                    maxWidth = SwingUtilities.computeStringWidth(fontMetrics, headerText);
                }
                catch (ClassCastException ce) {
                    // Can't work out the header column width..
                    maxWidth = 0;
                }
            }
        }

        TableCellRenderer tableCellRenderer;
        // Component comp;
        int cellWidth = 0;

        for (int i = 0; i < table.getRowCount(); i++) {
            tableCellRenderer = table.getCellRenderer(i, columnNo);
            comp = tableCellRenderer.getTableCellRendererComponent(
                    table, table.getValueAt(i, columnNo), false, false, i, columnNo);

            if (comp instanceof JTextComponent) {
                JTextComponent jtextComp = (JTextComponent) comp;

                String text = jtextComp.getText();
                Font font = jtextComp.getFont();
                FontMetrics fontMetrics = jtextComp.getFontMetrics(font);

                int textWidth = SwingUtilities.computeStringWidth(fontMetrics, text);
                maxWidth = Math.max(maxWidth, textWidth);
            }
            else {
                cellWidth = comp.getPreferredSize().width;
                maxWidth = Math.max(maxWidth, cellWidth);
            }
        }

        return maxWidth + columnPadding;
    }
}

/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import java.awt.*;
import java.sql.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;

/**
 *
 */
public class CPSTable extends AKJTable {

    /**
     * @param resource
     * @throws HurricanGUIException
     */
    public CPSTable(String resource) {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        @SuppressWarnings("rawtypes")
        // CPSTable selbst wird in diversen Panels instanziiert (CPSTxLogPanel,
                // CPSTxOrderPanel, CPSTxTable).
                // Diese Panels nutzen jeweils ihr eigenes TableModel bzw. ihre eigene
                // Typvariable T für AKTableModelXML<T>.
                AKTableModelXML tableModel = new AKTableModelXML(resource);
        setModel(tableModel);
        attachSorter();
        setFilterEnabled(Boolean.TRUE);
        CPSTableDefaultRenderer numberRenderer = new CPSTableNumberRenderer();
        setDefaultRenderer(String.class, new CPSTableDefaultRenderer());
        setDefaultRenderer(Long.class, numberRenderer);
        setDefaultRenderer(Integer.class, numberRenderer);
        setDefaultRenderer(Timestamp.class, new CPSTxTableTimestampRenderer());
    }

    /*
     * Default Renderer for CPSTable
     */
    static class CPSTableDefaultRenderer extends DefaultTableCellRenderer {

        final static String ARIAL = "Arial";
        final static Color SELECTION_COLOR = new Color(0, 0, 100);
        final static Color ERROR_COLOR = Color.RED;

        /**
         * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
         * java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(isSelected ? new Font(CPSTableDefaultRenderer.ARIAL, Font.BOLD, getFont().getSize()) : getFont());

            if (((AKTableSorter<?>) table.getModel()).getDataAtRow(row) instanceof CPSTransactionExt) {
                @SuppressWarnings("unchecked")
                CPSTransactionExt cpsTransactionExt = ((AKTableSorter<CPSTransactionExt>) table.getModel()).getDataAtRow(row);

                if (cpsTransactionExt.getTxState().equals(CPSTransactionExt.TX_STATE_FAILURE) ||
                        cpsTransactionExt.getTxState().equals(CPSTransactionExt.TX_STATE_TRANSMISSION_FAILURE)) {
                    setBackground(isSelected ? SELECTION_COLOR : ERROR_COLOR);
                }
                else {
                    if (!isSelected) {
                        setBackground(row % 2 == 0 ? getEvenRowBGColor() : getOddRowBGColor());
                    }
                }
            }
            return component;
        }
    }

    /*
     * Number Renderer for CPSTable
     */
    static class CPSTableNumberRenderer extends CPSTableDefaultRenderer {

        /*
         * Default-Konstruktor
         */
        public CPSTableNumberRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }
    }

    /*
     * Timestamp Renderer for CPSTable
     *
     *
     *
     */
    static class CPSTxTableTimestampRenderer extends CPSTableDefaultRenderer {

        /**
         * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
         */
        @Override
        public void setValue(Object value) {
            setText(null != value ? DateFormat.getDateTimeInstance().format(value) : "");
        }
    }
}

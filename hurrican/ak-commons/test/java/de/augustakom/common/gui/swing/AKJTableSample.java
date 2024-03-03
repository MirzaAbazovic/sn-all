/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 15:20:03
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.table.AKTableModel;


/**
 * Test-Klasse fuer eine Tabelle.
 *
 *
 */
public class AKJTableSample extends AKJFrame {

    private SimpleTM tbMdl = null;
    private AKJTable table = null;

    public static void main(String[] args) {
        AKJTableSample sample = new AKJTableSample();
        sample.startTest();
    }

    public void startTest() {
        setTitle("AKJTable-Test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tbMdl = new SimpleTM();
        table = new AKJTable(tbMdl);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(true);
        AKJScrollPane spTable = new AKJScrollPane(table);
        spTable.setPreferredSize(new Dimension(400, 250));

        AKJComboBox cb = new AKJComboBox(new Object[] { "test1", "test2", "test3" });
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cb));

        this.add(spTable);
        this.setVisible(true);
        this.pack();

        createTestData();
    }

    private void createTestData() {
        ArrayList data = new ArrayList();
        for (int i = 0; i < 20; i++) {
            Boolean b = (i < 10) ? Boolean.FALSE : Boolean.TRUE;
            SimpleModel sm = new SimpleModel("test" + i, Integer.valueOf(i), new Date(), null, b);
            data.add(sm);
        }
        tbMdl.setData(data);
    }

    class SimpleTM extends AKTableModel {
        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 5;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "String";
                case 1:
                    return "Integer";
                case 2:
                    return "Date";
                case 3:
                    return "null";
                case 4:
                    return "Boolean";
            }
            return " ";
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int column) {
            SimpleModel sm = (SimpleModel) getDataAtRow(row);
            switch (column) {
                case 0:
                    return sm.s;
                case 1:
                    return sm.i;
                case 2:
                    return sm.d;
                case 3:
                    return sm.nil;
                case 4:
                    return sm.b;
            }
            return super.getValueAt(row, column);
        }

        /**
         * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
         */
        public void setValueAt(Object aValue, int row, int column) {
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int column) {
            return true;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return Integer.class;
                case 2:
                    return Date.class;
                case 4:
                    return Boolean.class;
            }
            return super.getColumnClass(columnIndex);
        }
    }

    class SimpleModel {
        String s = null;
        Integer i = null;
        Date d = null;
        Object nil = null;
        Boolean b = null;

        SimpleModel(String s, Integer i, Date d, Object nil, Boolean b) {
            this.s = s;
            this.i = i;
            this.d = d;
            this.nil = nil;
            this.b = b;
        }
    }

}



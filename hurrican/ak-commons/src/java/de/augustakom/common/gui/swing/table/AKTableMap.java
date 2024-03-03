/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2004 10:29:53
 */

/**
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behavour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting
 * a TableMap which has not been subclassed into a chain of table filters
 * should have no effect.
 */
package de.augustakom.common.gui.swing.table;

import javax.swing.event.*;
import javax.swing.table.*;

public class AKTableMap extends AbstractTableModel implements TableModelListener {

    protected TableModel model;

    /**
     * Gibt das TableModel zurueck
     *
     * @return
     */
    public TableModel getModel() {
        return model;
    }

    /**
     * Setzt das TableModel.
     *
     * @param model
     */
    public void setModel(TableModel model) {
        this.model = model;
        if (model != null) {
            model.addTableModelListener(this);
        }
    }

    // By default, implement TableModel by forwarding all messages
    // to the model.

    public Object getValueAt(int aRow, int aColumn) {
        return model.getValueAt(aRow, aColumn);
    }

    public void setValueAt(Object aValue, int aRow, int aColumn) {
        model.setValueAt(aValue, aRow, aColumn);
    }

    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount();
    }

    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount();
    }

    public String getColumnName(int aColumn) {
        return model.getColumnName(aColumn);
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
        return model.getColumnClass(columnIndex);
    }

    public boolean isCellEditable(int row, int column) {
        return model.isCellEditable(row, column);
    }

    /**
     * Implementation of the TableModelListener interface, By default forward all events to all the listeners.
     */
    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
    }

}

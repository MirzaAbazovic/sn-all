/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 16:24:54
 */
package de.augustakom.hurrican.gui.hvt;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.PhysikTyp;


/**
 * TableModel fuer die Darstellung der Produkt-Gruppen, die ueber einen best. HVT-Standort beschaltet werden koennen.
 *
 *
 */
public class PhysikTypen4HVTTableModel extends AKTableModel {

    protected static final int COL_ID = 0;
    protected static final int COL_NAME = 1;

    private static final int COL_COUNT = 2;

    public PhysikTypen4HVTTableModel() {
        super("de.augustakom.hurrican.gui.hvt.resources.PhysikTypen4HVTTableModel");
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return COL_COUNT;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        switch (column) {
            case COL_ID:
                return getResourceReader().getValue("header.id");
            case COL_NAME:
                return getResourceReader().getValue("header.name");
            default:
                return " ";
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof PhysikTyp) {
            PhysikTyp pt = (PhysikTyp) tmp;
            switch (column) {
                case COL_ID:
                    return pt.getId();
                case COL_NAME:
                    return pt.getName();
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}



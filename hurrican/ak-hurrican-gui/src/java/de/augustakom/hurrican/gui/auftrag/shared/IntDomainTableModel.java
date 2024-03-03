/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2004 08:38:47
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.billing.view.IntDomain;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>IntDomain</code>.
 *
 *
 */
public class IntDomainTableModel extends AKTableModel<IntDomain> {

    private static final int COL_DOMAIN = 0;
    private static final int COL_CONFIRM_DATE = 1;
    private static final int COL_DELETE_DATE = 2;

    private static final int COL_COUNT = 3;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_DOMAIN:
                return "Domain";
            case COL_CONFIRM_DATE:
                return "Bestätigt am";
            case COL_DELETE_DATE:
                return "Gelöscht am";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof IntDomain) {
            IntDomain domain = (IntDomain) o;
            switch (column) {
                case COL_DOMAIN:
                    return domain.getDomainName();
                case COL_CONFIRM_DATE:
                    return domain.getConfirmDate();
                case COL_DELETE_DATE:
                    return domain.getDeleteDate();
                default:
                    break;
            }
        }
        return super.getValueAt(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_DOMAIN) {
            return String.class;
        }
        else {
            return Date.class;
        }
    }
}



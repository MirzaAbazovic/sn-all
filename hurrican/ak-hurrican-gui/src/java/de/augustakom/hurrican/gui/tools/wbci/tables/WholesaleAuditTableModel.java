/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 14.02.2017

 */

package de.augustakom.hurrican.gui.tools.wbci.tables;


import de.augustakom.common.gui.swing.table.AKTableModel;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;

/**
 * Tablemodel for {@link de.mnet.hurrican.wholesale.model.WholesaleAudit}s.
 */
public class WholesaleAuditTableModel extends AKTableModel<WholesaleAudit> {

    public static final int COL_GESCHAEFTSFALL = 0;
    public static final int COL_BEARBEITER = 1;
    public static final int COL_DATUM = 2;
    public static final int COL_STATUS = 3;

    private static final int COLUMN_COUNT = 4;


    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_GESCHAEFTSFALL:
                return "Geschäftsfall";
            case COL_BEARBEITER:
                return "Bearbeiter";
            case COL_DATUM:
                return "Datum";
            case COL_STATUS:
                return "Status";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        WholesaleAudit audit = getDataAtRow(row);

        Object result = null;
        if (audit != null) {
            switch (column) {
                case COL_GESCHAEFTSFALL:
                    result = audit.getBeschreibung();
                    break;
                case COL_BEARBEITER:
                    result = audit.getBearbeiter();
                    break;
                case COL_DATUM:
                    result = audit.getDatum();
                    break;
                case COL_STATUS:
                    result = audit.getStatus();
                    break;
                default:
                    result = null;
            }
        }

        return result;
    }

    @Override
    public boolean isCellEditable(int i, int j) {
        return false; //Table is not editable at all.
    }
}

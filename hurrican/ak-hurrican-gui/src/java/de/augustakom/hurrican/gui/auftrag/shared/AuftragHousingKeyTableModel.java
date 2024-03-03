/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2011 11:45:27
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;


/**
 * TableModel fuer die Anzeige von Schluesseln {@link AuftragHousingKey}
 */
public class AuftragHousingKeyTableModel extends AKTableModel<AuftragHousingKeyView> {

    private static final int COL_TRANSPONDER_ID = 0;
    private static final int COL_FIRSTNAME = 1;
    private static final int COL_LASTNAME = 2;
    private static final int COL_TRANSPONDER_GROUP_ID = 3;

    private static final int COL_COUNT = 4;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_TRANSPONDER_ID:
                return AuftragHousingKey.TRANSPONDER_BEZEICHNUNG;
            case COL_FIRSTNAME:
                return AuftragHousingKey.CUSTOMERFIRSTNAME_BEZEICHNUNG;
            case COL_LASTNAME:
                return AuftragHousingKey.CUSTOMERLASTNAME_BEZEICHNUNG;
            case COL_TRANSPONDER_GROUP_ID:
                return AuftragHousingKey.TRANSPONDER_GROUP_BEZEICHNUNG;
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof AuftragHousingKeyView) {
            AuftragHousingKeyView dataset = (AuftragHousingKeyView) tmp;
            switch (column) {
                case COL_TRANSPONDER_ID:
                    return dataset.getTransponderId();
                case COL_FIRSTNAME:
                    return dataset.getCustomerFirstName();
                case COL_LASTNAME:
                    return dataset.getCustomerLastName();
                case COL_TRANSPONDER_GROUP_ID:
                    return dataset.getTransponderGroupDescription();
                default:
                    return "";
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
        if (columnIndex == COL_TRANSPONDER_ID) {
            return Long.class;
        }
        else {
            return String.class;
        }
    }

}



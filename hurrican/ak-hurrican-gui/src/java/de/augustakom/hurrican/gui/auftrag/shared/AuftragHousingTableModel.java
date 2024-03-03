/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2011 14:01:15
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.view.CCAuftragHousingView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>AuftragHousingView</code>
 *
 *
 */
public class AuftragHousingTableModel extends AKTableModel<CCAuftragHousingView> {

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_KUNDE_NO = 1;
    private static final int COL_TRANSPONDER_ID = 2;
    private static final int COL_FIRSTNAME = 3;
    private static final int COL_LASTNAME = 4;

    private static final int COL_COUNT = 5;


    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_AUFTRAG_ID:
                return "Auftrag-ID (CC)";
            case COL_KUNDE_NO:
                return "Kunde__No";
            case COL_TRANSPONDER_ID:
                return AuftragHousingKey.TRANSPONDER_BEZEICHNUNG;
            case COL_FIRSTNAME:
                return AuftragHousingKey.CUSTOMERFIRSTNAME_BEZEICHNUNG;
            case COL_LASTNAME:
                return AuftragHousingKey.CUSTOMERLASTNAME_BEZEICHNUNG;
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_AUFTRAG_ID:
                return Long.class;
            case COL_KUNDE_NO:
                return Long.class;
            case COL_TRANSPONDER_ID:
                return Integer.class;
            case COL_FIRSTNAME:
                return String.class;
            case COL_LASTNAME:
                return String.class;
            default:
                return super.getColumnClass(columnIndex);
        }
    }


    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof CCAuftragHousingView) {
            CCAuftragHousingView housingView = (CCAuftragHousingView) o;
            switch (column) {
                case COL_AUFTRAG_ID:
                    return housingView.getAuftragId();
                case COL_KUNDE_NO:
                    return housingView.getKundeNo();
                case COL_TRANSPONDER_ID:
                    return housingView.getTransponderId();
                case COL_FIRSTNAME:
                    return housingView.getCustomerFirstName();
                case COL_LASTNAME:
                    return housingView.getCustomerLastName();
                default:
                    break;
            }
        }
        return super.getValueAt(row, column);
    }
}

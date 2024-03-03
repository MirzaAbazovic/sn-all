/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2013
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.shared.view.WbciRequestCarrierView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>WbciRequestCarrierView</code>
 */
public class WbciRequestCarrierViewTableModel extends AKTableModel<WbciRequestCarrierView> {

    private static final long serialVersionUID = -8773996595094312404L;

    private static final int COL_VA_ID = 0;
    private static final int COL_AENDERUNGS_ID = 1;
    private static final int COL_AUFTRAG_ID = 2;
    private static final int COL_BILLING_ORDER__NO = 3;
    private static final int COL_GF_TYP = 4;
    private static final int COL_TERMIN = 5;
    private static final int COL_EKP_ABG = 6;
    private static final int COL_EKP_AUF = 7;
    private static final int COL_STATUS = 8;

    private static final int COL_COUNT = 9;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_VA_ID:
                return "Vorabstimmungs-Id";
            case COL_AENDERUNGS_ID:
                return "Änderungs-Id";
            case COL_AUFTRAG_ID:
                return "(CC) Auftrag Id";
            case COL_BILLING_ORDER__NO:
                return "Taifun Auftrags Id";
            case COL_GF_TYP:
                return "Geschäftsfall";
            case COL_TERMIN:
                return "KW-Termin";
            case COL_EKP_ABG:
                return "EKP abg";
            case COL_EKP_AUF:
                return "EKP auf";
            case COL_STATUS:
                return "Status";
            default:
                return " ";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof WbciRequestCarrierView) {
            WbciRequestCarrierView view = (WbciRequestCarrierView) o;
            switch (column) {
                case COL_VA_ID:
                    return view.getVorabstimmungsId();
                case COL_AENDERUNGS_ID:
                    return view.getAenderungsId();
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_BILLING_ORDER__NO:
                    return view.getBillingOrderNoOrig();
                case COL_GF_TYP:
                    return view.getGeschaeftsfallTyp();
                case COL_TERMIN:
                    return view.getKundenwunschtermin();
                case COL_EKP_ABG:
                    return view.getAbgebenderEkp();
                case COL_EKP_AUF:
                    return view.getAufnehmenderEkp();
                case COL_STATUS:
                    return view.getStatus();
                default:
                    break;
            }
        }
        return null;
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
            case COL_BILLING_ORDER__NO:
                return Long.class;
            case COL_TERMIN:
                return Date.class;
            default:
                return String.class;
        }
    }
}

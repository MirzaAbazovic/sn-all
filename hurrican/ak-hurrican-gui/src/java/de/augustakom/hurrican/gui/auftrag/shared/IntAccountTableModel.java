/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2004 13:32:51
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.AccountArt;
import de.augustakom.hurrican.model.cc.IntAccount;


/**
 * TableModel fuer die Darstellung von IntAccount-Objekten.
 *
 *
 */
public class IntAccountTableModel extends AKTableModel<IntAccount> {

    private static final int COL_ACCOUNT = 0;
    private static final int COL_ACC_TEXT = 1;
    private static final int COL_RN = 2;
    private static final int COL_GESPERRT = 3;
    private static final int COL_ACC_ID = 4;

    private static final int COL_COUNT = 5;

    private Map<Integer, String> accArtMap = null;

    /**
     * Uebergibt dem TableModel eine Liste mit allen moglichen Account-Arten.
     *
     * @param accountArten
     */
    public void setAccountArten(List<AccountArt> accountArten) {
        accArtMap = new HashMap<Integer, String>();
        if (accountArten != null) {
            for (AccountArt accArt : accountArten) {
                accArtMap.put(accArt.getLiNr(), accArt.getText());
            }
        }
    }

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_ACCOUNT:
                return "Account";
            case COL_ACC_TEXT:
                return "Account-Typ";
            case COL_RN:
                return "Rufnummer";
            case COL_GESPERRT:
                return "Gesperrt";
            case COL_ACC_ID:
                return "Account-ID";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof IntAccount) {
            IntAccount acc = (IntAccount) o;
            switch (column) {
                case COL_ACCOUNT:
                    return acc.getAccount();
                case COL_ACC_TEXT:
                    return getAccText(acc.getLiNr());
                case COL_RN:
                    return acc.getRufnummer();
                case COL_GESPERRT:
                    return acc.getGesperrt();
                case COL_ACC_ID:
                    return acc.getId();
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
        switch (columnIndex) {
            case COL_GESPERRT:
                return Boolean.class;
            case COL_ACC_ID:
                return Long.class;
            default:
                return String.class;
        }
    }

    /* Gibt den Text fuer eine Account-Art zurueck. */
    private String getAccText(Integer accTyp) {
        String accountArt = accArtMap.get(accTyp);
        return accountArt;
    }
}



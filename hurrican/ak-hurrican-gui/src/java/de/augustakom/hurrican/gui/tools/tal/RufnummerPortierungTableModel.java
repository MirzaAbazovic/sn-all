/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 18:01:07
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;

public class RufnummerPortierungTableModel extends AKTableModel<RufnummerPortierungSelection> {

    private static final long serialVersionUID = -8000320670497967922L;

    private static final int COL_SELECTED = 0;
    private static final int COL_ONKZ = 1;
    private static final int COL_DN_BASE = 2;
    private static final int COL_DIRECT_DIAL = 3;
    private static final int COL_RANGE_FROM = 4;
    private static final int COL_RANGE_TO = 5;
    public static final int COL_PORTIERUNG_AM = 6;
    public static final int COL_LAST_CARRIER = 7;

    private static final int COL_COUNT = 8;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_SELECTED:
                return "Portieren";
            case COL_ONKZ:
                return "ONKZ";
            case COL_DN_BASE:
                return "DN-Base";
            case COL_DIRECT_DIAL:
                return "Direct Dial";
            case COL_RANGE_FROM:
                return "Range from";
            case COL_RANGE_TO:
                return "Range to";
            case COL_PORTIERUNG_AM:
                return "Portierung am";
            case COL_LAST_CARRIER:
                return "Letzter Carrier";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        RufnummerPortierungSelection rps = getDataAtRow(row);
        Rufnummer r = rps.getRufnummer();
        switch (column) {
            case COL_SELECTED:
                return rps.getSelected();
            case COL_ONKZ:
                return r.getOnKz();
            case COL_DN_BASE:
                return r.getDnBase();
            case COL_DIRECT_DIAL:
                return r.getDirectDial();
            case COL_RANGE_FROM:
                return r.getRangeFrom();
            case COL_RANGE_TO:
                return r.getRangeTo();
            case COL_PORTIERUNG_AM:
                return r.getRealDate();
            case COL_LAST_CARRIER:
                return r.getLastCarrier();
            default:
                return super.getValueAt(row, column);
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column == COL_SELECTED) {
            RufnummerPortierungSelection rps = getDataAtRow(row);
            rps.setSelected(!rps.getSelected());
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == COL_SELECTED) {
            return true;
        }
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_SELECTED:
                return Boolean.class;
            case COL_ONKZ:
                return String.class;
            case COL_DN_BASE:
                return String.class;
            case COL_DIRECT_DIAL:
                return String.class;
            case COL_RANGE_FROM:
                return String.class;
            case COL_RANGE_TO:
                return String.class;
            case COL_PORTIERUNG_AM:
                return Date.class;
            case COL_LAST_CARRIER:
                return String.class;
            default:
                return String.class;
        }
    }

}

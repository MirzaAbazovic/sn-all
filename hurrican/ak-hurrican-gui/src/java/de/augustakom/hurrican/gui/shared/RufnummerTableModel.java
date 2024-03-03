/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2004 13:32:15
 */
package de.augustakom.hurrican.gui.shared;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.billing.Rufnummer;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>Rufnummer</code>
 *
 *
 */
public class RufnummerTableModel extends AKTableModel<Rufnummer> {

    private static final int COL_GUELTIG_VON = 0;
    private static final int COL_GUELTIG_BIS = 1;
    private static final int COL_HIST_STATUS = 2;
    private static final int COL_ONKZ = 3;
    private static final int COL_DN_BASE = 4;
    private static final int COL_DIRECT_DIAL = 5;
    private static final int COL_RANGE_FROM = 6;
    private static final int COL_RANGE_TO = 7;
    private static final int COL_REMARKS = 8;
    private static final int COL_MAIN_NR = 9;
    private static final int COL_STATUS = 10;
    private static final int COL_PORT_MODE = 11;
    private static final int COL_LAST_CARRIER = 12;
    private static final int COL_ACT_CARRIER = 13;
    private static final int COL_FUTURE_CARRIER = 14;
    public static final int COL_PORTIERUNG_AM = 15;
    public static final int COL_PORTIERUNG_VON = 16;
    public static final int COL_PORTIERUNG_BIS = 17;
    public static final int COL_OE__NO = 18;
    public static final int COL_DN_NO = 19;

    private static final int COL_COUNT = 20;

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_GUELTIG_VON:
                return "Gültig von";
            case COL_GUELTIG_BIS:
                return "Gültig bis";
            case COL_HIST_STATUS:
                return "Hist-State";
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
            case COL_REMARKS:
                return "Remarks";
            case COL_MAIN_NR:
                return "Haupt-Nr";
            case COL_STATUS:
                return "Status";
            case COL_PORT_MODE:
                return "Port-Mode";
            case COL_LAST_CARRIER:
                return "Last Carrier";
            case COL_ACT_CARRIER:
                return "Act Carrier";
            case COL_FUTURE_CARRIER:
                return "Future Carrier";
            case COL_PORTIERUNG_AM:
                return "Portierung am";
            case COL_PORTIERUNG_VON:
                return "Portierung von";
            case COL_PORTIERUNG_BIS:
                return "Portierung bis";
            case COL_OE__NO:
                return "DN-Typ";
            case COL_DN_NO:
                return "DN_NO";
            default:
                return "";
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        Rufnummer r = getDataAtRow(row);
        switch (column) {
            case COL_GUELTIG_VON:
                return r.getGueltigVon();
            case COL_GUELTIG_BIS:
                return r.getGueltigBis();
            case COL_HIST_STATUS:
                return r.getHistStatus();
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
            case COL_REMARKS:
                return r.getRemarks();
            case COL_MAIN_NR:
                return r.isMainNumber();
            case COL_STATUS:
                return r.getState();
            case COL_PORT_MODE:
                return r.getPortMode();
            case COL_LAST_CARRIER:
                return StringUtils.trimToEmpty(r.getLastCarrier());
            case COL_ACT_CARRIER:
                return StringUtils.trimToEmpty(r.getActCarrier());
            case COL_FUTURE_CARRIER:
                return StringUtils.trimToEmpty(r.getFutureCarrier());
            case COL_PORTIERUNG_AM:
                return r.getRealDate();
            case COL_PORTIERUNG_VON:
                return r.getPortierungVon();
            case COL_PORTIERUNG_BIS:
                return r.getPortierungBis();
            case COL_OE__NO:
                return r.getOeNoOrig();
            case COL_DN_NO:
                return r.getDnNo();
            default:
                return super.getValueAt(row, column);
        }
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_GUELTIG_VON:
                return Date.class;
            case COL_GUELTIG_BIS:
                return Date.class;
            case COL_MAIN_NR:
                return Boolean.class;
            case COL_PORTIERUNG_AM:
                return Date.class;
            case COL_PORTIERUNG_VON:
                return Date.class;
            case COL_PORTIERUNG_BIS:
                return Date.class;
            case COL_OE__NO:
                return Long.class;
            case COL_DN_NO:
                return Long.class;
            default:
                return String.class;
        }
    }

}



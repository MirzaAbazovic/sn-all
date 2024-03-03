/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 10:29:25
 */
package de.augustakom.hurrican.gui.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;


/**
 * TableModel fuer die Darstellung von Rufnummern-Daten und deren Leistungen.
 *
 *
 */
public class DNLeistungsViewTableModel extends AKTableModel<DNLeistungsView> {

    public static final int COL_DEFAULT_LEISTUNG = 0;
    private static final int COL_DN_BASE = 1;
    private static final int COL_LEISTUNG = 2;
    private static final int COL_AM_REAL = 3;
    private static final int COL_AM_USER_REAL = 4;
    private static final int COL_AM_KUEND = 5;
    private static final int COL_AM_USER_KUEND = 6;
    private static final int COL_EWSD_REAL = 7;
    private static final int COL_EWSD_USER_REAL = 8;
    private static final int COL_EWSD_KUEND = 9;
    private static final int COL_EWSD_USER_KUEND = 10;
    private static final int COL_PARAM = 11;
    private static final int COL_PARAM_BESCH = 12;
    private static final int COL_ONKZ = 13;
    private static final int COL_RANGE_FROM = 14;
    private static final int COL_RANGE_TO = 15;
    private static final int COL_PARAM_ID = 16;
    private static final int COL_DIRECT_DIAL = 17;
    private static final int COL_MAIN_NR = 18;

    private static final int COL_COUNT = 19;

    private Map<Long, Rufnummer> rufnummerMap = null;
    private Map<Long, LeistungParameter> parameterMap = null;
    private Map<Long, Lb2Leistung> defaultLeistungen = null;

    /**
     * @see javax.swing.table.DefaultTableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_DEFAULT_LEISTUNG:
                return "Default";
            case COL_ONKZ:
                return "ONKZ";
            case COL_DN_BASE:
                return "DN-Base";
            case COL_RANGE_FROM:
                return "Range from";
            case COL_RANGE_TO:
                return "Range to";
            case COL_DIRECT_DIAL:
                return "Direct Dial";
            case COL_MAIN_NR:
                return "Haupt-Nr";
            case COL_LEISTUNG:
                return "Leistung";
            case COL_AM_REAL:
                return "AM Realisierung";
            case COL_AM_USER_REAL:
                return "Bearbeiter Real";
            case COL_AM_KUEND:
                return "AM Kündigung";
            case COL_AM_USER_KUEND:
                return "Bearbeiter Kuend";
            case COL_EWSD_REAL:
                return "CPS Realisierung";
            case COL_EWSD_USER_REAL:
                return "Bearbeiter Real";
            case COL_EWSD_KUEND:
                return "CPS Kündigung";
            case COL_EWSD_USER_KUEND:
                return "Bearbeiter Kuend";
            case COL_PARAM:
                return "Parameter";
            case COL_PARAM_ID:
                return "Parameter_id";
            case COL_PARAM_BESCH:
                return "Parameter Beschreibung";
            default:
                return null;
        }
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        DNLeistungsView view = getDataAtRow(row);

        Rufnummer rn = getRufnummer(view.getDnNo());
        LeistungParameter lp = getParameter(view.getParameterId());
        switch (column) {
            case COL_DEFAULT_LEISTUNG:
                return ((defaultLeistungen != null) && defaultLeistungen.containsKey(view.getLeistungsId()));
            case COL_ONKZ:
                return (rn != null) ? rn.getOnKz() : null;
            case COL_DN_BASE:
                return (rn != null) ? rn.getDnBase() : null;
            case COL_RANGE_FROM:
                return (rn != null) ? rn.getRangeFrom() : null;
            case COL_RANGE_TO:
                return (rn != null) ? rn.getRangeTo() : null;
            case COL_DIRECT_DIAL:
                return (rn != null) ? rn.getDirectDial() : null;
            case COL_MAIN_NR:
                return (rn != null) ? rn.isMainNumber() : null;
            case COL_LEISTUNG:
                return view.getLeistung();
            case COL_AM_REAL:
                return view.getAmRealisierung();
            case COL_AM_USER_REAL:
                return view.getAmUserRealisierung();
            case COL_AM_KUEND:
                return view.getAmKuendigung();
            case COL_AM_USER_KUEND:
                return view.getAmUserKuendigung();
            case COL_EWSD_REAL:
                return view.getEwsdRealisierung();
            case COL_EWSD_USER_REAL:
                return view.getEwsdUserRealisierung();
            case COL_EWSD_KUEND:
                return view.getEwsdKuendigung();
            case COL_EWSD_USER_KUEND:
                return view.getEwsdUserKuendigung();
            case COL_PARAM:
                return view.getParameter();
            case COL_PARAM_BESCH:
                return (lp != null) ? lp.getLeistungParameterBeschreibung() : null;
            case COL_PARAM_ID:
                return (lp != null) ? lp.getId() : null;
            default:
                return null;
        }
    }

    /**
     * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_DEFAULT_LEISTUNG:
                return Boolean.class;
            case COL_MAIN_NR:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    /* Gibt die Rufnummer mit der DN-NO <code>dnNo</code> zurueck. */
    private Rufnummer getRufnummer(Long dnNo) {
        if (rufnummerMap != null) {
            return rufnummerMap.get(dnNo);
        }
        return null;
    }

    /**
     * Uebergibt dem TableModel eine Map mit den Rufnummern, zu denen die Leistungen gehoeren. <br> Als Key wird die ID
     * der Rufnummer erwartet, als Value das Rufnummer-Objekt selbst.
     *
     * @param rufnummerMap The rufnummerMap to set.
     */
    public void setRufnummerMap(Map<Long, Rufnummer> rufnummerMap) {
        this.rufnummerMap = rufnummerMap;
    }

    /**
     * Uebergibt dem TableModel eine Map mit den Default-Leistungen fuer das aktuelle Produkt. <br> Als Key wird die ID
     * der Rufnummernleistung verwendet.
     *
     * @param defaultLeistungen
     *
     */
    public void setDefaultLeistungsMap(Map<Long, Lb2Leistung> defaultLeistungen) {
        this.defaultLeistungen = defaultLeistungen;
    }

    /* Gibt Parameter mit der Parameter-Id  <code>pId</code> zurueck. */
    private LeistungParameter getParameter(Long pId) {
        if (parameterMap != null) {
            return parameterMap.get(pId);
        }
        return null;
    }

    /**
     * Uebergibt dem TableModel eine Map mit den Parametern, die zu den Leistungen gehoeren. <br> Als Key wird die ID
     * des Parameters erwartet, als Value das Parameter-Objekt selbst.
     *
     * @param parameterMap The parameterMap to set.
     */
    public void setParameterMap(Map<Long, LeistungParameter> parameterMap) {
        this.parameterMap = parameterMap;
    }

}



/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 08:56:38
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>de.augustakom.hurrican.model.shared.view.AuftragDNView</code>
 *
 *
 */
public class AuftragDNTableModel extends AKTableModel<AuftragDNView> {

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_KUNDE__NO = 1;
    private static final int COL_ONKZ = 2;
    private static final int COL_DN_BASE = 3;
    private static final int COL_DN_RANGE_FROM = 4;
    private static final int COL_DN_RANGE_TO = 5;
    private static final int COL_REAL_DATE = 6;
    private static final int COL_HIST_STATE = 7;
    private static final int COL_VBZ = 8;
    private static final int COL_STATUS_TEXT = 9;
    private static final int COL_HAUPTKUNDE_NO = 10;
    private static final int COL_NAME = 11;
    private static final int COL_VORNAME = 12;
    private static final int COL_KUNDENTYP = 13;
    private static final int COL_VIP = 14;
    private static final int COL_FERNKATASTROPHE = 15;
    private static final int COL_STRASSE = 16;
    private static final int COL_NUMMER = 17;
    private static final int COL_PLZ = 18;
    private static final int COL_ORT = 19;
    private static final int COL_PRODAK_ORDER__NO = 20;
    private static final int COL_LAST_CARRIER = 21;
    private static final int COL_ACT_CARRIER = 22;
    private static final int COL_FUTURE_CARRIER = 23;

    private static final int COL_COUNT = 24;

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
            case COL_AUFTRAG_ID:
                return "Auftrag-ID (CC)";
            case COL_KUNDE__NO:
                return "Kunde__No";
            case COL_ONKZ:
                return "ONKZ";
            case COL_DN_BASE:
                return "DN_Base";
            case COL_DN_RANGE_FROM:
                return "Range from";
            case COL_DN_RANGE_TO:
                return "Range to";
            case COL_REAL_DATE:
                return "Real-Date";
            case COL_HIST_STATE:
                return "Hist-Status (DN)";
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_STATUS_TEXT:
                return "Auftrags-Status";
            case COL_HAUPTKUNDE_NO:
                return "Hauptkunden-No";
            case COL_NAME:
                return "Name";
            case COL_VORNAME:
                return "Vorname";
            case COL_KUNDENTYP:
                return "Kundentyp";
            case COL_VIP:
                return "VIP";
            case COL_FERNKATASTROPHE:
                return "Fernkatastrophe";
            case COL_STRASSE:
                return "Strasse";
            case COL_NUMMER:
                return "Nummer";
            case COL_PLZ:
                return "PLZ";
            case COL_ORT:
                return "Ort";
            case COL_PRODAK_ORDER__NO:
                return "Order__NO";
            case COL_LAST_CARRIER:
                return "Last Carrier";
            case COL_ACT_CARRIER:
                return "Act Carrier";
            case COL_FUTURE_CARRIER:
                return "Future Carrier";
            default:
                return " ";
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof AuftragDNView) {
            AuftragDNView view = (AuftragDNView) o;
            switch (column) {
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_KUNDE__NO:
                    return view.getKundeNo();
                case COL_ONKZ:
                    return view.getOnKz();
                case COL_DN_BASE:
                    return view.getDnBase();
                case COL_DN_RANGE_FROM:
                    return view.getRangeFrom();
                case COL_DN_RANGE_TO:
                    return view.getRangeTo();
                case COL_REAL_DATE:
                    return view.getRealDate();
                case COL_HIST_STATE:
                    return view.getHistStatus();
                case COL_VBZ:
                    return view.getVbz();
                case COL_STATUS_TEXT:
                    return view.getAuftragStatusText();
                case COL_HAUPTKUNDE_NO:
                    return view.getHauptKundenNo();
                case COL_NAME:
                    return view.getName();
                case COL_VORNAME:
                    return view.getVorname();
                case COL_KUNDENTYP:
                    return view.getKundenTyp();
                case COL_VIP:
                    return view.getVip();
                case COL_FERNKATASTROPHE:
                    return view.getFernkatastrophe();
                case COL_STRASSE:
                    return view.getStrasse();
                case COL_NUMMER:
                    return view.getNummer();
                case COL_PLZ:
                    return view.getPlz();
                case COL_ORT:
                    return view.getOrt();
                case COL_PRODAK_ORDER__NO:
                    return view.getAuftragNoOrig();
                case COL_LAST_CARRIER:
                    return view.getLastCarrier();
                case COL_ACT_CARRIER:
                    return view.getActCarrier();
                case COL_FUTURE_CARRIER:
                    return view.getFutureCarrier();
                default:
                    break;
            }
        }
        return "";
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
            case COL_AUFTRAG_ID:
                return Long.class;
            case COL_KUNDE__NO:
                return Long.class;
            case COL_HAUPTKUNDE_NO:
                return Long.class;
            case COL_PRODAK_ORDER__NO:
                return Long.class;
            case COL_FERNKATASTROPHE:
                return Boolean.class;
            case COL_REAL_DATE:
                return Date.class;
            default:
                return super.getColumnClass(columnIndex);
        }
    }

}



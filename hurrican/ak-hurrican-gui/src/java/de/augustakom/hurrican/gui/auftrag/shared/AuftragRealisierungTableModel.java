/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.06.2005 13:35:29
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragRealisierungView;


/**
 * TableModel zur Darstellung von Objekten des Typs <code>AuftragInbetriebnahmeView</code>.
 *
 *
 */
public class AuftragRealisierungTableModel extends AKTableModel<AuftragRealisierungView> {

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_VBZ = 1;
    private static final int COL_KUNDE__NO = 2;
    private static final int COL_VORGABE_AM = 3;
    private static final int COL_REAL_DATE = 4;
    private static final int COL_INBETRIEBNAHME = 5;
    private static final int COL_KUENDIGUNG = 6;
    private static final int COL_ES_A = 7;
    private static final int COL_ES_A_ORT = 8;
    private static final int COL_ES_B = 9;
    private static final int COL_ES_B_ORT = 10;
    private static final int COL_PRODUKT = 11;
    private static final int COL_KUNDE = 12;
    private static final int COL_STATUS = 13;
    private static final int COL_BEARBEITER = 14;

    private static final int COL_COUNT = 15;

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
            case COL_AUFTRAG_ID:
                return "Auftrag-ID";
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_KUNDE__NO:
                return "Kunde__No";
            case COL_VORGABE_AM:
                return "Vorgabe AM";
            case COL_REAL_DATE:
                return "Realisierungstermin";
            case COL_INBETRIEBNAHME:
                return "Inbetriebnahme";
            case COL_KUENDIGUNG:
                return "Kündigung";
            case COL_ES_A:
                return "Endstelle A";
            case COL_ES_A_ORT:
                return "ES A Ort";
            case COL_ES_B:
                return "Endstelle B";
            case COL_ES_B_ORT:
                return "ES B Ort";
            case COL_PRODUKT:
                return "Produkt";
            case COL_KUNDE:
                return "Name";
            case COL_STATUS:
                return "Status";
            case COL_BEARBEITER:
                return "Bearbeiter";
            default:
                return null;
        }
    }

    /**
     * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof AuftragRealisierungView) {
            AuftragRealisierungView view = (AuftragRealisierungView) tmp;
            switch (column) {
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_VBZ:
                    return view.getVbz();
                case COL_KUNDE__NO:
                    return view.getKundeNo();
                case COL_VORGABE_AM:
                    return view.getVorgabeSCV();
                case COL_REAL_DATE:
                    return view.getRealisierungstermin();
                case COL_INBETRIEBNAHME:
                    return view.getInbetriebnahme();
                case COL_KUENDIGUNG:
                    return view.getKuendigung();
                case COL_ES_A:
                    return view.getEsA();
                case COL_ES_A_ORT:
                    return view.getEsAOrt();
                case COL_ES_B:
                    return view.getEsB();
                case COL_ES_B_ORT:
                    return view.getEsBOrt();
                case COL_PRODUKT:
                    return view.getAnschlussart();
                case COL_KUNDE:
                    return view.getName();
                case COL_STATUS:
                    return view.getAuftragStatusText();
                case COL_BEARBEITER:
                    return view.getBearbeiter();
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_AUFTRAG_ID:
                return Long.class;
            case COL_KUNDE__NO:
                return Long.class;
            case COL_VORGABE_AM:
                return Date.class;
            case COL_REAL_DATE:
                return Date.class;
            case COL_INBETRIEBNAHME:
                return Date.class;
            case COL_KUENDIGUNG:
                return Date.class;
            default:
                return String.class;
        }
    }

    /**
     * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}



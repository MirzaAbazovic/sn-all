/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2004 15:34:56
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>AuftragEndstelleView</code>
 *
 *
 */
public class AuftragEndstelleTableModel extends AKTableModel<AuftragEndstelleView> {

    private static final int COL_VBZ = 0;
    private static final int COL_PRODAK_ORDER__NO = 1;
    private static final int COL_AUFTRAG_ID = 2;
    private static final int COL_KUNDE__NO = 3;
    private static final int COL_ENDSTELLE = 4;
    private static final int COL_ENDSTELLE_ORT = 5;
    private static final int COL_ES_TYP = 6;
    private static final int COL_INBETRIEBNAHME = 7;
    private static final int COL_KUENDIGUNG = 8;
    private static final int COL_NAME = 9;
    private static final int COL_ANSCHLUSSART = 10;
    private static final int COL_AUFTRAG_STATUS = 11;
    private static final int COL_PROFILE_NAME = 12;
    private static final int COL_LTG_ART = 13;
    private static final int COL_PROJECT_RESPONSIBLE = 14;

    private static final int COL_COUNT = 15;

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
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_PRODAK_ORDER__NO:
                return "Order__No";
            case COL_AUFTRAG_ID:
                return "Auftrag-ID (CC)";
            case COL_KUNDE__NO:
                return "Kunde__No";
            case COL_ENDSTELLE:
                return "Endstelle";
            case COL_ENDSTELLE_ORT:
                return "Ort";
            case COL_ES_TYP:
                return "ES-Typ";
            case COL_INBETRIEBNAHME:
                return "Inbetriebnahme";
            case COL_KUENDIGUNG:
                return "Kündigung";
            case COL_NAME:
                return "Name";
            case COL_ANSCHLUSSART:
                return "Anschlussart";
            case COL_AUFTRAG_STATUS:
                return "Auftragstatus";
            case COL_PROFILE_NAME:
                return "DSLAM-Profil";
            case COL_LTG_ART:
                return "Ltg-Art";
            case COL_PROJECT_RESPONSIBLE:
                return "Hauptprojektverantwortl.";
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
        if (o instanceof AuftragEndstelleView) {
            AuftragEndstelleView view = (AuftragEndstelleView) o;
            switch (column) {
                case COL_VBZ:
                    return view.getVbz();
                case COL_PRODAK_ORDER__NO:
                    return view.getAuftragNoOrig();
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_KUNDE__NO:
                    return view.getKundeNo();
                case COL_ENDSTELLE:
                    return view.getEndstelle();
                case COL_ENDSTELLE_ORT:
                    return view.getEndstelleOrt();
                case COL_ES_TYP:
                    return view.getEndstelleTyp();
                case COL_INBETRIEBNAHME:
                    return view.getInbetriebnahme();
                case COL_KUENDIGUNG:
                    return view.getKuendigung();
                case COL_NAME:
                    return view.getName();
                case COL_ANSCHLUSSART:
                    return view.getAnschlussart();
                case COL_AUFTRAG_STATUS:
                    return view.getAuftragStatusText();
                case COL_PROFILE_NAME:
                    return view.getDslamProfile();
                case COL_LTG_ART:
                    return view.getLtgArt();
                case COL_PROJECT_RESPONSIBLE:
                    return view.getProjectResponsibleUserName();
                default:
                    break;
            }
        }
        return null;
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
            case COL_PRODAK_ORDER__NO:
                return Long.class;
            case COL_INBETRIEBNAHME:
                return Date.class;
            case COL_KUENDIGUNG:
                return Date.class;
            default:
                return super.getColumnClass(columnIndex);
        }
    }

}



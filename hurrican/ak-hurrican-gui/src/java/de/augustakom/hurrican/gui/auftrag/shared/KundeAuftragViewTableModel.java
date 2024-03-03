/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2004 09:17:11
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKFilterTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;


/**
 * TableModel fuer die Darstelltung von Objekten des Typs <code>CCKundeAuftragView</code>. <br> <br> Das TableModel
 * implementiert die Schnittstelle <code>AKFilterTableModel</code>. Unterstuetzte Filter: <br> <ul>
 * <li>FILTERNAME_PRODUKTGRUPPE - Typ: ProduktGruppe <li>FILTERNAME_AUFTRAGSTATUS - Typ: AuftragStatus </ul>
 *
 *
 */
public class KundeAuftragViewTableModel extends AKTableModel<CCKundeAuftragView> implements AKFilterTableModel {

    /**
     * Name fuer das Filter-Objekt, um nach einer ProduktGruppe zu filtern.
     */
    public static final String FILTERNAME_PRODUKTGRUPPE = "Produktgruppe";
    /**
     * Name fuer das Filter-Objekt, um nach einem Auftrag-Status zu filtern.
     */
    public static final String FILTERNAME_AUFTRAGSTATUS = "Auftragstatus";

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_VBZ = 1;
    private static final int COL_PRODAK_ORDER_NO = 2;
    private static final int COL_PROD_NAME = 3;
    private static final int COL_INBETRIEBNAHME = 4;
    private static final int COL_KUENDIGUNG = 5;
    private static final int COL_VPN_ID = 6;
    private static final int COL_ENDSTELLE_B = 7;
    private static final int COL_ENDSTELLE_A = 8;
    private static final int COL_AUFTRAG_STATUS = 9;
    private static final int COL_BUENDEL_NR = 10;
    private static final int COL_BEMERKUNG = 11;
    private static final int COL_BRAUCHT_BUENDEL = 12;

    private static final int COL_COUNT = 13;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_AUFTRAG_ID:
                return "Tech. Auftragsnr.";
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_PRODAK_ORDER_NO:
                return "Taifun A-Nr";
            case COL_PROD_NAME:
                return "Produkt";
            case COL_INBETRIEBNAHME:
                return "Inbetriebnahme";
            case COL_KUENDIGUNG:
                return "Kündigung";
            case COL_VPN_ID:
                return "VPN ID";
            case COL_ENDSTELLE_B:
                return "Endstelle B";
            case COL_ENDSTELLE_A:
                return "Endstelle A";
            case COL_AUFTRAG_STATUS:
                return "Auftragstatus";
            case COL_BUENDEL_NR:
                return "Bündel-Nr";
            case COL_BEMERKUNG:
                return "Bemerkung";
            case COL_BRAUCHT_BUENDEL:
                return "braucht Bündel";
            default:
                return " ";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object o = getDataAtRow(row);
        if (o instanceof CCKundeAuftragView) {
            CCKundeAuftragView view = (CCKundeAuftragView) o;
            switch (column) {
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_VBZ:
                    return view.getVbz();
                case COL_PRODAK_ORDER_NO:
                    return view.getAuftragNoOrig();
                case COL_PROD_NAME:
                    return view.getProduktName();
                case COL_INBETRIEBNAHME:
                    return view.getInbetriebnahme();
                case COL_KUENDIGUNG:
                    return view.getKuendigung();
                case COL_VPN_ID:
                    return view.getVpnNr();
                case COL_ENDSTELLE_B:
                    return view.getEndstelleB();
                case COL_ENDSTELLE_A:
                    return view.getEndstelleA();
                case COL_AUFTRAG_STATUS:
                    return view.getStatusText();
                case COL_BUENDEL_NR:
                    return view.getBuendelNr();
                case COL_BEMERKUNG:
                    return view.getAuftragBemerkung();
                case COL_BRAUCHT_BUENDEL:
                    return view.getBrauchtBuendel();
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_AUFTRAG_ID:
                return Long.class;
            case COL_PRODAK_ORDER_NO:
                return Long.class;
            case COL_INBETRIEBNAHME:
                return Date.class;
            case COL_KUENDIGUNG:
                return Date.class;
            case COL_VPN_ID:
                return Long.class;
            case COL_BUENDEL_NR:
                return Integer.class;
            case COL_BRAUCHT_BUENDEL:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

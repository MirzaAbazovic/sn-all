/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2004 14:33:54
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>AuftragCarrierView</code>
 *
 *
 */
public class AuftragCarrierTableModel extends AKTableModel<AuftragCarrierView> {

    private static final long serialVersionUID = -8773996595094122404L;

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_KUNDE__NO = 1;
    private static final int COL_LBZ = 2;
    private static final int COL_OTHER_LBZ = 3;
    private static final int COL_VTRNR = 4;
    private static final int COL_PRODAK_ORDER__NO = 5;
    private static final int COL_AUFTRAG_STATUS = 6;
    private static final int COL_ANSCHLUSSART = 7;
    private static final int COL_VBZ = 8;
    private static final int COL_ES_TYP = 9;
    private static final int COL_NAME = 10;
    private static final int COL_VORNAME = 11;
    private static final int COL_KUNDENTYP = 12;
    private static final int COL_VIP = 13;
    private static final int COL_FERNKATASTROHPE = 14;
    private static final int COL_HAUPTKUNDE_NO = 15;
    private static final int COL_CARRIER_REF_NO = 16;

    private static final int COL_COUNT = 17;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_AUFTRAG_ID:
                return "Auftrag-ID (CC)";
            case COL_KUNDE__NO:
                return "Kunde__No";
            case COL_LBZ:
                return "LBZ";
            case COL_OTHER_LBZ:
                return "Weitere LBZ";
            case COL_VTRNR:
                return "VtrNr";
            case COL_PRODAK_ORDER__NO:
                return "Order__NO";
            case COL_AUFTRAG_STATUS:
                return "Auftrag-Status";
            case COL_ANSCHLUSSART:
                return "Anschlussart";
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_ES_TYP:
                return "ES-Typ";
            case COL_NAME:
                return "Name";
            case COL_VORNAME:
                return "Vorname";
            case COL_KUNDENTYP:
                return "Kundentyp";
            case COL_VIP:
                return "VIP";
            case COL_FERNKATASTROHPE:
                return "Fernkatastrophe";
            case COL_HAUPTKUNDE_NO:
                return "Hauptkunden-No";
            case COL_CARRIER_REF_NO:
                return "Carrier Ref-Nr";
            default:
                return " ";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        AuftragCarrierView view = getDataAtRow(row);
        if (view != null) {
            switch (column) {
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_KUNDE__NO:
                    return view.getKundeNo();
                case COL_LBZ:
                    return view.getLbz();
                case COL_OTHER_LBZ:
                    return view.getOtherLbz();
                case COL_VTRNR:
                    return view.getVtrNr();
                case COL_PRODAK_ORDER__NO:
                    return view.getAuftragNoOrig();
                case COL_AUFTRAG_STATUS:
                    return view.getAuftragStatusText();
                case COL_ANSCHLUSSART:
                    return view.getAnschlussart();
                case COL_VBZ:
                    return view.getVbz();
                case COL_ES_TYP:
                    return view.getEsTyp();
                case COL_NAME:
                    return view.getName();
                case COL_VORNAME:
                    return view.getVorname();
                case COL_KUNDENTYP:
                    return view.getKundenTyp();
                case COL_VIP:
                    return view.getVip();
                case COL_FERNKATASTROHPE:
                    return view.getFernkatastrophe();
                case COL_HAUPTKUNDE_NO:
                    return view.getHauptKundenNo();
                case COL_CARRIER_REF_NO:
                    return view.getCarrierRefNr();
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
            case COL_KUNDE__NO:
                return Long.class;
            case COL_HAUPTKUNDE_NO:
                return Long.class;
            case COL_FERNKATASTROHPE:
                return Boolean.class;
            default:
                return String.class;
        }
    }
}

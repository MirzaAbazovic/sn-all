/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 14:33:24
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>AuftragEndstelleView</code>. <br> Die Klasse entspricht
 * eigentlich <code>AuftragEndstelleTableModel</code> allerdings ist die Spaltenaufteilung anders. Es fehlt die Spalte
 * Leitungsart
 *
 *
 */
public class AuftragEndstelleExtTableModel extends AKTableModel<AuftragEndstelleView> {

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_VBZ = 1;
    private static final int COL_INBETRIEBNAHME = 2;
    private static final int COL_KUENDIGUNG = 3;
    private static final int COL_AUFTRAG_STATUS = 4;
    private static final int COL_ANSCHLUSSART = 5;
    private static final int COL_ENDSTELLE = 6;
    private static final int COL_ENDSTELLE_NAME = 7;
    private static final int COL_ENDSTELLE_ORT = 8;
    private static final int COL_KUNDE_NAME = 9;
    private static final int COL_PRODAK_ORDER__NO = 10;
    private static final int COL_ES_TYP = 11;

    private static final int COL_COUNT = 12;

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
                return "Tech. Auftragsnr.";
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_INBETRIEBNAHME:
                return "Inbetriebnahme";
            case COL_KUENDIGUNG:
                return "Kündigung";
            case COL_AUFTRAG_STATUS:
                return "Status";
            case COL_ANSCHLUSSART:
                return "Produkt";
            case COL_ENDSTELLE:
                return "Endstelle";
            case COL_ENDSTELLE_NAME:
                return "ES-Name";
            case COL_ENDSTELLE_ORT:
                return "ES-Ort";
            case COL_KUNDE_NAME:
                return "Name";
            case COL_PRODAK_ORDER__NO:
                return "Order__No";
            case COL_ES_TYP:
                return "ES-Typ";
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
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_VBZ:
                    return view.getVbz();
                case COL_INBETRIEBNAHME:
                    return view.getInbetriebnahme();
                case COL_KUENDIGUNG:
                    return view.getKuendigung();
                case COL_AUFTRAG_STATUS:
                    return view.getAuftragStatusText();
                case COL_ANSCHLUSSART:
                    return view.getAnschlussart();
                case COL_ENDSTELLE:
                    return view.getEndstelle();
                case COL_ENDSTELLE_NAME:
                    return view.getEndstelleName();
                case COL_ENDSTELLE_ORT:
                    return view.getEndstelleOrt();
                case COL_KUNDE_NAME:
                    return view.getName();
                case COL_PRODAK_ORDER__NO:
                    return view.getAuftragNoOrig();
                case COL_ES_TYP:
                    return view.getEndstelleTyp();
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



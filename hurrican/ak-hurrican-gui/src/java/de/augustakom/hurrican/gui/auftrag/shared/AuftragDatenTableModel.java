/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2004 10:05:12
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>AuftragDatenView</code>.
 *
 *
 */
public class AuftragDatenTableModel extends AKTableModel<AuftragDatenView> {

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_KUNDE__NO = 1;
    private static final int COL_AUFTRAG_NO_ORIG = 2;
    private static final int COL_AUFTRAG_STATUS = 3;
    private static final int COL_PROD_NAME = 4;
    private static final int COL_VBZ = 5;
    private static final int COL_NAME = 6;
    private static final int COL_VORNAME = 7;
    private static final int COL_KUNDENTYP = 8;
    private static final int COL_VIP = 9;
    private static final int COL_FERNKATASTROPHE = 10;
    private static final int COL_HAUPT_KUNDE_NO = 11;
    private static final int COL_BESTELL_NR = 12;
    private static final int COL_LBZ_KUNDE = 13;
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
            case COL_AUFTRAG_ID:
                return "Auftrag-Nr";
            case COL_KUNDE__NO:
                return "Kunde__No";
            case COL_AUFTRAG_NO_ORIG:
                return "Order__No";
            case COL_AUFTRAG_STATUS:
                return "Auftrag-Status";
            case COL_PROD_NAME:
                return "Produkt";
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
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
            case COL_HAUPT_KUNDE_NO:
                return "Haupt-Kunde No";
            case COL_BESTELL_NR:
                return "Bestell-Nr";
            case COL_LBZ_KUNDE:
                return "LBZ Kunde";
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
        AuftragDatenView view = getDataAtRow(row);
        if (view != null) {
            switch (column) {
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_KUNDE__NO:
                    return view.getKundeNo();
                case COL_AUFTRAG_NO_ORIG:
                    return view.getAuftragNoOrig();
                case COL_AUFTRAG_STATUS:
                    return view.getAuftragStatusText();
                case COL_PROD_NAME:
                    return view.getProduktName();
                case COL_VBZ:
                    return view.getVbz();
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
                case COL_HAUPT_KUNDE_NO:
                    return view.getHauptKundenNo();
                case COL_BESTELL_NR:
                    return view.getBestellNr();
                case COL_LBZ_KUNDE:
                    return view.getLbzKunde();
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
                return Integer.class;
            case COL_KUNDE__NO:
                return Long.class;
            case COL_AUFTRAG_NO_ORIG:
                return Long.class;
            case COL_HAUPT_KUNDE_NO:
                return Long.class;
            case COL_FERNKATASTROPHE:
                return Boolean.class;
            default:
                return String.class;
        }
    }

}



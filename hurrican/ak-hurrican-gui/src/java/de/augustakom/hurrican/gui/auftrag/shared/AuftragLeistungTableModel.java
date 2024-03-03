/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.08.2004 13:28:37
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>BAuftragLeistungView</code>.
 *
 *
 */
public class AuftragLeistungTableModel extends AKTableModel<BAuftragLeistungView> {

    private static final int COL_KUNDE__NO = 0;
    private static final int COL_AUFTRAG_TYP = 1;
    private static final int COL_AUFTRAG__NO = 2;
    private static final int COL_GUELTIG_VON = 3;
    private static final int COL_GUELTIG_BIS = 4;
    private static final int COL_MENGE = 5;
    private static final int COL_BUENDEL_NO = 6;
    private static final int COL_PRODUKT = 7;
    private static final int COL_LEISTUNG = 8;
    private static final int COL_EXT_PRODUKT__NO = 9;
    private static final int COL_EXT_LEISTUNG__NO = 10;
    private static final int COL_EXT_MISC__NO = 11;
    private static final int COL_HIST_STATUS = 12;

    private static final int COL_COUNT = 13;

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
            case COL_KUNDE__NO:
                return "Kunde__No";
            case COL_AUFTRAG_TYP:
                return "A-Typ";
            case COL_AUFTRAG__NO:
                return "Order__NO";
            case COL_GUELTIG_VON:
                return "Gültig von";
            case COL_GUELTIG_BIS:
                return "Gültig bis";
            case COL_MENGE:
                return "Menge";
            case COL_BUENDEL_NO:
                return "Bündel-No";
            case COL_PRODUKT:
                return "Produkt";
            case COL_LEISTUNG:
                return "Leistung";
            case COL_EXT_PRODUKT__NO:
                return "EXT_PRODUKT__NO";
            case COL_EXT_LEISTUNG__NO:
                return "EXT_LEISTUNG__NO";
            case COL_EXT_MISC__NO:
                return "EXT_MISC__NO";
            case COL_HIST_STATUS:
                return "Hist-Status";
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
        if (o instanceof BAuftragLeistungView) {
            BAuftragLeistungView view = (BAuftragLeistungView) o;
            switch (column) {
                case COL_KUNDE__NO:
                    return view.getKundeNo();
                case COL_AUFTRAG_TYP:
                    return view.getAtyp();
                case COL_AUFTRAG__NO:
                    return view.getAuftragNoOrig();
                case COL_GUELTIG_VON:
                    return view.getAuftragPosGueltigVon();
                case COL_GUELTIG_BIS:
                    return view.getAuftragPosGueltigBis();
                case COL_MENGE:
                    return view.getMenge();
                case COL_BUENDEL_NO:
                    return view.getBundleOrderNo();
                case COL_PRODUKT:
                    return view.getOeName();
                case COL_LEISTUNG:
                    return view.getLeistungName();
                case COL_EXT_PRODUKT__NO:
                    return view.getExternProduktNo();
                case COL_EXT_LEISTUNG__NO:
                    return view.getExternLeistungNo();
                case COL_EXT_MISC__NO:
                    return view.getExternMiscNo();
                case COL_HIST_STATUS:
                    return view.getAuftragHistStatus();
                default:
                    return null;
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
            case COL_KUNDE__NO:
                return Long.class;
            case COL_AUFTRAG__NO:
                return Long.class;
            case COL_GUELTIG_VON:
                return Date.class;
            case COL_GUELTIG_BIS:
                return Date.class;
            case COL_MENGE:
                return Long.class;
            case COL_BUENDEL_NO:
                return Integer.class;
            case COL_EXT_PRODUKT__NO:
                return Long.class;
            case COL_EXT_LEISTUNG__NO:
                return Long.class;
            case COL_EXT_MISC__NO:
                return Long.class;
            default:
                return String.class;
        }
    }

}



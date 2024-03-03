/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2004 11:59:58
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>BAuftragPosLeistungView</code>
 *
 *
 */
public class AuftragPosLeistungTableModel extends AKTableModel<BAuftragLeistungView> {

    private static final int COL_GUELTIG_VON = 0;
    private static final int COL_GUELTIG_BIS = 1;
    private static final int COL_MENGE = 2;
    private static final int COL_LEISTUNGSNAME = 3;
    private static final int COL_PRODUKT = 4;

    private static final int COL_COUNT = 5;

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
            case COL_MENGE:
                return "Menge";
            case COL_LEISTUNGSNAME:
                return "Leistung";
            case COL_PRODUKT:
                return "Produkt";
            default:
                return "";
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
                case COL_GUELTIG_VON:
                    return view.getAuftragPosGueltigVon();
                case COL_GUELTIG_BIS:
                    return view.getAuftragPosGueltigBis();
                case COL_MENGE:
                    return view.getMenge();
                case COL_LEISTUNGSNAME:
                    return view.getLeistungName();
                case COL_PRODUKT:
                    return view.getOeName();
                default:
                    return "";
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
            case COL_GUELTIG_VON:
                return Date.class;
            case COL_GUELTIG_BIS:
                return Date.class;
            case COL_MENGE:
                return Long.class;
            default:
                return String.class;
        }
    }

}



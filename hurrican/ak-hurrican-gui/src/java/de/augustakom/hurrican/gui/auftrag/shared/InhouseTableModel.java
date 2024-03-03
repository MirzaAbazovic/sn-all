/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2004 10:43:52
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.Inhouse;


/**
 * TableModel fuer die Darstellung von Inhouse-Objekten.
 *
 *
 */
public class InhouseTableModel extends AKTableModel<Inhouse> {

    private static final int COL_RAUM = 0;
    private static final int COL_VERKABELUNG = 1;
    private static final int COL_ANSPRECHPARTNER = 2;
    private static final int COL_GUELTIG_VON = 3;
    private static final int COL_GUELTIG_BIS = 4;

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
            case COL_RAUM:
                return "Raumnummer";
            case COL_VERKABELUNG:
                return "Verkabelung";
            case COL_ANSPRECHPARTNER:
                return "Ansprechpartner";
            case COL_GUELTIG_VON:
                return "Gültig von";
            case COL_GUELTIG_BIS:
                return "Gültig bis";
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
        if (o instanceof Inhouse) {
            Inhouse in = (Inhouse) o;
            switch (column) {
                case COL_RAUM:
                    return in.getRaumnummer();
                case COL_VERKABELUNG:
                    return in.getVerkabelung();
                case COL_ANSPRECHPARTNER:
                    return in.getAnsprechpartner();
                case COL_GUELTIG_VON:
                    return in.getGueltigVon();
                case COL_GUELTIG_BIS:
                    return in.getGueltigBis();
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
        if (columnIndex == COL_GUELTIG_VON) {
            return Date.class;
        }
        else if (columnIndex == COL_GUELTIG_BIS) {
            return Date.class;
        }
        else {
            return String.class;
        }
    }

}



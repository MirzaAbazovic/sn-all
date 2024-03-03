/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2004 15:47:20
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.CCAuftragProduktVbzView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>CCAuftragProduktVbzView</code>.
 *
 *
 */
public class AuftragProduktVbzTableModel extends AKTableModel<CCAuftragProduktVbzView> {

    private static final int COL_AUFTRAG_ID = 0;
    private static final int COL_ORDER__NO = 1;
    private static final int COL_VBZ = 2;
    private static final int COL_ANSCHLUSSART = 3;
    private static final int COL_STATUS = 4;

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
            case COL_AUFTRAG_ID:
                return "Auftrag-Id (CC)";
            case COL_ORDER__NO:
                return "Order__No";
            case COL_VBZ:
                return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
            case COL_ANSCHLUSSART:
                return "Anschlussart";
            case COL_STATUS:
                return "Status";
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
        if (o instanceof CCAuftragProduktVbzView) {
            CCAuftragProduktVbzView view = (CCAuftragProduktVbzView) o;
            switch (column) {
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_ORDER__NO:
                    return view.getAuftragNoOrig();
                case COL_VBZ:
                    return view.getVbz();
                case COL_ANSCHLUSSART:
                    return view.getAnschlussart();
                case COL_STATUS:
                    return view.getAuftragStatus();
                default:
                    break;
            }
        }
        return super.getValueAt(row, column);
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
            case COL_ORDER__NO:
                return Long.class;
            default:
                return String.class;
        }
    }
}



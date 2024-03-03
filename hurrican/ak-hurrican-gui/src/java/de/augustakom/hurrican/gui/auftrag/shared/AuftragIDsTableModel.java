/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 15:12:16
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.view.CCAuftragIDsView;


/**
 * TableModel fuer die Darstellung von Objekten des Typs <code>CCAuftragIdsView</code>.
 *
 *
 */
public class AuftragIDsTableModel extends AKTableModel<CCAuftragIDsView> {

    private static final int COL_VBZ = 0;
    private static final int COL_AUFTRAG_ID = 1;
    private static final int COL_PRODAK_ORDER__NO = 2;
    private static final int COL_PRODUKTNAME = 3;

    private static final int COL_COUNT = 4;

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
            case COL_AUFTRAG_ID:
                return "Tech. Auftragsnr.";
            case COL_PRODAK_ORDER__NO:
                return "Order__NO";
            case COL_PRODUKTNAME:
                return "Produktname";
            default:
                return "";
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        CCAuftragIDsView view = getDataAtRow(row);
        switch (column) {
            case COL_VBZ:
                return view.getVbz();
            case COL_AUFTRAG_ID:
                return view.getAuftragId();
            case COL_PRODAK_ORDER__NO:
                return view.getAuftragNoOrig();
            case COL_PRODUKTNAME:
                return view.getProduktName();
            default:
                return null;
        }
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
            case COL_VBZ:
                return String.class;
            case COL_PRODAK_ORDER__NO:
                return Long.class;
            case COL_PRODUKTNAME:
                return String.class;
            default:
                return Integer.class;
        }
    }

}



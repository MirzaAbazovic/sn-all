/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 11:51:34
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.shared.view.AuftragINRufnummerView;


/**
 * TableModel fuer Objekte des Typs <code>AuftragINRufnummerView</code>
 *
 *
 */
public class AuftragINRufnummerTableModel extends AKTableModel<AuftragINRufnummerView> {

    private static final int COL_ORDER__NO = 0;
    private static final int COL_AUFTRAG_ID = 1;
    private static final int COL_KUNDE__NO = 2;
    private static final int COL_NAME = 3;
    private static final int COL_VORNAME = 4;
    private static final int COL_ORT = 5;
    private static final int COL_VORWAHL = 6;
    private static final int COL_RUFNUMMER = 7;
    private static final int COL_STATUS = 8;

    private static final int COL_COUNT = 9;

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
            case COL_ORDER__NO:
                return "Order__No";
            case COL_AUFTRAG_ID:
                return "Auftrag-ID (CC)";
            case COL_KUNDE__NO:
                return "Kunde__No";
            case COL_NAME:
                return "Name";
            case COL_VORNAME:
                return "Vorname";
            case COL_ORT:
                return "Ort";
            case COL_VORWAHL:
                return "0800-Vorwahl";
            case COL_RUFNUMMER:
                return "0800-Rufnummer";
            case COL_STATUS:
                return "Status-Text";
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
        if (o instanceof AuftragINRufnummerView) {
            AuftragINRufnummerView view = (AuftragINRufnummerView) o;
            switch (column) {
                case COL_ORDER__NO:
                    return view.getAuftragNoOrig();
                case COL_AUFTRAG_ID:
                    return view.getAuftragId();
                case COL_KUNDE__NO:
                    return view.getKundeNo();
                case COL_NAME:
                    return view.getName();
                case COL_VORNAME:
                    return view.getVorname();
                case COL_ORT:
                    return view.getOrt();
                case COL_VORWAHL:
                    return view.getPrefix();
                case COL_RUFNUMMER:
                    return view.getBusinessNr();
                case COL_STATUS:
                    return view.getAuftragStatusText();
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
            case COL_ORDER__NO:
                return Long.class;
            case COL_AUFTRAG_ID:
                return Long.class;
            case COL_KUNDE__NO:
                return Long.class;
            default:
                return super.getColumnClass(columnIndex);
        }
    }

}



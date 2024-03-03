/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.mnet.wbci.model.RufnummernportierungVO;

public class RufnummernPortierungTableModel extends AKTableModel<RufnummernportierungVO> {

    private static final long serialVersionUID = 140647779818602956L;

    static final int COL_STATUS = 0;
    static final int COL_ONKZ = 1;
    static final int COL_DN_BASE = 2;
    static final int COL_DIRECT_DIAL = 3;
    static final int COL_FROM = 4;
    static final int COL_TO = 5;
    static final int COL_PKI_ABG = 6;
    static final int COL_PKI_AUF = 7;

    static final int COL_COUNT = 8;

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_STATUS:
                return "Info";
            case COL_ONKZ:
                return "ONKZ";
            case COL_DN_BASE:
                return "Durchwahl";
            case COL_DIRECT_DIAL:
                return "Abfragestelle";
            case COL_FROM:
                return "von";
            case COL_TO:
                return "bis";
            case COL_PKI_ABG:
                return "PKIabg";
            case COL_PKI_AUF:
                return "PKIauf";
            default:
                return " ";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        RufnummernportierungVO vo = getDataAtRow(row);
        if (vo != null) {
            switch (column) {
                case COL_STATUS:
                    return (vo.getStatusInfo() != null) ? vo.getStatusInfo().infoText : null;
                case COL_ONKZ:
                    return vo.getOnkz();
                case COL_DN_BASE:
                    return vo.getDnBase();
                case COL_DIRECT_DIAL:
                    return vo.getDirectDial();
                case COL_FROM:
                    return vo.getBlockFrom();
                case COL_TO:
                    return vo.getBlockTo();
                case COL_PKI_ABG:
                    return vo.getPkiAbg();
                case COL_PKI_AUF:
                    return vo.getPkiAuf();
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.billing.view.BAuftragVO;
import de.mnet.wbci.model.OrderMatchVO;

/**
 * Table model for suggested order assignment
 *
 *
 */
public class BillingOrderTableModel extends AKTableModel<Pair<BAuftragVO, OrderMatchVO>> {

    private static final long serialVersionUID = 4935323069742974918L;

    public static final int COL_ORDER_NO = 0;
    public static final int COL_CUST_NO = 1;
    public static final int COL_NAME = 2;
    public static final int COL_DN = 3;
    public static final int COL_LOCATION = 4;
    public static final int COL_CANCEL_DATE = 5;
    private static final int COL_COUNT = 6;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_ORDER_NO:
                return "Taifun Auftrags-Id";
            case COL_CUST_NO:
                return "Kunde No";
            case COL_NAME:
                return "Name";
            case COL_DN:
                return "Haupt-RN";
            case COL_LOCATION:
                return "Standort";
            case COL_CANCEL_DATE:
                return "Kündigungsdatum";
            default:
                return "";
        }
    }

    public int[] getColumnDimensions() {
        return new int[] { 100, 75, 130, 120, 180, 100 };
    }

    @Override
    public Object getValueAt(int row, int column) {
        Pair<BAuftragVO, OrderMatchVO> pair = getDataAtRow(row);
        if (pair != null) {
            BAuftragVO bAuftragVO = pair.getFirst();

            switch (column) {
                case COL_ORDER_NO:
                    return bAuftragVO.getAuftragNoOrig();
                case COL_CUST_NO:
                    return bAuftragVO.getKundeNo();
                case COL_NAME:
                    return bAuftragVO.getCustomerName();
                case COL_LOCATION:
                    return bAuftragVO.getStreet() + ", " + bAuftragVO.getCity();
                case COL_DN:
                    return bAuftragVO.getMainDn();
                case COL_CANCEL_DATE:
                    return bAuftragVO.getKuendigungsdatum();
                default:
                    break;
            }
        }
        return super.getValueAt(row, column);
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_ORDER_NO:
                return Long.class;
            case COL_CANCEL_DATE:
                return Date.class;
            default:
                return String.class;
        }
    }
}

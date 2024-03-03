/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2004 15:15:08
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;


/**
 * TableModel fuer die Darstellung von IP-Objekten.
 *
 *
 */
public class IPAddressTableModel extends AKTableModel<IPAddressPanelView> {

    private static final int COL_IP = 0;
    private static final int COL_PURPOSE = 1;
    private static final int COL_STATUS = 2;
    private static final int COL_TYPE = 3;
    private static final int COL_GUELTIG_VON = 4;
    private static final int COL_GUELTIG_BIS = 5;
    private static final int COL_COUNT = 6;

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
            case COL_IP:
                return "IP-Adresse";
            case COL_PURPOSE:
                return "Verwendungszweck";
            case COL_STATUS:
                return "Status";
            case COL_TYPE:
                return "Typ";
            case COL_GUELTIG_VON:
                return "Gültig Von";
            case COL_GUELTIG_BIS:
                return "Gültig Bis";
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
        if (o instanceof IPAddressPanelView) {
            IPAddressPanelView ipView = (IPAddressPanelView) o;
            switch (column) {
                case COL_IP:
                    return ipView.getIpAddress().getAddress();
                case COL_PURPOSE:
                    return (ipView.getIpAddress().getPurpose() != null) ?
                            ipView.getIpAddress().getPurpose().getStrValue() : null;
                case COL_STATUS:
                    return ipView.getStatus();
                case COL_TYPE:
                    return (ipView.getIpAddress().getIpType() != null) ?
                            ipView.getIpAddress().getIpType().name() : null;
                case COL_GUELTIG_VON:
                    return ipView.getIpAddress().getGueltigVon();
                case COL_GUELTIG_BIS:
                    return ipView.getIpAddress().getGueltigBis();
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
            case COL_GUELTIG_VON:
                return Date.class;
            case COL_GUELTIG_BIS:
                return Date.class;
            default:
                return String.class;
        }
    }
}

/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.10.2011 14:49:56
 */
package de.augustakom.hurrican.gui.auftrag.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.PortForwarding;

/**
 *
 */
public class PortForwardingTableModel extends AKTableModel<PortForwarding> {

    private static final int COL_AKTIV = 0;
    private static final int COL_TRANSPORT_PROTOKOLL = 1;
    private static final int COL_QUELL_IP = 2;
    private static final int COL_ZIEL_IP = 3;
    private static final int COL_QUELL_PORT = 4;
    private static final int COL_ZIEL_PORT = 5;
    private static final int COL_BEARBEITER = 6;
    private static final int COL_BEMERUNG = 7;

    private static final int COL_COUNT = 8;

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
            case COL_AKTIV:
                return "aktiv";
            case COL_TRANSPORT_PROTOKOLL:
                return "Transport-Protokoll";
            case COL_QUELL_IP:
                return "Quell-IP-Adresse";
            case COL_ZIEL_IP:
                return "Ziel-IP-Adresse";
            case COL_QUELL_PORT:
                return "Quell-Port";
            case COL_ZIEL_PORT:
                return "Ziel-Port";
            case COL_BEARBEITER:
                return "Bearbeiter";
            case COL_BEMERUNG:
                return "Bemerkung";
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
        if (o instanceof PortForwarding) {
            PortForwarding pf = (PortForwarding) o;
            switch (column) {
                case COL_AKTIV:
                    return pf.getActive();
                case COL_TRANSPORT_PROTOKOLL:
                    return pf.getTransportProtocolNullsafe();
                case COL_QUELL_IP:
                    return pf.getSourceIpAddressNullSafe();
                case COL_ZIEL_IP:
                    return pf.getDestIpAddressRef().getAddress();
                case COL_QUELL_PORT:
                    return pf.getSourcePortNullsafe();
                case COL_ZIEL_PORT:
                    return pf.getDestPort();
                case COL_BEARBEITER:
                    return pf.getBearbeiter();
                case COL_BEMERUNG:
                    return pf.getBemerkung();
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
        if (columnIndex == COL_AKTIV) {
            return Boolean.class;
        }
        else {
            return String.class;
        }
    }
}

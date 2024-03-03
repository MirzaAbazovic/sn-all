/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2011 14:58:29
 */
package de.augustakom.hurrican.gui.shared;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.auftrag.voip.AuftragVoIpDnTableView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;

/**
 * TableModel fuer die Darstellung der zugeordneten Rufnummern zum Auftrag.
 *
 *
 */
public class VoipDNAuftragViewTableModel extends AKTableModel<AuftragVoIpDnTableView> {

    public enum Column {
        VALID_FROM("Aktiv von"),
        VALID_TO("Aktiv bis"),
        ONKZ("ONKZ"),
        DN_BASE("DN-Base"),
        DIRECT_DIAL("Direct Dial"),
        BLOCK_START("Range from"),
        BLOCK_END("Range to"),
        IS_MAIN_NUMBER("Haupt-Nr"),
        IS_BLOCK("Block"),
        ALLE("Alle"),
        PORT1("Port 1"),
        PORT2("Port 2"),
        PORT3("Port 3"),
        PORT4("Port 4"),
        PORT5("Port 5"),
        PORT6("Port 6"),
        PORT7("Port 7"),
        PORT8("Port 8");

        private Column(String name) {
            this.name = name;
        }

        private final String name;

        String getName() {
            return name;
        }

        static Column forOrdinal(int ordinal) {
            return Column.values()[ordinal];
        }

        boolean isPortColumn() {
            return this.ordinal() >= Column.PORT1.ordinal();
        }
    }

    private int getPortsAvailableCount() {
        AuftragVoIpDnTableView row = getDataAtRow(0);
        return (row != null) ? row.getMaxPortCount() : 0;
    }

    @Override
    public int getColumnCount() {
        int portsAvailableCount = getPortsAvailableCount();
        return Column.values().length
                - ((portsAvailableCount > 0) ? (AuftragVoipDNView.MAX_PORTS_SUPPORTED - portsAvailableCount)
                : AuftragVoipDNView.MAX_PORTS_SUPPORTED + 1);
    }

    @Override
    public String getColumnName(int column) {
        Column selectedColumn = getSelectedColumn(column);
        String columnName = selectedColumn.getName();
        String columnNameToShow = columnName;
        return columnNameToShow;
    }

    private int getPortIndexFromColumn(int columnNr) {
        return getPortsAvailableCount() - (getColumnCount() - columnNr);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof AuftragVoIpDnTableView) {
            AuftragVoIpDnTableView model = (AuftragVoIpDnTableView) tmp;
            Column selectedColumn = getSelectedColumn(column);
            if (selectedColumn.isPortColumn()) {
                return model.isPortSelected(getPortIndexFromColumn(column));
            }
            else {
                switch (selectedColumn) {
                    case ONKZ:
                        return model.getOnKz();
                    case DN_BASE:
                        return model.getDnBase();
                    case VALID_FROM:
                        return model.getPortSelectionValidFrom();
                    case VALID_TO:
                        return model.getPortSelectionValidTo();
                    case IS_BLOCK:
                        return model.isBlock();
                    case DIRECT_DIAL:
                        return model.getDirectDial();
                    case BLOCK_START:
                        return model.getRangeFrom();
                    case BLOCK_END:
                        return model.getRangeTo();
                    case IS_MAIN_NUMBER:
                        return model.getMainNumber();
                    case ALLE:
                        return model.isAllSelected();
                    default:
                        break;
                }
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    /**
     * @param column
     * @return
     */
    private Column getSelectedColumn(int column) {
        Column selectedColumn = Column.forOrdinal(column);
        return selectedColumn;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Column selectedColumn = getSelectedColumn(columnIndex);
        if (selectedColumn.isPortColumn()) {
            return Boolean.class;
        }
        switch (selectedColumn) {
            case VALID_FROM:
                return Date.class;
            case VALID_TO:
                return Date.class;
            case IS_BLOCK:
                return Boolean.class;
            case IS_MAIN_NUMBER:
                return Boolean.class;
            case ALLE:
                return Boolean.class;
            default:
                return String.class;
        }
    }
}

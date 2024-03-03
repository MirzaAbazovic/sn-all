/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.13 14:18
 */
package de.augustakom.hurrican.gui.auftrag.voip;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;

/**
 *
 */
public class VoipDnTableModel extends AKTableModel<AuftragVoipDNView> {
    public enum Column {
        GUELTIG_VON("Gültig von"),
        GUELTIG_BIS("Gültig bis"),
        ONKZ("ONKZ"),
        DN_BASE("DN-Base"),
        DIRECT_DIAL("Direct Dial"),
        BLOCK_START("Range from"),
        BLOCK_END("Range to"),
        SIP_REGISTRAR("SIP-Registrar"),
        IS_MAIN_NUMBER("Haupt-Nr"),
        IS_BLOCK("Block");

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
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        Column selectedColumn = getSelectedColumn(column);
        String columnName = selectedColumn.getName();
        String columnNameToShow = columnName;
        return columnNameToShow;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof AuftragVoipDNView) {
            AuftragVoipDNView model = (AuftragVoipDNView) tmp;
            Column selectedColumn = getSelectedColumn(column);
            switch (selectedColumn) {
                case ONKZ:
                    return model.getOnKz();
                case DN_BASE:
                    return model.getDnBase();
                case GUELTIG_VON:
                    return model.getGueltigVon();
                case GUELTIG_BIS:
                    return model.getGueltigBis();
                case SIP_REGISTRAR:
                    return model.getSipDomain();
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
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof AuftragVoipDNView) {
            AuftragVoipDNView model = (AuftragVoipDNView) tmp;
            Column selectedColumn = getSelectedColumn(column);
            switch (selectedColumn) {
                case SIP_REGISTRAR:
                    if (aValue instanceof Reference) {
                        Reference ref = (Reference) aValue;
                        model.setSipDomain(ref);
                        fireTableDataChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof AuftragVoipDNView) {
            Column selectedColumn = getSelectedColumn(column);
            switch (selectedColumn) {
                case SIP_REGISTRAR:
                    return true;
                default: break;
            }
        }
        return false;
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
        switch (selectedColumn) {
            case GUELTIG_VON:
                return Date.class;
            case GUELTIG_BIS:
                return Date.class;
            case SIP_REGISTRAR:
                return Reference.class;
            case IS_BLOCK:
                return Boolean.class;
            case IS_MAIN_NUMBER:
                return Boolean.class;
            default:
                return String.class;
        }
    }
}

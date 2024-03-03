/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.2007 08:12:43
 */
package de.augustakom.hurrican.gui.shared;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.Montageart;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;

/**
 * TableModel fuer die Darstellung der zugeordneten Endgeraete zum Auftrag.
 */
public class EG2AuftragViewTableModel extends AKTableModel<EG2AuftragView> {

    private static final int COL_ACTIVE = 0;
    private static final int COL_EG_NAME = 1;
    private static final int COL_EG_MONTAGE = 2;
    private static final int COL_CONFIGURABLE = 3;
    private static final int COL_HAS_CONFIG = 4;
    private static final int COL_ES_TYP = 5;
    private static final int COL_ETAGE = 6;
    private static final int COL_RAUM = 7;

    private static final int COL_COUNT = 8;

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_ACTIVE:
                return "aktiv";
            case COL_EG_NAME:
                return "Endgerät";
            case COL_EG_MONTAGE:
                return "Montage";
            case COL_CONFIGURABLE:
                return "konfigurierbar";
            case COL_HAS_CONFIG:
                return "hat Konfig";
            case COL_ES_TYP:
                return "ES-Typ";
            case COL_ETAGE:
                return "Etage";
            case COL_RAUM:
                return "Raum";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof EG2AuftragView) {
            EG2AuftragView model = (EG2AuftragView) tmp;
            switch (column) {
                case COL_ACTIVE:
                    return !BooleanTools.nullToFalse(model.getDeactivated());
                case COL_EG_NAME:
                    return model.getEgName();
                case COL_EG_MONTAGE:
                    return model.getMontageart();
                case COL_CONFIGURABLE:
                    return model.getIsConfigurable();
                case COL_HAS_CONFIG:
                    return model.getHasConfiguration();
                case COL_ES_TYP:
                    return model.getEsTyp();
                case COL_ETAGE:
                    return model.getEtage();
                case COL_RAUM:
                    return model.getRaum();
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Object tmp = getDataAtRow(row);
        if (tmp instanceof EG2AuftragView) {
            EG2AuftragView model = (EG2AuftragView) tmp;
            switch (column) {
                case COL_ACTIVE:
                    if (aValue instanceof Boolean) {
                        model.setDeactivated(!(Boolean) aValue);
                    }
                    break;
                case COL_EG_MONTAGE:
                    if (aValue instanceof Montageart) {
                        Montageart mont = (Montageart) aValue;
                        model.setMontageartId(mont.getId());
                        model.setMontageart(mont.getName());
                    }
                    break;
                case COL_ETAGE:
                    if (aValue instanceof String) {
                        model.setEtage((String) aValue);
                    }
                    break;
                case COL_RAUM:
                    if (aValue instanceof String) {
                        model.setRaum((String) aValue);
                    }
                    break;
                default:
                    break;
            }
            fireTableDataChanged();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if ((column == COL_ACTIVE) || (column == COL_RAUM) || (column == COL_ETAGE) || (column == COL_EG_MONTAGE)) {
            return true;
        }
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_ACTIVE:
                return Boolean.class;
            case COL_CONFIGURABLE:
                return Boolean.class;
            case COL_HAS_CONFIG:
                return Boolean.class;
            default:
                return String.class;
        }
    }
}






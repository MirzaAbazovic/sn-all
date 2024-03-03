/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2004 13:41:39
 */
package de.augustakom.authentication.gui.role;

import java.util.*;

import de.augustakom.authentication.model.AKCompBehavior;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.common.gui.swing.table.AKTableModel;

/**
 * TableModel fuer die Darstellung der Rollenrechte auf einer best. GUI-Komponente.
 */
public class RoleRightsTableModel extends AKTableModel<AKRole> {

    static final int COL_ROLE = 0;
    static final int COL_VISIBLE = 1;
    static final int COL_EXECUTABLE = 2;

    private static final int COL_COUNT = 3;

    private Map<Long, AKCompBehavior> behaviors = null;

    /**
     * Konstruktor fuer das TableModel.
     *
     * @param roles     Verfuegbare Rollen
     * @param behaviors Komponentenverhalten der Rollen.
     */
    public RoleRightsTableModel(List<AKRole> roles, Map<Long, AKCompBehavior> behaviors) {
        super("de.augustakom.authentication.gui.role.resources.RoleRightsTableModel");
        setData(roles);
        this.behaviors = behaviors;
    }

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_ROLE:
                return getResourceReader().getValue("header.role");
            case COL_VISIBLE:
                return getResourceReader().getValue("header.visible");
            case COL_EXECUTABLE:
                return getResourceReader().getValue("header.executable");
            default:
                return " ";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        final AKRole role = getDataAtRow(row);
        if (role != null) {
            switch (column) {
                case COL_ROLE:
                    return role;
                case COL_VISIBLE:
                    return isVisible(role.getId());
                case COL_EXECUTABLE:
                    return isExecutable(role.getId());
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Ueberprueft, ob eine Komponente fuer eine best. Rolle sichtbar ist.
     */
    private Boolean isVisible(Long roleId) {
        if (behaviors != null) {
            AKCompBehavior obj = behaviors.get(roleId);
            if (obj != null) {
                return obj.isVisible();
            }
        }
        return Boolean.TRUE;
    }

    /**
     * Ueberprueft, ob eine Komponente fuer eine best. Rolle ausfuehrbar ist.
     */
    private Boolean isExecutable(Long roleId) {
        if (behaviors != null) {
            AKCompBehavior obj = behaviors.get(roleId);
            if (obj != null) {
                return obj.isExecutable();
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return (column == COL_VISIBLE) || (column == COL_EXECUTABLE);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if ((column == COL_VISIBLE) || ((column == COL_EXECUTABLE) && (behaviors != null))) {
            AKRole role = getDataAtRow(row);
            AKCompBehavior obj = behaviors.get(role.getId());

            if (obj == null) {
                obj = new AKCompBehavior();
                (obj).setRoleId(role.getId());
                behaviors.put(role.getId(), obj);
            }

            AKCompBehavior cb = obj;
            if (column == COL_VISIBLE) {
                cb.setVisible(!((Boolean) getValueAt(row, COL_VISIBLE)).booleanValue());
            }
            else {
                cb.setExecutable(!((Boolean) getValueAt(row, COL_EXECUTABLE)).booleanValue());
            }
        }
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if ((column == COL_VISIBLE) || (column == COL_EXECUTABLE)) {
            return Boolean.class;
        }
        else if (column == COL_ROLE) {
            return AKRole.class;
        }
        return super.getColumnClass(column);
    }
}

/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.05.2004 14:35:15
 */
package de.augustakom.authentication.gui.user;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKDepartmentService;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.locator.ServiceLocator;


/**
 * TableModel fuer AKUser-Objekte.
 */
public class UserTableModel extends AKTableModel<AKUser> {

    private static final Logger LOGGER = Logger.getLogger(UserTableModel.class);

    protected static final int COL_ID = 0;
    protected static final int COL_NAME = 1;
    protected static final int COL_FIRSTNAME = 2;
    protected static final int COL_DEPARTMENT = 3;
    protected static final int COL_ACTIVE = 4;

    private static final int COL_COUNT = 5;

    private Map<Long, String> departments = null;

    /**
     * Standardkonstruktor
     */
    public UserTableModel() {
        super("de.augustakom.authentication.gui.user.resources.UserTableModel");
    }

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public int getRowCount() {
        return (getData() != null) ? getData().size() : 0;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_ID:
                return getResourceReader().getValue("header.id");
            case COL_NAME:
                return getResourceReader().getValue("header.name");
            case COL_FIRSTNAME:
                return getResourceReader().getValue("header.firstname");
            case COL_DEPARTMENT:
                return getResourceReader().getValue("header.department");
            case COL_ACTIVE:
                return getResourceReader().getValue("header.active");
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        AKUser user = getDataAtRow(row);
        if (user != null) {
            switch (column) {
                case COL_ID:
                    return user.getId();
                case COL_NAME:
                    return user.getName();
                case COL_FIRSTNAME:
                    return user.getFirstName();
                case COL_DEPARTMENT:
                    return getDepartmentName(user.getDepartmentId());
                case COL_ACTIVE:
                    return (user.isActive()) ? Boolean.TRUE : Boolean.FALSE;
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * Gibt den Namen der Abteilung mit der angegebenen ID zurueck.
     */
    private Object getDepartmentName(Long depId) {
        if (departments == null) {
            loadDepartments();
        }

        return departments.get(depId);
    }

    /**
     * Laedt alle AKDepartment-Objekte. <br> Die Namen der Abteilungen werden unter der jeweiligen ID in einer HashMap
     * gespeichert.
     */
    private void loadDepartments() {
        departments = new HashMap<Long, String>();

        try {
            AKDepartmentService depService = ServiceLocator.instance().getService(AKAuthenticationServiceNames.DEPARTMENT_SERVICE, AKDepartmentService.class);
            List<AKDepartment> result = depService.findAll();
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    AKDepartment department = result.get(i);
                    departments.put(department.getId(), department.getName());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_ID:
                return Long.class;
            case COL_ACTIVE:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

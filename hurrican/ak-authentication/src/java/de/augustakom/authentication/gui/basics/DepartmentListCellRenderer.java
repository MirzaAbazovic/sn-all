/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2004 14:26:51
 */
package de.augustakom.authentication.gui.basics;

import java.awt.*;
import javax.swing.*;

import de.augustakom.authentication.model.AKDepartment;


/**
 * ListCellRenderer fuer AKDepartment-Objekte.
 *
 *
 */
public class DepartmentListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (value instanceof AKDepartment) {
            setText(((AKDepartment) value).getName());
        }

        return comp;
    }
}

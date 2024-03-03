/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2012 09:49:44
 */
package de.augustakom.authentication.gui.basics;

import java.awt.*;
import javax.swing.*;

import de.augustakom.authentication.model.AKTeam;

/**
 *
 */
public class TeamListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (value instanceof AKTeam) {
            setText(((AKTeam) value).getName());
        }

        return comp;
    }
}



/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2015 08:10
 */
package de.augustakom.authentication.gui.basics;

import de.augustakom.authentication.model.AKBereich;

import javax.swing.*;
import java.awt.*;

public class BereichListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (value instanceof AKBereich) {
            setText(((AKBereich) value).getName());
        }

        return comp;
    }

}

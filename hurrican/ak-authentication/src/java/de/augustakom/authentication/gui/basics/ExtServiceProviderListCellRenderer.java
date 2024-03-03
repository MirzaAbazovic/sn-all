/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2009 10:26:51
 */
package de.augustakom.authentication.gui.basics;

import java.awt.*;
import javax.swing.*;

import de.augustakom.authentication.model.AKExtServiceProviderView;


/**
 * ListCellRenderer fuer AKExtServiceProviderView-Objekte.
 *
 *
 */
public class ExtServiceProviderListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (value instanceof AKExtServiceProviderView) {
            setText(((AKExtServiceProviderView) value).getName());
        }

        return comp;
    }
}

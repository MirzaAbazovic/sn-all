/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2017
 */
package de.augustakom.hurrican.gui.tools.tal.wita.model;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;

public class WBCIVorabstimmungFaxComboBoxModel implements ComboBoxModel<String> {

    private List<WBCIVorabstimmungFax> vorabstimmungFaxes;
    private int index = -1;

    public WBCIVorabstimmungFaxComboBoxModel(List<WBCIVorabstimmungFax> vorabstimmungFaxes) {
        this.vorabstimmungFaxes = vorabstimmungFaxes;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        for (int i = 0; i < vorabstimmungFaxes.size(); i++) {
            if (getElementAt(i).equals(anItem)) {
                index = i;
                break;
            }
        }
    }

    @Override
    public Object getSelectedItem() {
        if (index > -1)
            return getElementAt(index);
        return "";
    }

    @Override
    public int getSize() {
        return vorabstimmungFaxes.size();
    }

    @Override
    public String getElementAt(int index) {
        return vorabstimmungFaxes.get(index).getVorabstimmungsId();
    }

    @Override
    public void addListDataListener(ListDataListener l) {

    }

    @Override
    public void removeListDataListener(ListDataListener l) {

    }
}
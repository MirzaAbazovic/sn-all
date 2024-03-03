/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.11.2011 18:09:17
 */
package de.augustakom.common.gui.swing.table;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Selection Model, dass keine Auswahl erlaubt
 */
public class NullSelectionModel implements ListSelectionModel {

    public static final NullSelectionModel INSTANCE = new NullSelectionModel();

    @Override
    public boolean isSelectionEmpty() {
        return true;
    }

    @Override
    public boolean isSelectedIndex(int index) {
        return false;
    }

    @Override
    public int getMinSelectionIndex() {
        return -1;
    }

    @Override
    public int getMaxSelectionIndex() {
        return -1;
    }

    @Override
    public int getLeadSelectionIndex() {
        return -1;
    }

    @Override
    public int getAnchorSelectionIndex() {
        return -1;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
    }

    @Override
    public void setLeadSelectionIndex(int index) {
    }

    @Override
    public void setAnchorSelectionIndex(int index) {
    }

    @Override
    public void addSelectionInterval(int index0, int index1) {
    }

    @Override
    public void insertIndexInterval(int index, int length, boolean before) {
    }

    @Override
    public void clearSelection() {
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
    }

    @Override
    public void removeIndexInterval(int index0, int index1) {
    }

    @Override
    public void setSelectionMode(int selectionMode) {
    }

    @Override
    public int getSelectionMode() {
        return SINGLE_SELECTION;
    }

    @Override
    public void addListSelectionListener(ListSelectionListener lsl) {
    }

    @Override
    public void removeListSelectionListener(ListSelectionListener lsl) {
    }

    @Override
    public void setValueIsAdjusting(boolean valueIsAdjusting) {
    }

    @Override
    public boolean getValueIsAdjusting() {
        return false;
    }
}


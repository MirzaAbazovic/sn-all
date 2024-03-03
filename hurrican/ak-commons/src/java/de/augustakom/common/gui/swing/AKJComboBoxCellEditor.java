/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2006 13:30:05
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * CellEditor fuer eine ComboBox.
 *
 *
 */
public class AKJComboBoxCellEditor extends DefaultCellEditor implements PopupMenuListener {

    private AKJComboBox comboBox = null;

    /**
     * Konstruktor mit Angabe der zu verwendenden ComboBox
     */
    public AKJComboBoxCellEditor(AKJComboBox comboBox) {
        super(comboBox);
        this.comboBox = comboBox;
        this.comboBox.addPopupMenuListener(this);
    }

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean,
     * int, int)
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (comboBox.getModelClass4Renderer() != null) {
            comboBox.selectItem(value);
        }
        else {
            comboBox.selectItemRaw(value);
        }
        return comboBox;
    }

    /**
     * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
     */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    /**
     * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    /**
     * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
     */
    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

}



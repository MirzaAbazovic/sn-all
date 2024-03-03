/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 06.10.2010 13:25:00
  */

package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.table.AKTableMap;
import de.augustakom.common.gui.swing.table.AKTableModelXML;

public class AKStringCellEditor extends DefaultCellEditor {

    private AKJTextField textField;

    public AKStringCellEditor(AKJTextField textField) {
        super(textField); // dummy call to satisfy parent constructor
        this.textField = textField;
        editorComponent = textField;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
        textField.setText((String) value);
        // Beruecksichtigung des Attributs "maxchars"
        if (table.getModel() instanceof AKTableMap) {
            AKTableMap tableMap = (AKTableMap) table.getModel();
            if (tableMap.getModel() instanceof AKTableModelXML<?>) {
                AKTableModelXML<?> tableModel = (AKTableModelXML<?>) tableMap.getModel();
                int maxChars = tableModel.getMaxChars(row, column);
                textField.setMaxChars(maxChars);
            }
        }

        return textField;
    }

    @Override
    public Object getCellEditorValue() {
        return textField.getText();
    }
}

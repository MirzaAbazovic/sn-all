/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 02.08.2010 10:31:28
  */

package de.augustakom.common.gui.swing;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 *
 */
public class AKJDateComponentCellEditor extends DefaultCellEditor {

    private AKJDateComponent dateComponent;

    public AKJDateComponentCellEditor(AKJDateComponent dateComponent) {
        super(new JTextField()); // dummy call to satisfy parent constructor
        this.dateComponent = dateComponent;
        editorComponent = dateComponent;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        dateComponent.setDate((Date) value);
        return dateComponent;
    }

    @Override
    public Object getCellEditorValue() {
        return dateComponent.getDate(null);
    }
}

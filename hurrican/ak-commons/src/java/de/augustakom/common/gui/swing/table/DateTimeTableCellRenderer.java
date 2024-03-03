/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2004 10:02:42
 */
package de.augustakom.common.gui.swing.table;

import java.time.*;
import java.time.format.*;
import javax.swing.table.*;

/**
 * TableCellRenderer, um DateTime-Objekte in einer Tabelle darzustellen.
 */
public class DateTimeTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -7426605321535199093L;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");

    public DateTimeTableCellRenderer() {
        super();
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof LocalDateTime) {
            setText(((LocalDateTime) value).format(DATE_TIME_FORMATTER));
        }
        else {
            setText("");
        }
    }
}

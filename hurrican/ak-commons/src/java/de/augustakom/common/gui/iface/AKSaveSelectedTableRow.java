/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.2011 11:17:17
 */
package de.augustakom.common.gui.iface;

/**
 * Interface für Tabellen, die einen speziellen Datensatz oder Felder innerhalb des Datensatzen speichern moechten.
 */
public interface AKSaveSelectedTableRow {

    public void saveRow(Object Row);
}



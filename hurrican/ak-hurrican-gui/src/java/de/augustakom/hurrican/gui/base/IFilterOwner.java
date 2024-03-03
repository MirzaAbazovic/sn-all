/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2004 08:53:12
 */
package de.augustakom.hurrican.gui.base;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.table.AKTableModel;


/**
 * Interface fuer GUI-Komponenten (z.B. Panels), ueber die ein Filter definiert werden kann.
 *
 * @param <S> Typ der Query mit der gesucht werden soll
 * @param <T> Typ des TableModel
 */
public interface IFilterOwner<S, T extends AKTableModel<?>> {

    /**
     * Gibt den definierten Filter zurueck.
     *
     * @return das Filter-Objekt des jeweiligen Panels
     * @throws HurricanGUIException wenn das Filter-Objekt ungueltige Werte enthaelt.
     */
    public S getFilter() throws HurricanGUIException;

    /**
     * Veranlasst das Panel dazu, die Suche durchzufuehren. <br> Das Ergebnis der Suche wird als
     * <code>resultTableModel</code> zurückgeliefert. Diese Methode darf keine GUI-Methoden aufrufen, da sie
     * normalerweise in einem Workerthread ausgeführt wird, d.h. nicht im EDT Thread.
     *
     * @param AKTableModel, in das das Suchergebnis geschrieben wurde.
     *
     */
    public T doSearch(S filter) throws HurricanGUIException;

    /**
     * Veranlasst das Panel dazu, einen Table so zu formatieren, so dass die Daten aus dem Model schön angezeigt werden.
     * <br>
     *
     * @param tableModel  Modell, in dem das Suchergebnis steht.
     * @param resultTable Tabelle, in der das Suchergebnis dargestellt wird.
     * @throws HurricanGUIException wird geworfen, wenn bei der Suche ein Fehler auftritt.
     *
     */
    public void updateGui(T tableModel, AKJTable resultTable);

    /**
     * Setzt die Fitler-Daten auf die Standardwerte zurueck.
     */
    public void clearFilter();
}

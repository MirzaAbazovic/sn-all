/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2010 11:32:35
 */
package de.augustakom.hurrican.gui.verlauf;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Interface für das korrekte Laden der Tabelle mit Bauaufträgen (meist über SwingWorker).
 *
 * @param <T> Typ der geladenen Bauaufträge
 */
public interface LoadBauauftragTable<T> {

    /**
     * Veranlasst ein Bauauftrag Panel dazu, die Tabellendaten (Bauaufträge) zu laden.<br/> Die Ergebnis-Daten werden
     * als Liste vom Type <code>T</code> zurückgeliefert. <p><b>Achtung:</b> Diese Methode darf keine GUI-Methoden
     * aufrufen, da sie normalerweise in einem Workerthread ausgeführt wird, d.h. <b>nicht</b> im EDT Thread.</p>
     *
     * @param abteilungId      für die zu ladenen Daten
     * @param realisierungFrom Realisierungstermin soll später oder gleich sein oder {@code null} falls nicht zu
     *                         beachten
     * @param realisierungTo   Realisierungstermin soll früher oder gleich sein oder {@code null} falls nicht zu
     *                         beachten
     * @throws ServiceNotFoundException falls ein Fehler beim Laden des Service zum Laden der Daten auftritt
     * @throws FindException            falls ein Fehler Laden der Daten auftritt
     */
    public List<T> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException;

    /**
     * Veranlasst das Bauauftrag Panel dazu, die im {@link #loadTableData(Integer)} geladenen Daten zu setzen, so dass
     * die Daten aus der Liste angezeigt werden.<br/>
     *
     * @param tableData Liste mit den anzuzueigenden Daten
     */
    public void updateGuiByTableData(List<T> tableData);

    /**
     * Setzt die angezeigten Daten der Tabelle auf die Standardwerte (leer) zurück.
     */
    public void clearTable();
}

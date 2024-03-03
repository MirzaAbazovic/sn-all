/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2004 10:29:53
 */

package de.augustakom.common.gui.swing.table;

import java.util.*;
import javax.swing.table.*;

/**
 * Interface fuer TableModels, die ein 'komplettes' Objekt einer bestimmten Zeile zurueck geben koennen. <br> Ausserdem
 * muessen Implementierungen des Interfaces erkennen koenne, ob sich die Darstellungsreihenfolge (Sortierung) der
 * Tabelle geaendert hat und in einem solchen Fall immer noch das richtige Objekt zurueck geben.
 */
public interface AKMutableTableModel<T> extends TableModel {

    /**
     * Setzt die Daten fuer das TableModel.
     */
    void setData(Collection<T> data);

    /**
     * Gibt die Daten des TableModels zurueck. <br> Voraussetzung: Die Daten wurden zuvor ueber die Methode
     * <code>setData</code> gesetzt.
     *
     * @return Collection mit den Daten-Objekten.
     */
    Collection<T> getData();

    /**
     * Fuegt dem TableModel ein neues Objekt hinzu.
     */
    void addObject(T obj);

    /**
     * Fuegt dem TableModel neue Objekt hinzu.
     */
    void addObjects(Collection<? extends T> objs);

    /**
     * Entfernt das angegebene Objekt aus dem TableModel bzw. aus der Data-Liste.
     */
    void removeObject(Object objToRemove);

    /**
     * Entfernt alle Eintraege aus dem TableModel.
     */
    void removeAll();

    /**
     * Entfernt die angegebenen Objekte aus dem TableModel bzw. aus der Data-Liste.
     */
    void removeObjects(Collection<? extends T> objs);

    /**
     * Liefert das Objekt des TableModels an der angegebenen Zeile.
     *
     * @param row Zeile, dessen Objekt angefordert wird
     * @return Objekt an der angegebenen Zeile.
     */
    T getDataAtRow(int row);

    void setDisabledRows(Collection<Integer> disabledRows);

    void disableRow(int row);

    void enableRow(int row);

    void enableAllRows();

    boolean isRowEnabled(int row);

    default Optional<Integer> getColumnIndex(String columnName) {
        return Optional.empty();
    }

}

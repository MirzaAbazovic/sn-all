/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.08.2004 09:34:29
 */
package de.augustakom.common.gui.swing.table;


/**
 * Interface fuer TableModels, die Filter-Objekte auswerten koennen.
 *
 *
 */
public interface AKFilterTableModel {

    /**
     * Setzt den Filter auf den uebergebenen Filter
     *
     * @param relation Der Filter, der genutzt werden soll
     */
    void setFilter(FilterRelation relation);

    /**
     * Fuegt den gegebenen Filter zu den bereits genutzten Filtern hinzu.
     *
     * @param relation Der Filter, der hinzugefuegt werden soll
     */
    void addFilter(FilterRelation relation);

    /**
     * Funktion ermöglicht das Filtern der Daten.
     */
    void addFilter(FilterOperator operator);

    /**
     * Entfernt den Filter mit dem gegebenen Namen. Wird {@code null} als Name uebergeben, werden alle unbenannten
     * Filter geloescht.
     *
     * @param name Name des Filters
     */
    void removeFilter(String name);

    /**
     * Loescht alle Filter. Meist sollte {@code removeFilter(null)} genutzt werden, was benannte Filter belaesst.
     */
    void clearFilter();

    /**
     * Fügt einen Listener hinzu der bei einer Filterung informiert wird
     */
    void addFilterTableModelListener(AKFilterTableModelListener listener);

    /**
     * Entfernt den Listener
     */
    void removeFilterTableModelListener();
}



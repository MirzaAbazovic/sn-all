/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2005 15:47:52
 */
package de.augustakom.common.gui.swing.table;


/**
 * Interface fuer TableModels, die den TableSorter 'kennen'.
 *
 *
 */
public interface AKTableSorterAware<T> {

    /**
     * Uebergibt der Implementierung den verwendeten TableSorter.
     *
     * @param tableSorter
     */
    public void setTableSorter(AKTableSorter<T> tableSorter);

}



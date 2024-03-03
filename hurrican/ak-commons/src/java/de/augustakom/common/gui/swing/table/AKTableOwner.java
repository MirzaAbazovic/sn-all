/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2004 11:06:55
 */
package de.augustakom.common.gui.swing.table;


/**
 * Interface fuer Panels/Frames, die eine Tabelle darstellen und auf eine Selektion reagieren wollen.
 *
 *
 */
public interface AKTableOwner {

    /**
     * Wird vom AKTableListener aufgerufen, wenn eine Zeile selektiert wird. <br> Die Implementierung kann dadurch die
     * Details des selektierten Datensatzes anzeigen.
     *
     * @param details
     */
    public void showDetails(Object details);

}



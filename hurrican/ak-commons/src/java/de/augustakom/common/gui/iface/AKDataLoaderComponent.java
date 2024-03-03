/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2004 11:09:23
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer GUI-Komponenten (z.B. Panels, Frames), die Daten laden muessen/koennen.
 */
public interface AKDataLoaderComponent {

    /**
     * Enthaelt Code, um die benoetigten Daten zu laden.
     */
    public void loadData();

}



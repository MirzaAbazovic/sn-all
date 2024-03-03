/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.2005 10:23:38
 */
package de.augustakom.common.gui.iface;


/**
 * Interface fuer GUI-Komponenten, die eine Suche ausfuehren.
 *
 *
 */
public interface AKSearchComponent {

    /**
     * Veranlasst die Komponente, eine (beliebige) Suche durchzufuehren. Fuer die Ermittlung der Such-Parameter, die
     * Verarbeitung des Such-Ergebnisses und eine evtl. Fehler-Behandlung ist die Komponente selbst verantwortlich.
     */
    public void doSearch();

}



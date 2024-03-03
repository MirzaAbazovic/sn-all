/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 11:56:13
 */
package de.augustakom.common.gui.iface;

import de.augustakom.common.gui.exceptions.AKGUIException;


/**
 * Interface fuer GUI-Komponenten (z.B. Panels, Frames), die Daten darstellen, anlegen und speichern koennen.
 */
public interface AKModelOwner extends AKSimpleModelOwner {

    /**
     * Veranlasst den Owner dazu, das Modell auszulesen und die Daten darzustellen. <br> In dieser Methoden koennen
     * evtl. auch noch weitere benoetigte Daten geladen werden.
     *
     * @throws AKGUIException wenn beim Lesen der Daten ein Fehler auftritt.
     */
    public void readModel() throws AKGUIException;

    /**
     * Veranlasst den Owner dazu, eine Speicher-Operation durchzufuehren. <br> Welche Daten gespeichert werden, wird von
     * dem Owner selbst bestimmt.
     *
     * @throws AKGUIException wenn beim Speichern der Daten ein Fehler auftritt.
     */
    public void saveModel() throws AKGUIException;

    /**
     * Ueberprueft, ob Werte des Modells geaendert wurden.
     *
     * @return true wenn das Modell geaendert wurde.
     */
    public boolean hasModelChanged();
}



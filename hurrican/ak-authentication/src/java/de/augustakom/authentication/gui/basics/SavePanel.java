/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.05.2004 09:06:26
 */
package de.augustakom.authentication.gui.basics;

import de.augustakom.authentication.gui.exceptions.GUIException;


/**
 * Interface definiert Methoden fuer Panels, die speicherbare Daten darstellen und auch fuer das Speichern dieser Daten
 * verantwortlich sind.
 */
public interface SavePanel {

    /**
     * Veranlasst das Panel dazu, die Daten zu speichern.
     *
     * @throws GUIException wenn waehrend dem Speichern ein Fehler auftritt.
     */
    void doSave() throws GUIException;

}

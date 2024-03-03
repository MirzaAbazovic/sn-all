/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 13:32:41
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKApplication;


/**
 * Interface definiert Methoden fuer DAO-Objekte zur Applikationsverwaltung.
 */
public interface AKApplicationDAO {

    /**
     * Gibt eine Liste aller verfuegbaren Applikationen zurueck.
     *
     * @return Liste mit AKApplication-Objekten (never {@code null}).
     */
    List<AKApplication> findAll();

    /**
     * Sucht nach einer Application mit dem angegebenen Namen
     *
     * @param name Name der gesuchten Applikation.
     * @return Gefundene Application oder <code>null</code>
     */
    AKApplication findApplicationByName(String name);

}

/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.06.2004 14:49:26
 */
package de.augustakom.authentication.dao;

import de.augustakom.authentication.model.AKGUIComponent;


/**
 * DAO-Definition fuer Klassen des Typs <code>AKGUIComponent</code>.
 */
public interface AKGUIComponentDAO {

    /**
     * Legt einen neuen Eintrag fuer das Objekt an oder aktualisiert einen vorhandenen Eintrag.
     */
    void save(AKGUIComponent toSave);

    /**
     * Loescht das Objekt aus der DB.
     */
    void delete(AKGUIComponent toDelete);

    /**
     * Sucht nach einer bestimmten Komponente ueber deren Namen und Parent sowie der Applikation.
     *
     * @param name          Name der gesuchten Komponente
     * @param parent        Parent der gesuchten Komponente
     * @param applicationId ID der Applikation, in der die Komponente dargestellt wird.
     * @return die gefundene Komponente oder <code>null</code>
     */
    AKGUIComponent find(String name, String parent, Long applicationId);

}



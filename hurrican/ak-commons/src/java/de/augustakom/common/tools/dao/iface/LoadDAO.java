/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 14:08:03
 */
package de.augustakom.common.tools.dao.iface;

import java.io.*;


/**
 * Interface definiert Methoden, um ein Objekt ueber dessen ID zu laden.
 *
 *
 */
public interface LoadDAO {

    /**
     * Sucht nach einem Datensatz mit der ID <code>id</code> und speichert die Daten in dem Objekt <code>object</code>.
     *
     * @param object
     * @param id
     */
    public void load(Object object, Serializable id);

}



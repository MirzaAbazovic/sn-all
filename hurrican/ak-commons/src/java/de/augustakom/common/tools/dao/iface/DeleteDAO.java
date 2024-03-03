/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 09:55:34
 */
package de.augustakom.common.tools.dao.iface;

import java.io.*;


/**
 * Interface definiert Methoden fuer DAO-Implementierungen, die Objekte bzw. Datensaetze loeschen koennen. <br> Evtl.
 * auftretende Exceptions werden von den DAO-Implementierungen als Runtime-Exception weitergeleitet.
 *
 *
 */
public interface DeleteDAO {

    /**
     * Loescht ein Objekt (bzw. einen Datensatz) ueber dessen ID.
     *
     * @param id ID des zu loeschenden Objekts.
     */
    void deleteById(Serializable id);

}



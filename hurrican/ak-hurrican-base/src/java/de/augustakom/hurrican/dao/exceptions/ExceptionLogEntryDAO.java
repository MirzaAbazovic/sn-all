/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 15:22:14
 */
package de.augustakom.hurrican.dao.exceptions;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;

/**
 * DAO-Interface fuer Objekte des Typs <code>ExceptionLogEntry</code>
 */
public interface ExceptionLogEntryDAO extends StoreDAO, FindDAO {

    /**
     * Ermittelt die Anzahl an noch offenen neuen Exception Log Einträgen, die noch nicht
     * bearbeitet bzw. bei denen noch keine Loesung angegeben ist.
     * @return
     */
    public long getNewExceptionLogEntriesCount();

    /**
     * Holt alle neuen Eintraege aus der T_Exception_Log, die noch nicht bearbeitet bzw. bei denen noch keine Loesung
     * angegeben ist. Die Abfrage wird durch den exception log entry context identifier eingeschränkt. <br/>
     * Die Ermittlung erfolgt sortiert (desc) nach ID.
     * 
     * @param context the exception log entry context holding the log identifier to serach for.
     * @param maxResults
     * @return Liste der neuen ExceptionLog-Eintraege
     */
    public List<ExceptionLogEntry> findNewExceptionLogEntries(ExceptionLogEntryContext context, int maxResults);
}

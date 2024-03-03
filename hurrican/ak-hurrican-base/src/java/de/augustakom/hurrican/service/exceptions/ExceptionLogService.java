/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 15:35:49
 */
package de.augustakom.hurrican.service.exceptions;

import java.util.*;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.cc.ICCService;

/**
 * Service-Interface fuer Eintraege in der ExceptionLog
 */
public interface ExceptionLogService extends ICCService {

    /**
     * Methode um eine Exception in der DB zu speichern
     *
     * @param exception - ExcepetionLog - Eintrag
     * @return der gespeicherte Eintrag in der DB
     */
    ExceptionLogEntry saveExceptionLogEntry(ExceptionLogEntry exception);

    /**
     * Holt sich einen Eintrag aus der ExceptionLog anhand der ID
     *
     * @param id des ExceptionLog-Eintrags
     * @return der gefundene ExceptionLog-Eintrag
     */
    ExceptionLogEntry findExceptionLogEntryById(Long id);

    /**
     * Holt alle neuen Eintraege aus der ExceptionLog, die noch nicht bearbeitet werden
     *
     * @return Liste der neuen ExceptionLog-Eintraege
     */
    String getMailtextForThrownExceptions();

    /**
     * Ermittelt alle 'neuen' ExceptionLog-Eintraege. <br/>
     * Also Eintraege, die noch nicht zugewiesen / bearbeitet wurden.
     * 
     * @param context the context with the log identifier to search for.
     * @param maxResults
     * @return
     */
    List<ExceptionLogEntry> findNewExceptionLogEntries(ExceptionLogEntryContext context, int maxResults);

}

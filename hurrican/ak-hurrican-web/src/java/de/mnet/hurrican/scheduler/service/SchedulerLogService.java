/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 16:16:07
 */
package de.mnet.hurrican.scheduler.service;

import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException;
import de.mnet.hurrican.scheduler.model.ExportedBillingFile;
import de.mnet.hurrican.scheduler.service.utils.SchedulerService;

/**
 * Service fuer das Logging von verschiedenen Vorgaengen waehrend einer Job-Ausfuehrung.
 *
 *
 */
public interface SchedulerLogService extends SchedulerService {

    /**
     * Protokolliert den Export eines Billing-Files (z.B. Rechnung od. EVN) mit.
     */
    public void logRechnungsExport(ExportedBillingFile toLog) throws AKSchedulerStoreException;

    /**
     * Ueberprueft, ob ein bestimmtes File bereits exportiert wurde bzw. ob eine entsprechende Protokollierung vorhanden
     * ist.
     *
     * @return true wenn eine Protokollierung vorhanden ist.
     * @throws AKSchedulerFindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public boolean isLogged(String year, String month, String filename) throws AKSchedulerFindException;

}

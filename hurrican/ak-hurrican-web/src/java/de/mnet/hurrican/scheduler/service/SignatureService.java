/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2006 17:42:16
 */
package de.mnet.hurrican.scheduler.service;

import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException;
import de.mnet.hurrican.scheduler.model.SignaturedFile;
import de.mnet.hurrican.scheduler.service.utils.SchedulerService;

/**
 * Service-Interface fuer die Verwaltung von Signaturen.
 */
public interface SignatureService extends SchedulerService {

    /**
     * Speichert das angegebene Modell.
     */
    void save(SignaturedFile toSave) throws AKSchedulerStoreException;

    /**
     * Sucht nach dem Signierungs-Eintrag fuer die Datei mit dem Namen 'filename'.
     *
     * @param isAbsolutePath Angabe, ob als 'filename' der absolute Pfad (true) oder nur der Dateiname (false) angegeben
     *                       wird.
     */
    SignaturedFile findSignaturedFile(String filename, boolean isAbsolutePath) throws AKSchedulerFindException;
}

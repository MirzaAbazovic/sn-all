/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm;

import javax.validation.*;

import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.service.WitaService;

/**
 * Abstraction-Layer fuer Services, die den Workflow Tasks bereitstehen.
 */
public interface WorkflowTaskValidationService extends WitaService {

    /**
     * Validiert einen ausgehenden Request. Gibt eine ValidationException zurueck, falls ein Fehler auftaucht, so dass
     * der Aufrufer den Fehler behandeln muss.
     */
    <T extends MnetWitaRequest> void validateMwfOutput(T request) throws ValidationException;

    /**
     * Validiert eine eingehende Nachricht.
     *
     * @return null, wenn die Validierung erfolgreich war, die Fehlermeldung sonst
     */
    String validateMwfInput(Meldung<?> meldung);

}

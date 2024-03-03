/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2011 14:47:34
 */
package de.mnet.wita.bpm.tasks;

import javax.validation.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.service.WitaConfigService;

/**
 * Implementiert Exception Handling fuer ausgehende Wita Nachrichten.
 */
public abstract class AbstractSendingWitaTask extends AbstractWitaTask {

    @Autowired
    private WitaConfigService witaConfigService;

    @Override
    public final void execute(DelegateExecution execution) throws Exception {
        try {
            WitaCdmVersion witaCdmVersion = witaConfigService.getDefaultWitaVersion();
            send(execution, witaCdmVersion);
        }
        catch (ValidationException e) {
            throw new WitaDataAggregationException(
                    "Bei der Validierung der WITA Daten wurden fehlerhafte Daten entdeckt: " + e.getMessage(), e);
        }
        catch (WitaDataAggregationException e) {
            throw new WitaDataAggregationException("WITA-Vorgang konnte nicht ausgeloest werden.\nGrund: "
                    + e.getMessage(), e);
        }
    }

    protected abstract void send(DelegateExecution execution, WitaCdmVersion witaCdmVersion) throws Exception;
}

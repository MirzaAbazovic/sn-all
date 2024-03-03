/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2011 09:12:11
 */
package de.mnet.wita.message.meldung;

import de.mnet.wita.activiti.CanOpenActivitiWorkflow;
import de.mnet.wita.message.IncomingMessage;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.validators.getters.HasVertragsNummer;

/**
 * Marker interface for all meldung types that should be passed to the abgebend pv workflow.
 */
public interface IncomingPvMeldung extends CanOpenActivitiWorkflow, IncomingMessage, HasVertragsNummer {

    /**
     * Id is required for AbgebendPvWorkflowService to get it for Activiti to handle message properly
     */
    Long getId();

    MeldungsType getMeldungsTyp();

    void setExterneAuftragsnummer(String extAuftragsNummer);

}

/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2014
 */
package de.mnet.wita.service;

import javax.validation.constraints.*;

import de.mnet.wita.WitaMessage;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;

/**
 *
 */
public interface WitaSendMessageService extends WitaService {

    /**
     * Sends a {@link de.mnet.wita.WitaMessage} to the LineOrderService and further processes the message
     *
     * @param message the messsge to be sent
     */
    <T extends WitaMessage> void sendAndProcessMessage(@NotNull T message);

    /**
     * Sends out the before scheduled {@link Auftrag} to the LineOrderService or trigger the   {@link
     * TalOrderWorkflowService#sendTvOrStornoRequest(MnetWitaRequest)} to processed the Workflow to the send out the TV
     * or Storno.
     *
     * @param unsentWitaRequestId the ID of the unsent {@link Auftrag}, {@link Storno} or {@link TerminVerschiebung}
     * @return true if send processing runs correctly, otherwise false
     * @throws WitaBaseException for foreseeable Errors during the sending
     */
    boolean sendScheduledRequest(@NotNull Long unsentWitaRequestId) throws WitaBaseException;
}

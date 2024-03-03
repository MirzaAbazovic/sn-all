/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2013 14:35:27
 */
package de.mnet.wbci.service;

import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.WbciMessage;

public interface WbciSendMessageService extends WbciService {

    /**
     * Sends a {@link de.mnet.wbci.model.WbciMessage} to the CarrierNegotiationService and further processes the message
     * (updates the geschaeftsfall status, creates an IO archive entry).
     *
     * @param message the messsge to be sent
     */
    <T extends WbciMessage> void sendAndProcessMessage(T message);

    /**
     * Sends a {@link de.mnet.wbci.model.WbciMessage} to the CarrierNegotiationService and further processes the message
     * (updates the geschaeftsfall status, creates an IO archive entry).
     *
     * @param metadata details relating to the processing or generation of the message. The processing details influence
     *                 what steps are taken later on (like creating an IoArchive entry) after sending the message.
     * @param message  the messsge to be sent
     */
    <T extends WbciMessage> void sendAndProcessMessage(MessageProcessingMetadata metadata, T message);
}

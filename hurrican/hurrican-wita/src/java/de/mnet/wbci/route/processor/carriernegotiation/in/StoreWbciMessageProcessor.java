/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.in;

import java.time.*;
import java.util.*;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.route.HurricanInProcessor;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciRequestService;

/**
 * Camel-Prozessor, um eine WBCI-Message (sowohl Meldungen als auch Requests) zu speichern.
 */
@Component
public class StoreWbciMessageProcessor extends HurricanInProcessor {

    @Autowired
    private WbciMeldungService wbciMeldungService;
    @Autowired
    private WbciRequestService wbciRequestService;
    @Autowired
    private MessageProcessingMetadataHelper metadataHelper;

    @Override
    public void process(Exchange exchange) throws Exception {
        WbciMessage wbciMessage = getOriginalMessage(exchange);
        final LocalDateTime creationDate = LocalDateTime.now();
        MessageProcessingMetadata processingMetadata = metadataHelper.getMessageProcessingMetadata(exchange);

        if (wbciMessage instanceof WbciRequest) {
            WbciRequest wbciRequest = (WbciRequest) wbciMessage;
            wbciRequest.setCreationDate(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
            wbciRequest.setUpdatedAt(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
            wbciRequest.setProcessedAt(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
            wbciRequest.setIoType(IOType.IN);
            wbciRequestService.processIncomingRequest(processingMetadata, wbciRequest);
        }
        else if (wbciMessage instanceof Meldung) {
            wbciMessage.setProcessedAt(Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant()));
            wbciMessage.setIoType(IOType.IN);
            wbciMeldungService.processIncomingMeldung(processingMetadata, (Meldung) wbciMessage);
        }
        else {
            String type = (wbciMessage != null) ? wbciMessage.getClass().getName() : "<null>";
            throw new MessageProcessingException(String.format("Could not process object of type %s", type));
        }
    }

}

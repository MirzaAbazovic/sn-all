/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 14:36:29
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.wbci.integration.CarrierNegotationService;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;
import de.mnet.wbci.service.WbciSendMessageService;

@CcTxRequired
public class WbciSendMessageServiceImpl implements WbciSendMessageService {

    private static final Logger LOGGER = Logger.getLogger(WbciSendMessageServiceImpl.class);

    @Autowired
    CamelProxyLookupService camelProxyLookupService;

    @Override
    public <T extends WbciMessage> void sendAndProcessMessage(T message) {
        sendAndProcessMessage(new MessageProcessingMetadata(), message);
    }

    @Override
    public <T extends WbciMessage> void sendAndProcessMessage(MessageProcessingMetadata metadata, T message) {
        LOGGER.trace("Sending and processing WBCI Message (Request or Notification)");
        CarrierNegotationService carrierNegotationService =
                camelProxyLookupService.lookupCamelProxy(PROXY_CARRIER_NEGOTIATION,
                        CarrierNegotationService.class);
        Map<String, Object> options = new HashMap<>();
        options.put(MessageProcessingMetadataHelper.MESSAGE_PROCESSING_METADATA_KEY, metadata);
        carrierNegotationService.sendToWbci(message, options);
    }
}

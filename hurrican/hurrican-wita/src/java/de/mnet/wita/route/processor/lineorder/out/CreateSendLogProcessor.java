/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2014
 */
package de.mnet.wita.route.processor.lineorder.out;

import java.time.*;
import java.util.*;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wita.OutgoingWitaMessage;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.service.WitaConfigService;

/**
 *
 */
@Component
public class CreateSendLogProcessor extends HurricanOutProcessor implements WbciCamelConstants {

    @Autowired
    private WitaConfigService witaConfigService;

    @Autowired
    private MwfEntityDao mwfEntityDao;

    @Override
    public void process(Exchange exchange) throws Exception {
        OutgoingWitaMessage message = getOriginalMessage(exchange);
        if (message instanceof MnetWitaRequest) {
            MnetWitaRequest mnetWitaRequest = (MnetWitaRequest) message;
            mnetWitaRequest.setSentAt(new Date());
            mwfEntityDao.store(mnetWitaRequest);
            witaConfigService.createSendLog(mnetWitaRequest, LocalDateTime.now());
        }
    }

}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.13
 */
package de.mnet.wita.route.processor.lineorder.out;

import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.service.WitaConfigService;

/**
 * Apache Camel Processor, um die CDM-Version zu ermitteln und abhaengig von dieser Version die JMS Destination URL zu
 * setzen.
 */
@Component("WitaEvaluateCdmVersionProcessor")
public class EvaluateCdmVersionProcessor extends HurricanOutProcessor implements WbciCamelConstants {

    @Value("${atlas.lineorderservice.out.queue}")
    String lineOrderServiceOut;

    @Autowired
    private WitaConfigService witaConfigService;

    @Override
    public void process(Exchange exchange) throws Exception {
        WitaCdmVersion cdmVersion = witaConfigService.getDefaultWitaVersion();

        String destinationUrl = String.format(lineOrderServiceOut, cdmVersion.getVersion());
        exchange.getIn().setHeader(JmsConstants.JMS_DESTINATION_NAME, destinationUrl);
    }

}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.out;

import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wita.service.WitaConfigService;

/**
 * Apache Camel Processor, um die CDM-Version zu ermitteln und abhaengig von dieser Version die JMS Destination URL zu
 * setzen.
 */
@Component("WbciEvaluateCdmVersionProcessor")
public class EvaluateCdmVersionProcessor extends HurricanOutProcessor implements WbciCamelConstants {

    @Value("${atlas.carriernegotiationservice.out.queue}")
    String carrierNegotiationServiceOut;

    @Autowired
    private WitaConfigService witaConfigService;

    @Override
    public void process(Exchange exchange) throws Exception {
        WbciMessage wbciMessage = getOriginalMessage(exchange);
        WbciCdmVersion wbciCdmVersion = witaConfigService.getWbciCdmVersion(getEKPMessageRecipient(wbciMessage));

        String destinationUrl = String.format(carrierNegotiationServiceOut, wbciCdmVersion.getVersion());
        exchange.getIn().setHeader(JmsConstants.JMS_DESTINATION_NAME, destinationUrl);
        exchange.getIn().setHeader(AtlasEsbConstants.CDM_VERSION_KEY, wbciCdmVersion);
    }

    private CarrierCode getEKPMessageRecipient(WbciMessage wbciMessage) {
        CarrierCode ekpPartner = wbciMessage.getEKPPartner();

        if (ekpPartner == null) {
            throw new IllegalArgumentException("Could not identify the target carrier from wbci message type '" + wbciMessage.getClass().getSimpleName() + "'");
        }

        return ekpPartner;
    }

}

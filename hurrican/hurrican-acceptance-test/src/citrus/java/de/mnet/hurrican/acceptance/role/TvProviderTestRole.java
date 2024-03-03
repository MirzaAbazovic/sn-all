/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.2014
 */
package de.mnet.hurrican.acceptance.role;

import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public class TvProviderTestRole extends AbstractTestRole {

    public static final String SOAP_ACTION = "citrus_soap_action";

    @Autowired
    @Qualifier("tvProvider")
    private WebServiceClient tvProviderWebClient;

    /**
     * Sends TV Provider Web Service request.
     * @return
     */
    public SendMessageActionDefinition sendTvProviderRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(tvProviderWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/getTvAvailabilityInformation")
                .description(String.format("Sending TV-Provider Web Service request %s", payloadTemplate));
    }

    /**
     * Receives TV Provider Web Service response.
     * @return
     */
    public ReceiveMessageActionDefinition receiveTvProviderResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(tvProviderWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving TV-Provider Web Service response %s", payloadTemplate));
    }

}

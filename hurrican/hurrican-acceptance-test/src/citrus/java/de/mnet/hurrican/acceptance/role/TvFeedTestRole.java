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
public class TvFeedTestRole extends AbstractTestRole {

    public static final String SOAP_ACTION = "citrus_soap_action";

    @Autowired
    @Qualifier("tvFeed")
    private WebServiceClient tvFeedWebClient;

    /**
     * Sends TV Feed Web Service request.
     * @return
     */
    public SendMessageActionDefinition sendTVFeedData4GeoIdsRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(tvFeedWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/getTVFeedData4GeoIds")
                .description(String.format("Sending TVFeedData4GeoIds Web Service request %s", payloadTemplate));
    }

    /**
     * Receives TV Feed Web Service response.
     * @return
     */
    public ReceiveMessageActionDefinition receiveTVFeedData4GeoIdsResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(tvFeedWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving TVFeedData4GeoIds Web Service response %s", payloadTemplate));
    }

    /**
     * Sends TV Feed Web Service request.
     * @return
     */
    public SendMessageActionDefinition sendTVFeedData4TechLocationsRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(tvFeedWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/getTVFeedData4TechLocations")
                .description(String.format("Sending TVFeedData4TechLocations Web Service request %s", payloadTemplate));
    }

    /**
     * Receives TV Feed Web Service response.
     * @return
     */
    public ReceiveMessageActionDefinition receiveTVFeedData4TechLocationsResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(tvFeedWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving TVFeedData4TechLocations Web Service response %s", payloadTemplate));
    }

}

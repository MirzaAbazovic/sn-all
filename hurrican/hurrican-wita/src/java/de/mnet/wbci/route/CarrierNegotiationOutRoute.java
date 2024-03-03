/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.13
 */
package de.mnet.wbci.route;

import org.apache.camel.LoggingLevel;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.route.ExtractExchangeOptionsProcessor;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.route.handler.AfterWbciMessageSentHandler;
import de.mnet.wbci.route.helper.MessageProcessingMetadataHelper;
import de.mnet.wbci.route.processor.carriernegotiation.out.CustomerServiceProtocolOutProcessor;
import de.mnet.wbci.route.processor.carriernegotiation.out.WbciIoArchiveOutProcessor;

/**
 * Simple Camel-Route, um ein asynchrones Senden eines WBCI-Requests zu realisieren. (Request wird per JMS auf den Atlas
 * gesendet; eine Antwort wird nicht erwartet!) <br><br> Die Camel-Route ist dafuer verantwortlich, die WBCI Messages
 * aus dem Hurrican-Format in das Atlas CDM Format zu transformieren und anschliessend an den Atlas zu senden.
 */
public class CarrierNegotiationOutRoute extends AbstractCarrierNegotiationOutRoute {

    @Autowired
    private ExtractExchangeOptionsProcessor extractExchangeOptionsProcessor;

    @Autowired
    private WbciIoArchiveOutProcessor wbciIoArchiveOutProcessor;

    @Autowired
    private AfterWbciMessageSentHandler afterWbciMessageSentHandler;

    @Autowired
    private CustomerServiceProtocolOutProcessor customerServiceProtocolOutProcessor;

    @Autowired
    private MessageProcessingMetadataHelper metadataHelper;

    public void addCarrierNegotiationOutRoutes() {
        addCarrierNegotiationServiceOutRoute();
    }


    /**
     * Builds Camel out route for sending carrier negotiation service requests to Atlas ESB with additional business
     * logic such as IO archive entries.
     * <p/>
     * If the outgoing message is the result of a communication error (e.g. ABBM Message to carriers that have sent an
     * invalid Request or Meldung to M-Net, like Duplicate Requests, TV sent by donating carrier, etc), the WBCI
     * database can be hold clean and consistent, by not persist the invalid inbound message from the carrier. Also the
     * corresponding outbound message shouldn't be persisted.
     * <p/>
     * When invoking this route, the caller can additionally determine if a record of the outbound message is a response
     * : <ul> <li>to an invalid request, set {@link MessageProcessingMetadata#isPostProcessMessage()}<br> <b>=>
     * postprocessing will be skipped</b> </br></li> <li>to a duplicated VA request, set {@link
     * MessageProcessingMetadata#isResponseToDuplicateVaRequest}<br> <b>=> only IO archive entry will be skipped</b>
     * </br></li> </ul>
     */
    private void addCarrierNegotiationServiceOutRoute() {
        // @formatter:off
        from("direct:atlasCarrierNegotiationOut")
            .transacted("ems.required")
            .inOnly(String.format("%s:queue:atlasCdmQueue", routeConfigHelper.getAtlasOutComponent()))
            .end()
            .log(LoggingLevel.INFO, "Message successfully sent to Atlas ESB")
        .end();

        from("direct:carrierNegotiationService")
            .transacted("db.required")
            .routeId(WBCI_CARRIER_NEGOTIATION_OUT_ROUTE_ID)
            .process(extractExchangeOptionsProcessor)
            // Destination-URL ermitteln, da versions-abhaengig
            .process(evaluateCdmVersionProcessor)
            // convert the Hurrican WBCI request to CDM format
            .process(convertWbciMessageToCdmProcessor)
            // send CDM format to Atlas
            .to("direct:atlasCarrierNegotiationOut")
            .choice()
                .when(metadataHelper.isPostProcessMessage())
                    // Status und processedAt auf wbci message setzen und speichern
                    .bean(afterWbciMessageSentHandler)
                    // call IoArchiveProcessor to create a new IoArchive entry for the current WBCI message
                    .process(wbciIoArchiveOutProcessor)
                    .process(customerServiceProtocolOutProcessor)
                .endChoice()
                .otherwise()
                    .log(LoggingLevel.INFO, "Skipping post-processing since this is a response to an invalid request")
                    .choice()
                        .when(metadataHelper.isResponseToDuplicateVaRequest())
                            .log(LoggingLevel.INFO, "Skipping recording of response in IoArchive since this is a response to a duplicate VA request")
                        .endChoice()
                        .otherwise()
                            // call IoArchiveProcessor to create a new IoArchive entry for the current WBCI message
                            .process(wbciIoArchiveOutProcessor)
                        .endChoice()
                    .end()
                .endChoice()
            .end()
            .log(LoggingLevel.INFO, "Message processing complete")
            .end();
        // @formatter:on
    }
}

/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.06.2014
 */
package de.mnet.wbci.acceptance.common.role;

import com.consol.citrus.TestActor;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.ws.server.WebServiceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public class ElektraTestRole extends AbstractTestRole {

    @Autowired
    @Qualifier("elektraTestActor")
    protected TestActor elektraTestActor;
    @Autowired
    @Qualifier("elektraWebServer")
    private WebServiceServer elektraWebServer;

    public void purgeAllJmsQueues() {
        if (!elektraTestActor.isDisabled()) {
            testBuilder.purgeChannels();
        }
    }

    /**
     * Receives Elektra Web Service request.
     * @return
     */
    public ReceiveMessageActionDefinition receiveElektraServiceRequest(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(elektraWebServer)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving Elektra Web Service request %s", payloadTemplate));
    }

    /**
     * Sends Elektra Web Service response.
     * @return
     */
    public SendMessageActionDefinition sendElektraServiceResponse(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(elektraWebServer)
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Sending Elektra Web Service response %s", payloadTemplate));
    }
}

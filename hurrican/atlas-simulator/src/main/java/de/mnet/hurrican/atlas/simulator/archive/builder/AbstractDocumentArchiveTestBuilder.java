/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.14
 */
package de.mnet.hurrican.atlas.simulator.archive.builder;

import java.io.*;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.consol.citrus.dsl.definition.SendMessageActionDefinition;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.jms.message.JmsMessageHeaders;
import com.google.common.io.Resources;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.mnet.hurrican.atlas.simulator.AbstractSimulatorTestBuilder;
import de.mnet.hurrican.atlas.simulator.SimulatorMessageHeaders;
import de.mnet.hurrican.atlas.simulator.SimulatorVariableNames;
import de.mnet.hurrican.atlas.simulator.archive.DocumentArchiveOperation;
import de.mnet.hurrican.atlas.simulator.archive.DocumentArchiveVariableNames;
import de.mnet.hurrican.atlas.simulator.archive.DocumentArchiveXpathExpressions;

/**
 * Basic DocumentArchive simulator test builder able to receive incoming order request such as archiveDocument. Based
 * on use case implementation sends response back to calling client.
 *
 *
 */
public abstract class AbstractDocumentArchiveTestBuilder extends AbstractSimulatorTestBuilder {

    @Autowired
    @Qualifier("simInboundEndpoint")
    private Endpoint simInboundEndpoint;

    /**
     * Receive archive document request.
     * @return
     */
    protected ReceiveMessageActionDefinition receiveArchiveDocument() {
        return (ReceiveMessageActionDefinition) receive(simInboundEndpoint)
                .selector(SimulatorMessageHeaders.ORDER_ID + " = '" + getOrderId() + "'")
                .header(SOAP_ACTION, DocumentArchiveOperation.ARCHIVE_DOCUMENT.getSoapAction())
                .extractFromHeader(SimulatorMessageHeaders.ORDER_ID.getName(), SimulatorVariableNames.ORDER_ID.getName())
                .description("Wait for archiveDocument request ...");

    }

    /**
     * Receive get document request.
     * @return
     */
    protected ReceiveMessageActionDefinition receiveGetDocument() {
        return (ReceiveMessageActionDefinition) receive(simInboundEndpoint)
                .selector(SimulatorMessageHeaders.ORDER_ID + " = '" + getOrderId() + "'")
                .header(SOAP_ACTION, DocumentArchiveOperation.GET_DOCUMENT.getSoapAction())
                .extractFromPayload(DocumentArchiveXpathExpressions.DOCUMENT_ID.getXpath(), DocumentArchiveVariableNames.DOCUMENT_ID.getName())
                .extractFromHeader(SimulatorMessageHeaders.ORDER_ID.getName(), SimulatorVariableNames.ORDER_ID.getName())
                .extractFromHeader(JmsMessageHeaders.CORRELATION_ID, SimulatorVariableNames.CORRELATION_ID.getName())
                .description("Wait for getDocument request ...");

    }

    /**
     * Send archive service response.
     * @return
     */
    protected SendMessageActionDefinition sendResponse(String messageTemplateName) {
        return (SendMessageActionDefinition) send(simInboundEndpoint)
                .payload(getXmlTemplate(messageTemplateName))
                .header(JmsMessageHeaders.CORRELATION_ID, String.format("${%s}",SimulatorVariableNames.CORRELATION_ID.getName()))
                .description(String.format("Sending response %s ...", messageTemplateName));

    }

    @Override
    protected String getInterfaceName() {
        return "archive";
    }

    protected void configureGetDocument(String fileName, String fileExtension, String documentType) {
        receiveGetDocument();

        byte[] fileData;
        try {
            fileData = Resources.toByteArray(Resources.getResource("anlagen/" + fileName));
        }
        catch (IOException e) {
            fileData = new byte[0];
        }

        variable(DocumentArchiveVariableNames.DOCUMENT_TYPE.getName(), documentType);
        variable(DocumentArchiveVariableNames.FILE_NAME.getName(), fileName);
        variable(DocumentArchiveVariableNames.FILE_LENGTH.getName(), fileData.length);
        variable(DocumentArchiveVariableNames.FILE_DATA.getName(), Base64.encodeBase64String(fileData));
        variable(DocumentArchiveVariableNames.FILE_EXTENSION.getName(), fileExtension);

        sendResponse("getDocumentResponse");
    }

}

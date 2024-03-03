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
import com.consol.citrus.ws.server.WebServiceServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * ResourceInventory role, for simulating the sending of ResourceInventory SOAP requests to Hurrican
 */
public class ResourceInventoryTestRole extends AbstractTestRole {

    public static final String SOAP_ACTION = "citrus_soap_action";

    @Autowired
    @Qualifier("resourceInventoryServer")
    private WebServiceServer resourceInventoryWebServer;

    @Autowired
    @Qualifier("resourceInventoryClient")
    private WebServiceClient resourceInventoryWebClient;

    @Autowired
    @Qualifier("commandStiftDataClient")
    private WebServiceClient commandStiftDataWebClient;

    @Autowired
    @Qualifier("commandCustomerDataClient")
    private WebServiceClient commandCustomerDataWebClient;

    @Autowired
    @Qualifier("commandLocationDataClient")
    private WebServiceClient commandLocationDataWebClient;

    @Autowired
    @Qualifier("resourceCharacteristicsClient")
    private WebServiceClient resourceCharacteristicsClient;

    @Autowired
    @Qualifier("portierungServiceClient")
    private WebServiceClient portierungServiceClient;

    @Autowired
    @Qualifier("resourceOrderManagementServiceClient")
    private WebServiceClient resourceOrderManagementServiceClient;

    /**
     * Sends Resource Web Service request.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveUpdateResourceRequest(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(resourceInventoryWebServer)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                //.header(SOAP_ACTION, "/ResourceInventoryService/updateResource") //TODO verify with Fachbereich
                .description(String.format("Receive updateResource Web Service request %s", payloadTemplate));
    }

    /**
     * Receives Resource Web Service response.
     *
     * @return
     */
    public SendMessageActionDefinition sendUpdateResourceResponse(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(resourceInventoryWebServer)
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Send updateResource Web Service response %s", payloadTemplate));
    }

    /**
     * Sends Resource Web Service request.
     *
     * @return
     */
    public SendMessageActionDefinition sendUpdateResourceRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(resourceInventoryWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/ResourceInventoryService/updateResource")
                .description(String.format("Sending updateResource Web Service request %s", payloadTemplate));
    }

    /**
     * Receives Resource Web Service response.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveUpdateResourceResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(resourceInventoryWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving updateResource Web Service response %s", payloadTemplate));
    }

    /**
     * Sends Command Stift Data Web Service request.
     *
     * @return
     */
    public SendMessageActionDefinition sendCommandStiftDataRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(commandStiftDataWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/setCommandStiftDataRequest")
                .description(String.format("Sending setCommandStiftData Web Service request %s", payloadTemplate));
    }

    /**
     * Receives Command Stift Data Web Service response.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveCommandStiftDataResponse(String payloadTemplate) {
        return receiveCommandStiftDataResponse(payloadTemplate, true);
    }

    /**
     * Receives Command Stift Data Web Service response.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveCommandStiftDataResponse(String payloadTemplate, boolean schemaValidation) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(commandStiftDataWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .schemaValidation(schemaValidation)
                .description(String.format("Receiving CommandStiftData Web Service response %s", payloadTemplate));
    }

    /**
     * Sends Command Customer Data Web Service request.
     *
     * @return
     */
    public SendMessageActionDefinition sendCommandCustomerDataRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(commandCustomerDataWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/setCommandCustomerDataRequest")
                .description(String.format("Sending setCommandCustomerData Web Service request %s", payloadTemplate));
    }

    /**
     * Receives Command Customer Data Web Service response.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveCommandCustomerDataResponse(String payloadTemplate) {
        return receiveCommandCustomerDataResponse(payloadTemplate, true);
    }

    /**
     * Receives Command Customer Data Web Service response.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveCommandCustomerDataResponse(String payloadTemplate, boolean schemaValidation) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(commandCustomerDataWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .schemaValidation(schemaValidation)
                .description(String.format("Receiving CommandCustomerData Web Service response %s", payloadTemplate));
    }

    /**
     * Sends Command Location Data Web Service request.
     *
     * @return
     */
    public SendMessageActionDefinition sendCommandLocationDataRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(commandLocationDataWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/setCommandLocationDataRequest")
                .description(String.format("Sending setCommandLocationData Web Service request %s", payloadTemplate));
    }

    /**
     * Receives Command Location Data Web Service response.
     *
     * @return
     */
    public ReceiveMessageActionDefinition receiveCommandLocationDataResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(commandLocationDataWebClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .description(String.format("Receiving CommandLocationData Web Service response %s", payloadTemplate));
    }

    public SendMessageActionDefinition sendResourceCharacteristicsRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(resourceCharacteristicsClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/ResourceCharacteristicsService/updateResourceCharacteristics")
                .description(String.format("Sending updateResourceCharacteristics Web Service request %s", payloadTemplate));
    }

    /**
     * Portierung Service
     */
    public SendMessageActionDefinition sendMigratePortierungskennungRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(portierungServiceClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/migratePortierungskennung")
                .description(String.format("Sending migratePortierungskennung Web Service request %s", payloadTemplate));
    }

    /**
     * Portierung Service
     */
    public ReceiveMessageActionDefinition receiveMigratePortierungskennungResponse(String payloadTemplate) {
        return (ReceiveMessageActionDefinition) testBuilder.receive(portierungServiceClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .schemaValidation(false)
                .description(String.format("Receiving migratePortierungskennung Web Service response %s", payloadTemplate));
    }

    /**
     * Sends reservePort ResourceOrderManagementService request.
     */
    public SendMessageActionDefinition sendReservePortResourceOrderManagementServiceRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(resourceOrderManagementServiceClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/ResourceOrderManagementService/reservePort")
                .description(String.format("Sending reservePort Web Service request %s", payloadTemplate));
    }

    /**
     * Sends releasePort ResourceOrderManagementService request.
     */
    public SendMessageActionDefinition sendReleasePortResourceOrderManagementServiceRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(resourceOrderManagementServiceClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/ResourceOrderManagementService/releasePort")
                .description(String.format("Sending releasePort Web Service request %s", payloadTemplate));
    }

    /**
     * Sends releasePort ResourceOrderManagementService request.
     */
    public SendMessageActionDefinition sendModifyPortReservationDateResourceOrderManagementServiceRequest(String payloadTemplate) {
        return (SendMessageActionDefinition) testBuilder.send(resourceOrderManagementServiceClient)
                .soap()
                .payload(getXmlTemplate(payloadTemplate))
                .header(SOAP_ACTION, "/ResourceOrderManagementService/modifyPortReservationDate")
                .description(String.format("Sending modifyPortReservationDate Web Service request %s", payloadTemplate));
    }
}

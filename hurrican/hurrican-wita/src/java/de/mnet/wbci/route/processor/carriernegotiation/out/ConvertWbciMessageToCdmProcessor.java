/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.13
 */
package de.mnet.wbci.route.processor.carriernegotiation.out;

import java.io.*;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wbci.marshal.MessageMarshallerDelegate;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.route.WbciCamelConstants;
import de.mnet.wbci.service.WbciSchemaValidationService;

/**
 * Apache Camel Processor, um einen WBCI Request im Hurrican-Format in das Atlas CDM Format zu konvertieren. <br> Der
 * Processor verwendet dazu den {@link MessageMarshallerDelegate}.
 */
@Component
public class ConvertWbciMessageToCdmProcessor extends HurricanOutProcessor implements WbciCamelConstants {

    private static final Logger LOG = Logger.getLogger(ConvertWbciMessageToCdmProcessor.class);

    @Autowired
    private MessageMarshallerDelegate messageMarshaller;

    @Autowired
    private WbciSchemaValidationService schemaValidationService;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Value("${atlas.carriernegotiationservice.requestCarrierChange}")
    private String soapActionRequestCarrierChange;

    @Value("${atlas.carriernegotiationservice.updateCarrierChange}")
    private String soapActionUpdateCarrierChange;

    @Value("${atlas.carriernegotiationservice.rescheduleCarrierChange}")
    private String soapActionRescheduleCarrierChange;

    @Value("${atlas.carriernegotiationservice.cancelCarrierChange}")
    private String soapActionCancelCarrierChange;

    @Override
    public void process(Exchange exchange) throws Exception {
        WbciMessage wbciMessage = getOriginalMessage(exchange);

        LOG.debug(String.format("Transforming wbci message to cdm format: %s", wbciMessage));

        WbciCdmVersion wbciCdmVersion = (WbciCdmVersion) exchange.getIn().getHeader(AtlasEsbConstants.CDM_VERSION_KEY);
        // remove header in order to not propagate to outside world
        exchange.getIn().removeHeader(AtlasEsbConstants.CDM_VERSION_KEY);

        // lets setup the out message before we invoke the dataFormat
        // so that it can mutate it if necessary
        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());

        SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
        messageMarshaller.marshal(wbciMessage, soapMessage.getSoapBody().getPayloadResult(), wbciCdmVersion);

        // validates message payload with schema definition
        schemaValidationService.validatePayload(soapMessage.getSoapBody().getPayloadSource());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soapMessage.writeTo(bos);

        String soapXml = bos.toString(System.getProperty("file.encoding", "UTF-8"));
        out.setBody(XmlPrettyFormatter.prettyFormat(soapXml));

        out.setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, getSoapAction(wbciMessage));
    }

    /**
     * Gets proper SOAP action value according to wbci message nature.
     *
     * @param wbciMessage
     */
    private String getSoapAction(WbciMessage wbciMessage) {
        if (wbciMessage instanceof VorabstimmungsAnfrage) {
            return soapActionRequestCarrierChange;
        }
        else if (wbciMessage instanceof TerminverschiebungsAnfrage) {
            return soapActionRescheduleCarrierChange;
        }
        else if (wbciMessage instanceof Meldung) {
            return soapActionUpdateCarrierChange;
        }
        else if (wbciMessage instanceof StornoAnfrage) {
            return soapActionCancelCarrierChange;
        }
        else {
            throw new MessageProcessingException("Failed to evaluate SOAP action for wbci message: " + wbciMessage);
        }
    }

    public void setSoapActionRequestCarrierChange(String soapActionRequestCarrierChange) {
        this.soapActionRequestCarrierChange = soapActionRequestCarrierChange;
    }

    public void setSoapActionUpdateCarrierChange(String soapActionUpdateCarrierChange) {
        this.soapActionUpdateCarrierChange = soapActionUpdateCarrierChange;
    }

    public void setSoapActionRescheduleCarrierChange(String soapActionRescheduleCarrierChange) {
        this.soapActionRescheduleCarrierChange = soapActionRescheduleCarrierChange;
    }

    public void setSoapActionCancelCarrierChange(String soapActionCancelCarrierChange) {
        this.soapActionCancelCarrierChange = soapActionCancelCarrierChange;
    }
}

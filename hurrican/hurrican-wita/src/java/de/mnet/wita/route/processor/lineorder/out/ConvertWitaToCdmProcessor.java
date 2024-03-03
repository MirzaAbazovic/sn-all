/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2014
 */
package de.mnet.wita.route.processor.lineorder.out;

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

import de.mnet.common.route.HurricanOutProcessor;
import de.mnet.common.tools.XmlPrettyFormatter;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.exceptions.WitaMessageProcessingException;
import de.mnet.wita.marshal.MessageMarshallerDelegate;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.route.WitaCamelConstants;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaSchemaValidationService;

/**
 *
 */
@Component
public class ConvertWitaToCdmProcessor extends HurricanOutProcessor implements WitaCamelConstants {

    private static final Logger LOG = Logger.getLogger(ConvertWitaToCdmProcessor.class);

    @Autowired
    private WitaConfigService witaConfigService;

    @Autowired
    private MessageMarshallerDelegate messageMarshaller;

    @Autowired
    private WitaSchemaValidationService schemaValidationService;

    @Autowired
    @Qualifier("atlasEsbSoapMessageFactory")
    private SoapMessageFactory soapMessageFactory;

    @Value("${atlas.lineorderservice.createOrder}")
    private String soapActionCreateOrder;

    @Value("${atlas.lineorderservice.updateOrder}")
    private String soapActionUpdateOrder;

    @Value("${atlas.lineorderservice.rescheduleOrder}")
    private String soapActionRescheduleOrder;

    @Value("${atlas.lineorderservice.cancelOrder}")
    private String soapActionCancelOrder;

    @Override
    public void process(Exchange exchange) throws Exception {
        WitaMessage witaMessage = getOriginalMessage(exchange);

        LOG.debug(String.format("Transforming wita message to cdm format: %s", witaMessage));

        WitaCdmVersion witaCdmVersion = witaConfigService.getDefaultWitaVersion();

        // lets setup the out message before we invoke the dataFormat
        // so that it can mutate it if necessary
        Message out = exchange.getOut();
        out.copyFrom(exchange.getIn());

        SoapMessage soapMessage = soapMessageFactory.createWebServiceMessage();
        messageMarshaller.marshal(witaMessage, soapMessage.getSoapBody().getPayloadResult(), witaCdmVersion);

        // validates message payload with schema definition
        schemaValidationService.validatePayload(soapMessage.getSoapBody().getPayloadSource());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soapMessage.writeTo(bos);

        String soapXml = bos.toString(System.getProperty("file.encoding", "UTF-8"));
        out.setBody(XmlPrettyFormatter.prettyFormat(soapXml));

        out.setHeader(AtlasEsbConstants.SOAPACTION_TIBCO, getSoapAction(witaMessage));
    }

    /**
     * Gets proper SOAP action value according to wita message nature.
     *
     * @param witaMessage
     */
    private String getSoapAction(WitaMessage witaMessage) {
        if (witaMessage instanceof Auftrag) {
            return soapActionCreateOrder;
        }
        else if (witaMessage instanceof TerminVerschiebung) {
            return soapActionRescheduleOrder;
        }
        else if (witaMessage instanceof Meldung) {
            return soapActionUpdateOrder;
        }
        else if (witaMessage instanceof Storno) {
            return soapActionCancelOrder;
        }
        else {
            throw new WitaMessageProcessingException("Failed to evaluate SOAP action for WITA message: " + witaMessage);
        }
    }

    public void setSoapActionCreateOrder(String soapActionCreateOrder) {
        this.soapActionCreateOrder = soapActionCreateOrder;
    }

    public void setSoapActionUpdateOrder(String soapActionUpdateOrder) {
        this.soapActionUpdateOrder = soapActionUpdateOrder;
    }

    public void setSoapActionRescheduleOrder(String soapActionRescheduleOrder) {
        this.soapActionRescheduleOrder = soapActionRescheduleOrder;
    }

    public void setSoapActionCancelOrder(String soapActionCancelOrder) {
        this.soapActionCancelOrder = soapActionCancelOrder;
    }

}

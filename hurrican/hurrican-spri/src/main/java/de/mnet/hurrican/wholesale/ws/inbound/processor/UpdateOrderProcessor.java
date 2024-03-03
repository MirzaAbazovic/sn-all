/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 17.02.2017

 */

package de.mnet.hurrican.wholesale.ws.inbound.processor;


import java.time.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.AuftragstypType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypABMType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypAKMPVType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MeldungstypQEBType;
import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.message.MessageType;
import de.mnet.hurrican.wholesale.interceptor.WholesaleResponseInterceptor;
import de.mnet.hurrican.wholesale.model.PvStatus;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.hurrican.wholesale.service.WholesaleAuditService;
import de.mnet.hurrican.wholesale.ws.MessageTypeSpri;
import de.mnet.hurrican.wholesale.ws.outbound.WholesaleOrderOutboundService;

/**
 * Processor for incoming messages.
 * Created by morozovse on 17.02.2017.
 */
@Component
public class UpdateOrderProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateOrderProcessor.class);
    private static final String REQUEST_EMPTY = "Request with EMPTY parameters received";
    private static final String SYSTEM = "SYSTEM";

    @Autowired
    private WholesaleAuditService wholesaleAuditService;
    @Autowired
    private WholesaleOrderOutboundService wholesaleOrderOutboundService;

    /**
     * Processes request for AKM-PV Meldungstype.
     *
     * @param auftragsType Type of the message
     * @param messageType  Message payload
     * @param message      raw message data for logging
     */
    public void process(AuftragstypType auftragsType, MessageType messageType, Message message) {

        if (messageType.getAKMPV() != null) {
            processAKMPV(auftragsType, messageType.getAKMPV(), message);
        }
        else if (messageType.getQEB() != null) {
            processQEB(messageType.getQEB(), message);
        }
        else if (messageType.getABM() != null) {
            processABM(messageType.getABM(), message);
        }
        else {
            LOG.error("Unerwarteter messageType im WholesaleOrderService: ");
        }
    }

    private void processAKMPV(AuftragstypType auftragsType, MeldungstypAKMPVType akmpv, Message message) {
        Validate.notNull(akmpv.getMeldungsattribute());
        String vorabstimmungId = checkVorabstimmungId(akmpv.getMeldungsattribute().getVorabstimmungId());
        String request_xml = (String) message.get(WholesaleResponseInterceptor.REQUEST_XML);
        createAndStoreWholesaleAudit(vorabstimmungId, request_xml, MessageTypeSpri.AKMPV.getLabel());

        wholesaleOrderOutboundService.sendWholesaleUpdateOrderRUEMPV(auftragsType, akmpv);
    }

    private void processQEB(MeldungstypQEBType qeb, Message message) {
        Validate.notNull(qeb.getMeldungsattribute());
        String vorabstimmungId = checkVorabstimmungId(qeb.getMeldungsattribute().getVorabstimmungId());
        String request_xml = (String) message.get(WholesaleResponseInterceptor.REQUEST_XML);
        createAndStoreWholesaleAudit(vorabstimmungId, request_xml, MessageTypeSpri.QEB.getLabel());
    }

    private void processABM(MeldungstypABMType abm, Message message) {
        Validate.notNull(abm.getMeldungsattribute());
        String vorabstimmungId = checkVorabstimmungId(abm.getMeldungsattribute().getVorabstimmungId());
        String request_xml = (String) message.get(WholesaleResponseInterceptor.REQUEST_XML);
        createAndStoreWholesaleAudit(vorabstimmungId, request_xml, MessageTypeSpri.ABM.getLabel());
    }

    private String checkVorabstimmungId(String vorabstimmungId) {
        if (StringUtils.isBlank(vorabstimmungId)) {
            LOG.warn(REQUEST_EMPTY);
            throw new IllegalArgumentException(REQUEST_EMPTY);
        }
        return vorabstimmungId;
    }

    private void createAndStoreWholesaleAudit(String vorabstimmungId, String request_xml, String labelAkmpv) {
        WholesaleAudit wholesaleAudit = new WholesaleAudit();
        wholesaleAudit.setBearbeiter(SYSTEM);
        wholesaleAudit.setVorabstimmungsId(vorabstimmungId);
        wholesaleAudit.setDatum(createCurrentDateTime());
        wholesaleAudit.setStatus(PvStatus.EMPFANGEN);
        wholesaleAudit.setBeschreibung(labelAkmpv);
        wholesaleAudit.setRequestXml(request_xml);
        try {
            wholesaleAuditService.saveWholesaleAudit(wholesaleAudit);
        }
        catch (StoreException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    LocalDateTime createCurrentDateTime() {
        return LocalDateTime.now();
    }
}

/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2009 10:27:19
 */
package de.mnet.hurrican.webservice.taifun.order.workflow.endpoint;

import org.apache.log4j.Logger;
import org.springframework.ws.client.WebServiceFaultException;

import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.common.tools.ExceptionTools;
import de.mnet.hurrican.webservice.taifun.order.workflow.exceptions.CreateTechOrderException;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderType;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeAcknowledgement;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeAcknowledgementDocument;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeDocument;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeFault;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeFaultDocument;


/**
 * Endpoint-Implementierung fuer den UseCase 'createTechOrder'. <br> Dieser Endpoint/WebService wird von Taifun
 * aufgerufen, um zu einem Billing-Auftrag eine entsprechende technische Auftragsreferenz (Hurrican-Auftrag) anzulegen.
 * <br> <br> Als Acknowledge wird ein Objekt mit den urspruenglich uebergebenen Daten (Taifun Kunden- und
 * Auftragsnummer) sowie der Hurrican Auftrags-ID generiert und zurueck gegeben.
 *
 *
 */
public class CreateTechOrderRequestEndpoint extends AbstractOrderWorkflowEndpoint {

    private static final Logger LOGGER = Logger.getLogger(CreateTechOrderRequestEndpoint.class);

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        LOGGER.debug("CreateTechOrderRequestEndpoint invoked");
        CreateTechOrderTypeDocument ctoTypeDoc = (CreateTechOrderTypeDocument) requestObject;
        CreateTechOrderType ctoType = ctoTypeDoc.getCreateTechOrderType();

        // Service-Aufruf um technischen Auftrag anzulegen
        try {
            CCAuftragService cas = getCCService(CCAuftragService.class);
            Auftrag auftrag = cas.createTechAuftrag(ctoType.getCustomerNo(), ctoType.getOrderNo(), getSessionId());

            if (auftrag != null) {
                // Auftrag wurde angelegt, OK-Meldung zurueck
                CreateTechOrderTypeAcknowledgementDocument ctoTypeAckDoc =
                        CreateTechOrderTypeAcknowledgementDocument.Factory.newInstance();
                CreateTechOrderTypeAcknowledgement ctoTypeAck = ctoTypeAckDoc.addNewCreateTechOrderTypeAcknowledgement();
                ctoTypeAck.setOrderNo(ctoType.getOrderNo());
                ctoTypeAck.setCustomerNo(ctoType.getCustomerNo());
                ctoTypeAck.setHurricanServiceNo(auftrag.getId());
                return ctoTypeAck;
            }
            else {
                throw new WebServiceFaultException("Auftrag wurde aus unbekanntem Grund nicht angelegt.");
            }
        }
        catch (Exception e) {
            // Fehlermeldung
            CreateTechOrderTypeFaultDocument ctoTypeFauDoc = CreateTechOrderTypeFaultDocument.Factory.newInstance();
            CreateTechOrderTypeFault ctoTypeFault = ctoTypeFauDoc.addNewCreateTechOrderTypeFault();
            ctoTypeFault.setOrderNo(ctoType.getOrderNo());
            ctoTypeFault.setCustomerNo(ctoType.getCustomerNo());
            ctoTypeFault.setErrorMessage(ExceptionTools.showDistinctMessageList(e));

            throw new CreateTechOrderException(e.getMessage(), e, ctoTypeFauDoc);
        }

    }

}



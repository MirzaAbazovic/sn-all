/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.mnet.hurrican.webservice.sourceagent.endpoint;

import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import com.evolving.wsdl.sa.v1.types.ServiceRequestAcknowledgement;
import com.evolving.wsdl.sa.v1.types.ServiceRequestAcknowledgementDocument;
import com.evolving.wsdl.sa.v1.types.ServiceRequestFault;
import com.evolving.wsdl.sa.v1.types.ServiceRequestFaultDocument;
import com.evolving.wsdl.sa.v1.types.impl.ServiceRequestDocumentImpl;

import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.hurrican.webservice.base.MnetAbstractMarshallingPayloadEndpoint;

/**
 * CPS Dummy-Implementierung.
 *
 *
 */
public class ServiceRequestMarshallingEndpoint extends MnetAbstractMarshallingPayloadEndpoint implements SourceAgentEndpoint {

    /**
     * @see org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint#invokeInternal(java.lang.Object)
     */
    @Override
    protected Object invokeInternal(Object requestObject) throws Exception {
        ServiceRequestDocumentImpl serviceDocumentImpl = (ServiceRequestDocumentImpl) requestObject;
        ServiceRequest serviceRequest = serviceDocumentImpl.getServiceRequest();
        String transactionId = serviceRequest.getTransactionId();
        Object object;
        String soData = serviceRequest.getSOData();

        ReferenceService refs = getCCService(ReferenceService.class);
        Reference reqTypeRef = refs.findReference(Reference.REF_TYPE_CPS_SERVICE_ORDER_TYPE, serviceRequest.getServiceOrderType());

        if (null == reqTypeRef) {
            ServiceRequestFaultDocument serviceRequestFaultDocument = ServiceRequestFaultDocument.Factory.newInstance();
            ServiceRequestFault serviceRequestFault = serviceRequestFaultDocument.addNewServiceRequestFault();
            serviceRequestFault.setTransactionId(transactionId);
            serviceRequestFault.setCode("CODE");
            serviceRequestFault.setDescription(soData);
            object = serviceRequestFaultDocument;
        }
        else {
            ServiceRequestAcknowledgementDocument serviceRequestAcknowledgementDocument = ServiceRequestAcknowledgementDocument.Factory.newInstance();
            ServiceRequestAcknowledgement serviceAcknowledgement = serviceRequestAcknowledgementDocument.addNewServiceRequestAcknowledgement();
            serviceAcknowledgement.setTransactionId(transactionId);
            object = serviceRequestAcknowledgementDocument;
        }
        return object;
    }
}

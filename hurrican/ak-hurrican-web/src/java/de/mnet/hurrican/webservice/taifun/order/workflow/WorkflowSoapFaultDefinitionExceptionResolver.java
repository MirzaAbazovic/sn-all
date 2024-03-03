/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.08.2009 11:43:44
 */
package de.mnet.hurrican.webservice.taifun.order.workflow;

import javax.xml.transform.*;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.xml.transform.StringSource;

import de.mnet.hurrican.webservice.taifun.order.workflow.exceptions.CreateTechOrderException;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeFault;
import de.mnet.hurricanweb.order.workflow.types.CreateTechOrderTypeFaultDocument;


/**
 * Exception Resolver um einen Fault zu generieren.
 *
 *
 */
public class WorkflowSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        CreateTechOrderException msg = null;
        if (ex instanceof CreateTechOrderException) {
            msg = (CreateTechOrderException) ex;
        }
        else {
            msg = createFaultMessage(ex);
        }
        addServiceFaultDetail(msg, fault);
    }

    private void addServiceFaultDetail(CreateTechOrderException msg, SoapFault fault)
            throws TransformerFactoryConfigurationError {
        Transformer trn;
        try {
            trn = TransformerFactory.newInstance().newTransformer();
            SoapFaultDetail faultDetail = fault.addFaultDetail();
            Result result = faultDetail.getResult();
            CreateTechOrderTypeFaultDocument doc = msg.getFaultMessage();
            if (doc == null) {
                logger.error("ServiceFaultException thrown with no serviceFaultDocument!", msg);
            }
            else {
                trn.transform(new StringSource(doc.toString()), result);
            }
        }
        catch (TransformerException e) {
            logger.error("problem with XML transform: ", e);
        }
    }

    private CreateTechOrderException createFaultMessage(Exception e) {
        CreateTechOrderTypeFaultDocument faultDocument = CreateTechOrderTypeFaultDocument.Factory.newInstance();
        CreateTechOrderTypeFault fault = faultDocument.addNewCreateTechOrderTypeFault();
        fault.setCustomerNo(0);
        fault.setOrderNo(0);
        fault.setErrorCode(null);
        fault.setErrorMessage("Unbekannter Fehler");
        CreateTechOrderException faultMsg = new CreateTechOrderException(e.getMessage(), e, faultDocument);
        return faultMsg;
    }


}



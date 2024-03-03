/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.06.2012 08:16:34
 */
package de.mnet.hurrican.e2e.sourceagent;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.annotation.*;
import com.evolving.wsdl.sa.v1.types.ServiceResponse.ServiceResponse2;
import com.evolving.wsdl.sa.v1.types.ServiceResponseAcknowledgement;
import com.evolving.wsdl.sa.v1.types.ServiceResponseAcknowledgementDocument;
import com.evolving.wsdl.sa.v1.types.ServiceResponseDocument;
import com.evolving.wsdl.sa.v1.types.ServiceResponseFault;
import com.evolving.wsdl.sa.v1.types.ServiceResponseFaultDocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.apache.xmlbeans.XmlObject;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceResponseSOData;
import de.augustakom.hurrican.service.cc.CPSService;
import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;

/**
 * E2E-Test des CPS-Endpoints
 * <p/>
 * TODO test fuer (TX_SOURCE_HURRICAN_MDU, SERVICE_ORDER_TYPE_INIT_MDU) und TX_SOURCE_HURRICAN_VERLAUF
 */
@Test(groups = E2E)
public class ServiceResponseTest extends BaseHurricanE2ETest {

    @Resource(name = "cpsWebServiceTemplate")
    private WebServiceTemplate cpsWebServiceTemplate;

    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;

    public void serviceResponseWithUnknownTransactionId() throws Exception {
        final String transactionId = String.valueOf(Long.MAX_VALUE);

        ServiceResponseDocument serviceResponseDoc = ServiceResponseDocument.Factory.newInstance();
        serviceResponseDoc.addNewServiceResponse();
        serviceResponseDoc.getServiceResponse().addNewServiceResponse();
        ServiceResponse2 serviceResponse2 = serviceResponseDoc.getServiceResponse().getServiceResponse();

        CPSServiceResponseSOData cpsServiceResponseSOData = new CPSServiceResponseSOData();
        cpsServiceResponseSOData.setExecDate("2010-02-07 10:36:25");

        serviceResponse2.setSOResult(0);
        serviceResponse2.setTransactionId(transactionId);
        serviceResponse2.addNewSOResponseData();
        serviceResponse2.getSOResponseData()
                .set(XmlObject.Factory.parse(new XStream().toXML(cpsServiceResponseSOData)));

        Object responseObj = cpsWebServiceTemplate.marshalSendAndReceive(serviceResponseDoc);

        if (responseObj instanceof ServiceResponseFaultDocument) {
            ServiceResponseFaultDocument faultDocument = (ServiceResponseFaultDocument) responseObj;
            ServiceResponseFault fault = faultDocument.getServiceResponseFault();
            assertThat(fault.getTransactionId(), equalTo(transactionId));
        }
        else {
            fail();
        }
    }

    public void successfulServiceResponse() throws Exception {
        CPSTransaction cpsTransaction = new CPSTransaction();
        cpsTransaction.setTxState(CPSTransaction.TX_STATE_IN_PROVISIONING);
        cpsTransaction.setTxSource(CPSTransaction.TX_SOURCE_HURRICAN_ORDER);
        cpsTransaction.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        cpsTransaction.setEstimatedExecTime(new Date());
        cpsTransaction = cpsService.saveCPSTransaction(cpsTransaction, 0L);

        ServiceResponseDocument serviceResponseDoc = ServiceResponseDocument.Factory.newInstance();
        serviceResponseDoc.addNewServiceResponse();
        serviceResponseDoc.getServiceResponse().addNewServiceResponse();
        ServiceResponse2 serviceResponse2 = serviceResponseDoc.getServiceResponse().getServiceResponse();

        CPSServiceResponseSOData cpsServiceResponseSOData = new CPSServiceResponseSOData();
        cpsServiceResponseSOData.setExecDate("2010-02-07 10:36:25");

        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("__", "_");
        XStream xstream = new XStream(new DomDriver("UTF-8", replacer));
        xstream.autodetectAnnotations(true);

        serviceResponse2.setSOResult(0);
        serviceResponse2.setTransactionId(String.valueOf(cpsTransaction.getId()));
        serviceResponse2.addNewSOResponseData();
        serviceResponse2.getSOResponseData().newCursor().setTextValue(xstream.toXML(cpsServiceResponseSOData));

        Object responseObj = cpsWebServiceTemplate.marshalSendAndReceive(serviceResponseDoc);

        if (responseObj instanceof ServiceResponseAcknowledgementDocument) {
            ServiceResponseAcknowledgementDocument acknowledgementDoc = (ServiceResponseAcknowledgementDocument) responseObj;
            ServiceResponseAcknowledgement acknowledgement = acknowledgementDoc.getServiceResponseAcknowledgement();
            assertThat(acknowledgement.getTransactionId(),
                    equalTo(String.valueOf(cpsTransaction.getId())));
        }
        else {
            fail();
        }
    }
}



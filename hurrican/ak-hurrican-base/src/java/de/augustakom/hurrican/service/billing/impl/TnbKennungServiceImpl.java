/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.03.2016
 */
package de.augustakom.hurrican.service.billing.impl;

import ch.ergon.taifun.ws.messages.GetDialNumberInfoDocument;
import ch.ergon.taifun.ws.messages.GetDialNumberInfoRequestType;
import ch.ergon.taifun.ws.messages.GetDialNumberInfoResponseDocument;
import ch.ergon.taifun.ws.types.AuthenticationType;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.TnbKennungService;

/**
 * Das Billing-System ermittelt ueber einen Webservice-Aufrufdie Portierungskennung aus der Billing-Auftrags-Id
 */
public class TnbKennungServiceImpl extends DefaultBillingService implements TnbKennungService {
    private static final Logger LOGGER = Logger.getLogger(TnbKennungServiceImpl.class);

    /**
     * siehe https://jira.m-net.de/browse/TAI-5971 und https://jira.m-net.de/browse/HUR-26897 <p/>
     * Beschreibung aus TAI-5971: <br/>
     * Ist das Flag auf 0 gesetzt, werden allfällige Orders in der Domain auf der DB committed und der Workflow ausgeführt. <br/>
     * Falls es auf 1 gesetzt ist, werden DB Änderungen zurückgerollt. <br/>
     * Das heisst, auf der Produktion sollte das Flag immer auf 0 gesetzt sein,
     * auf einem Testsystem ist es - wenn überhaupt - nur für Domain Orders relevant.
     */
    private static final int TEST_MODE_OFF = 0;

    @Override
    public String getTnbKennungFromTaifunWebservice(Long taifunAutragsNummer) throws FindException {
        String tbnKennung = null;
        try {
            WebServiceTemplate billingWSTemplate = configureAndGetBillingWSTemplate();
            GetDialNumberInfoDocument doc = GetDialNumberInfoDocument.Factory.newInstance();
            GetDialNumberInfoRequestType request = doc.addNewGetDialNumberInfo();
            AuthenticationType authType = configureAndGetAuthenticationType();
            request.setAuthentication(authType);
            int serviceNo = Math.toIntExact(taifunAutragsNummer);
            request.setServiceNoo(serviceNo);
            request.setTestMode(TEST_MODE_OFF);
            Object result = billingWSTemplate.marshalSendAndReceive(request);
            if (result instanceof GetDialNumberInfoResponseDocument) {
                GetDialNumberInfoResponseDocument response = (GetDialNumberInfoResponseDocument) result;
                tbnKennung = response.getGetDialNumberInfoResponse().getPortingIdentifier();
            }

            return tbnKennung;
        }
        catch (ServiceCommandException | SoapFaultClientException ex) {
            LOGGER.error(ex.getMessage(), ex);
            String msg = String.format("Error finding tbnKennung for order '%d]: %s", taifunAutragsNummer, ex.getMessage());
            throw new FindException(msg);
        }
    }
}

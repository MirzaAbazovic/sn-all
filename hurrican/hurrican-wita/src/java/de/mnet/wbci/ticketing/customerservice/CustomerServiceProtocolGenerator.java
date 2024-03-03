/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.13
 */
package de.mnet.wbci.ticketing.customerservice;

import java.util.*;
import javax.xml.datatype.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.builder.cdm.customer.v1.AddCommunicationBuilder;
import de.mnet.wbci.ticketing.customerservice.converter.CustomerServiceProtocolEnricher;

@Component
public class CustomerServiceProtocolGenerator {

    private static final Logger LOG = Logger.getLogger(CustomerServiceProtocolGenerator.class);

    @Autowired
    private List<CustomerServiceProtocolEnricher> protocolEnrichers;

    @Autowired
    private BillingAuftragService billingAuftragService;

    protected BAuftrag lookupBillingAuftrag(WbciMessage wbciMessage) throws FindException {
        Long taifunAuftragId = wbciMessage.getWbciGeschaeftsfall().getBillingOrderNoOrig();
        if (taifunAuftragId == null) {
            return null;
        }

        return billingAuftragService.findAuftrag(taifunAuftragId);
    }

    /**
     * Generates the Customer Service Protocol, when possible (i.e. when the message has a Taifun Auftrag associated
     * with it).
     *
     * @param wbciMessage the wbci message to generate the protocol from
     * @return the Customer Service Protocol or null, if the protocol could not be generated
     */
    public AddCommunication generateCustomerServiceProtocol(WbciMessage wbciMessage) {
        try {
            LOG.info(String.format("Generating the CustomerService Protocol for the WBCI Message: %s", wbciMessage));

            BAuftrag billingAuftrag = lookupBillingAuftrag(wbciMessage);
            if (billingAuftrag == null) {
                LOG.info("CustomerService Protocol could not be generated as there was no Taifun Auftrag associated with the WBCI Message");
                return null;
            }

            AddCommunication communication = new AddCommunicationBuilder()
                    .withContractId(getContractId(billingAuftrag))
                    .withCustomerId(getCustomerId(billingAuftrag))
                    .withType("WBCI")
                    .withReason("WBCI-Meldung")
                    .withTime(getCurrentTime())
                    .build();

            enrichProtocol(wbciMessage, communication);

            LOG.info(String.format("CustomerService Protocol successfully generated"));

            return communication;
        }
        catch (Exception e) {
            LOG.error(String.format("Error generating the CustomerService Protocol for the WBCI Message: %s", wbciMessage), e);
            return null;
        }
    }

    private String getCustomerId(BAuftrag billingAuftrag) {
        return String.valueOf(billingAuftrag.getKundeNo());
    }

    private String getContractId(BAuftrag billingAuftrag) {
        return String.valueOf(billingAuftrag.getAuftragNoOrig());
    }

    private void enrichProtocol(WbciMessage wbciMessage, AddCommunication csProtocol) {
        for (CustomerServiceProtocolEnricher protocolEnricher : protocolEnrichers) {
            if (protocolEnricher.supports(wbciMessage)) {
                protocolEnricher.enrich(wbciMessage, csProtocol);
                return;
            }
        }

        throw new WbciServiceException(String.format("Customer Service Protocol generation error - No enricher found for WBCI Message %s", wbciMessage));
    }

    private XMLGregorianCalendar getCurrentTime() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    }

}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.mnet.common.customer.service.CustomerService;
import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.service.WbciCustomerService;
import de.mnet.wbci.ticketing.customerservice.CustomerServiceProtocolGenerator;

@CcTxRequired
public class WbciCustomerServiceImpl implements WbciCustomerService {

    private static final Logger LOGGER = Logger.getLogger(WbciCustomerServiceImpl.class);

    @Autowired
    private CustomerServiceProtocolGenerator customerServiceProtocolGenerator;

    @Autowired
    private CamelProxyLookupService camelProxyLookupService;

    @Override
    public void sendCustomerServiceProtocol(WbciMessage wbciMessage) {
        try {
            CustomerService customerService = camelProxyLookupService.lookupCamelProxy(PROXY_CUSTOMER_SERVICE,
                    CustomerService.class);

            AddCommunication csProtocol = customerServiceProtocolGenerator.generateCustomerServiceProtocol(wbciMessage);
            if (csProtocol != null) {
                customerService.sendCustomerServiceProtocol(csProtocol);
            }
        }
        catch (Exception e) {
            // All BSI exceptions should be caught here but not propagated further. Communication errors with BSI
            // are not considered critical and should therefore not affect normal WBCI message processing.
            LOGGER.error("Exception caught sending customer service message", e);
        }
    }

}

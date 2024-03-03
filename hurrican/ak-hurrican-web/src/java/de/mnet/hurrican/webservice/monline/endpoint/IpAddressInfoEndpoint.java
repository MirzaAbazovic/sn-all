/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2011 11:22:38
 */
package de.mnet.hurrican.webservice.monline.endpoint;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.mnet.hurricanweb.netid.customer.CustomerOrderCombination;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpRequestDocument;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpResponseDocument;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpResponseDocument.GetCustomerByIpResponse;

/**
 * Dieser WebserviceEndpoint liefert eine Kombination aus Kundennummer und Auftragsnummer zu einer Net-Id. Die Net-Id
 * identifiziert IP-Adressbloecke. Das Ziel dieses Service ist es also zu einem bestimmten Zeitpunkt die Zuordnung von
 * IP-Adressbloecken zu Kundendaten darzustellen.
 *
 *
 * @since 15.09.2011
 */
@Endpoint
public class IpAddressInfoEndpoint {

    private static final Logger LOGGER = Logger.getLogger(IpAddressInfoEndpoint.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.IPAddressService")
    IPAddressService ipAddressService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    CCAuftragService auftragService;

    private CustomerOrderCombinationConverter converter = null;

    /**
     * fuehrt den Aufruf auf den Webservice aus.
     *
     * @param request
     * @return
     */
    @PayloadRoot(localPart = "GetCustomerByIpRequest", namespace = "http://mnet.de/hurricanweb/netid/customer/")
    @ResponsePayload
    public GetCustomerByIpResponseDocument getCustomerByIp(@RequestPayload GetCustomerByIpRequestDocument request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("REQUEST: " + request);
        }

        Long netId = request.getGetCustomerByIpRequest().getNetId();
        Calendar dateActive = request.getGetCustomerByIpRequest().getDate();

        Set<CustomerOrderCombinationWrapper> domainCombinations;
        try {
            domainCombinations = retrieveCombinations(netId, dateActive);
        }
        catch (FindException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return convertCombinationsAndProvideResponse(domainCombinations);
    }

    GetCustomerByIpResponseDocument convertCombinationsAndProvideResponse(
            Set<CustomerOrderCombinationWrapper> domainCombinations) {
        if ((domainCombinations != null) && (!domainCombinations.isEmpty())) {
            CustomerOrderCombination[] webserviceCombinations = getConverter().setSource(domainCombinations).convert();
            return createResponse(webserviceCombinations);
        }
        else {
            return createEmptyResponse();
        }
    }

    /**
     * @param customerOrderCombination
     * @return
     */
    GetCustomerByIpResponseDocument createResponse(CustomerOrderCombination... customerOrderCombination) {
        GetCustomerByIpResponseDocument responseDoc = GetCustomerByIpResponseDocument.Factory.newInstance();
        GetCustomerByIpResponse response = responseDoc.addNewGetCustomerByIpResponse();
        if ((customerOrderCombination != null) && (customerOrderCombination.length > 0)) {
            response.setCustomerOrderCombinationArray(customerOrderCombination);
        }
        responseDoc.setGetCustomerByIpResponse(response);
        return responseDoc;
    }

    private static final CustomerOrderCombination[] EMPTY_WEBSERVICE_COMBINATIONS = null;

    /**
     * @return
     */
    GetCustomerByIpResponseDocument createEmptyResponse() {
        return createResponse(EMPTY_WEBSERVICE_COMBINATIONS);
    }

    /**
     * ermittelt zu den angegebenen Parametern ein Menge an {@link CustomerOrderCombinationWrapper}s.
     *
     * @param netId
     * @param dateActive
     * @return
     * @throws FindException
     */
    Set<CustomerOrderCombinationWrapper> retrieveCombinations(Long netId, Calendar dateActive) throws FindException {
        final Set<CustomerOrderCombinationWrapper> combinations = new HashSet<>();
        final List<IPAddress> ipAddressList = getIpAddressService().findAssignedIPs4NetId(netId, dateActive.getTime());

        if (CollectionTools.isNotEmpty(ipAddressList)) {
            for (IPAddress ipAddress : ipAddressList) {
                final Long billingOrderNo = ipAddress.getBillingOrderNo();
                List<AuftragDaten> auftragDatenList = getAuftragService().findAuftragDaten4OrderNoOrig(billingOrderNo);
                if (CollectionTools.isNotEmpty(auftragDatenList)) {
                    for (AuftragDaten auftragDaten : auftragDatenList) {
                        Auftrag auftrag = getAuftragService().findAuftragById(auftragDaten.getAuftragId());
                        if (auftrag != null) {
                            CustomerOrderCombinationWrapper combo = CustomerOrderCombinationWrapper.create(
                                    auftrag.getKundeNo(), billingOrderNo);
                            combinations.add(combo);
                        }
                    }
                }
            }
        }
        return combinations;
    }

    IPAddressService getIpAddressService() {
        return ipAddressService;
    }

    CCAuftragService getAuftragService() {
        return auftragService;
    }

    CustomerOrderCombinationConverter getConverter() {
        if (converter == null) {
            converter = new CustomerOrderCombinationConverter();
        }
        return converter;
    }

    void setConverter(CustomerOrderCombinationConverter converter) {
        this.converter = converter;
    }

}


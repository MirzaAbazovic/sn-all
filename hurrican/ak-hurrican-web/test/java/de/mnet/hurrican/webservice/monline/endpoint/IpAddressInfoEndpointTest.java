/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.2011 09:39:43
 */
package de.mnet.hurrican.webservice.monline.endpoint;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.mnet.hurricanweb.netid.customer.CustomerOrderCombination;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpRequestDocument;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpRequestDocument.GetCustomerByIpRequest;
import de.mnet.hurricanweb.netid.customer.GetCustomerByIpResponseDocument;
import de.mnet.hurricanweb.netid.customer.impl.CustomerOrderCombinationImpl;

/**
 * Testklasse von {@link IpAddressInfoEndpoint}.
 *
 *
 * @since 16.09.2011
 */
@Test(groups = BaseTest.UNIT)
public class IpAddressInfoEndpointTest extends BaseTest {

    private IpAddressInfoEndpoint cut;

    private IPAddressService ipAddressServiceMock;
    private CCAuftragService auftragServiceMock;

    @BeforeMethod
    public void setUp() {
        ipAddressServiceMock = Mockito.mock(IPAddressService.class);
        auftragServiceMock = Mockito.mock(CCAuftragService.class);

        cut = new IpAddressInfoEndpoint();
        cut.ipAddressService = ipAddressServiceMock;
        cut.auftragService = auftragServiceMock;
    }

    /**
     * Testet die Methode {@link IpAddressInfoEndpoint#retrieveCombinations(Long, Calendar)}. Die Suche nach
     * Kombinationen liefert nichts (eine leere Liste) zurueck.
     */
    @Test
    public void retrieveCombinations_NothingFound() throws FindException {
        Mockito.when(ipAddressServiceMock.findAssignedIPs4NetId(Matchers.anyLong(), Matchers.any(Date.class)))
                .thenReturn(Collections.<IPAddress>emptyList());

        Set<CustomerOrderCombinationWrapper> result = cut.retrieveCombinations(2000L, Calendar.getInstance());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    /**
     * Testet die Methode {@link IpAddressInfoEndpoint#retrieveCombinations(Long, Calendar)}. Die Suche nach einer
     * Kombination liefert genau einen Treffer.
     */
    @Test
    public void retrieveCombinations_OneIpFoundWithOneAuftragDatenAndOneAuftrag() throws FindException {
        final Long billingOrderNo = 20000L;
        IPAddress ipAddress = new IPAddress();
        ipAddress.setBillingOrderNo(billingOrderNo);
        Mockito.when(ipAddressServiceMock.findAssignedIPs4NetId(Matchers.anyLong(), Matchers.any(Date.class)))
                .thenReturn(Arrays.asList(ipAddress));

        final long auftragId = 1020L;
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(auftragId);
        Mockito.when(auftragServiceMock.findAuftragDaten4OrderNoOrig(billingOrderNo)).thenReturn(
                Arrays.asList(auftragDaten));

        final Long kundeNo = 12345L;
        Auftrag auftrag = new Auftrag();
        auftrag.setKundeNo(kundeNo);
        Mockito.when(auftragServiceMock.findAuftragById(auftragId)).thenReturn(auftrag);

        Set<CustomerOrderCombinationWrapper> combinations = cut.retrieveCombinations(2000L, Calendar.getInstance());

        Assert.assertNotNull(combinations);
        Assert.assertEquals(combinations.size(), 1);
        CustomerOrderCombinationWrapper firstAndLastCombination = combinations.iterator().next();
        Assert.assertNotNull(firstAndLastCombination);
        Assert.assertEquals(firstAndLastCombination.getCustomerNo(), kundeNo);
        Assert.assertEquals(firstAndLastCombination.getBillingNo(), billingOrderNo);
    }

    /**
     * Testet die Methode {@link IpAddressInfoEndpoint#getCustomerByIp(GetCustomerByIpRequestDocument)}.
     */
    @Test
    public void getCustomerByIp_StraightForward() {
        // erstelle ein neues Webserviceanfragedokument.
        final long netId = 101010L;
        final Calendar dateActive = Calendar.getInstance();
        GetCustomerByIpRequestDocument requestDocument = createRequestDocument(netId, dateActive);

        // erstelle die Menge an Kombinationen, die vom Service geliefert werden.
        final CustomerOrderCombinationWrapper domainCombination = CustomerOrderCombinationWrapper.create(2000L, 1000L);
        final Set<CustomerOrderCombinationWrapper> domainCombinations = new HashSet<CustomerOrderCombinationWrapper>(
                Arrays.asList(domainCombination));

        // erstelle Konvertierungs-Mock und sag ihm, er solle fuer die Menge an Kombinationen aus dem Service eine
        // gleichwertige Menge an Kombinationen im Format fuer den Webservice liefern.
        final CustomerOrderCombination[] webserviceCombinations = new CustomerOrderCombinationImpl[domainCombinations
                .size()];
        webserviceCombinations[0] = CustomerOrderCombination.Factory.newInstance();
        webserviceCombinations[0].setCustomerNo(domainCombination.getCustomerNo());
        webserviceCombinations[0].setOrderNo(domainCombination.getBillingNo());
        final CustomerOrderCombinationConverter converterMock = Mockito.mock(CustomerOrderCombinationConverter.class);
        Mockito.when(converterMock.setSource(domainCombinations)).thenReturn(converterMock);
        Mockito.when(converterMock.convert()).thenReturn(webserviceCombinations);

        GetCustomerByIpResponseDocument responseDocument = cut.getCustomerByIp(requestDocument);
        Assert.assertNotNull(responseDocument);
    }

    /**
     * Testet die Methode {@link IpAddressInfoEndpoint#getCustomerByIp(GetCustomerByIpRequestDocument)}. Das Laden der
     * Daten wirft eine {@link FindException}, welche als {@link RuntimeException} weiter geworfen wird.
     *
     * @throws FindException
     */
    @Test(expectedExceptions = RuntimeException.class)
    public void getCustomerByIp_FindExceptionOnDataRetrieval() throws FindException {
        GetCustomerByIpRequestDocument requestDocument = createRequestDocument();

        IpAddressInfoEndpoint spy = spy(cut);
        doThrow(new FindException("Irgendwas ist schief gelaufen beim Laden der Daten!")).when(spy).retrieveCombinations(any(Long.class), any(Calendar.class));
        spy.getCustomerByIp(requestDocument);
    }

    /**
     * liefert eine neue Instanz einer Webserviceanfrage mit den angegebenen Parametern.
     *
     * @param netId
     * @param dateActive
     * @return
     */
    private GetCustomerByIpRequestDocument createRequestDocument(Long netId, Calendar dateActive) {
        GetCustomerByIpRequestDocument requestDocument = GetCustomerByIpRequestDocument.Factory.newInstance();
        GetCustomerByIpRequest request = requestDocument.addNewGetCustomerByIpRequest();
        request.setNetId(netId);
        request.setDate(dateActive);
        return requestDocument;
    }

    /**
     * liefert eine neue Instanz einer Webserviceanfrage mit Standardwerten. Fuer einige Tests sind die genauen
     * Paramenter egal.
     *
     * @return
     */
    private GetCustomerByIpRequestDocument createRequestDocument() {
        return createRequestDocument(1L, Calendar.getInstance());
    }

}


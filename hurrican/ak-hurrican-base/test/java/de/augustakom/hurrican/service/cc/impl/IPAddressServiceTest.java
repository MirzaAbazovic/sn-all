/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.09.2011 13:50:57
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.hurrican.model.cc.AddressTypeEnum.*;
import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.NonUniqueResultException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IPPoolType;
import de.augustakom.hurrican.model.cc.IPPurpose;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchQuery;
import de.augustakom.hurrican.model.cc.view.IPAddressSearchView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4Response;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4ResponseDocument;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6Response;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6ResponseDocument;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV4Response;
import de.mnet.monline.ipProviderService.ipp.ReleaseIPV4ResponseDocument;
import de.mnet.monline.ipProviderService.ipp.Success;

/**
 * Testklasse f&uuml;r {@link IPAddressServiceImpl}.
 */
@Test(groups = { BaseTest.SERVICE })
public class IPAddressServiceTest extends AbstractHurricanBaseServiceTest {

    @Resource(name = "monlineWSTemplate")
    private MnetWebServiceTemplate monlineWebServiceTemplate = null;
    private IPAddressService cut;

    @BeforeMethod
    public void setup() {
        cut = getCCService(IPAddressService.class);
    }

    @Test
    public void findV6PrefixAddressesByBillingOrderNo_Success() throws FindException {
        Long billingOrderNo = Long.valueOf(1234);
        Long billingOrderNoDifferent = Long.valueOf(1235);
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV4_prefix)
                .withBillingOrderNumber(billingOrderNo).build();
        // Dieses sollte ermittelt werden
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV4_prefix)
                .withBillingOrderNumber(billingOrderNoDifferent).build();
        List<IPAddress> prefixAddresses = cut.findV6PrefixesByBillingOrderNumber(billingOrderNo);
        assertNotNull(prefixAddresses);
        assertEquals(prefixAddresses.size(), 1);
        assertNull(prefixAddresses.get(0).getPrefixRef());
    }

    @Test
    public void findAssignedIPs4BillingOrder_Success() throws FindException {
        Long billingOrderNo = Long.valueOf(1234);
        Long billingOrderNoDifferent = Long.valueOf(1235);
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(1)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNoDifferent).withNetId(Long.valueOf(2))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(3)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(4))
                .withGueltigBis(new Date()).build();
        List<IPAddress> ipList = cut.findAssignedIPs4BillingOrder(billingOrderNo);
        assertNotNull(ipList);
        assertEquals(ipList.size(), 2);
    }

    @Test
    public void findAllIPAddressPanelViews_Success() throws FindException {
        Long billingOrderNo = Long.valueOf(1234);
        Long billingOrderNoDifferent = Long.valueOf(1235);
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(1)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNoDifferent).withNetId(Long.valueOf(2))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(3)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(4))
                .withGueltigBis(new Date()).build();
        List<IPAddressPanelView> ipList = cut.findAllIPAddressPanelViews(billingOrderNo);
        assertNotNull(ipList);
        assertEquals(ipList.size(), 3);
    }

    @Test
    public void findAssignedIPsOnly4BillingOrder_Success() throws FindException {
        Long billingOrderNo = Long.valueOf(1234);
        Long billingOrderNoDifferent = Long.valueOf(1235);
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withAddressType(AddressTypeEnum.IPV4)
                .withNetId(Long.valueOf(1)).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_full)
                .withBillingOrderNumber(billingOrderNoDifferent).withNetId(Long.valueOf(2)).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_full)
                .withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(3)).build();
        List<IPAddress> ipList = cut.findAssignedIPsOnly4BillingOrder(billingOrderNo);
        assertNotNull(ipList);
        assertEquals(ipList.size(), 2);
    }

    @Test
    public void findAssignedNetsOnly4BillingOrder_Success() throws FindException {
        Long billingOrderNo = Long.valueOf(1234);
        Long billingOrderNoDifferent = Long.valueOf(1235);
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo).withAddressType(AddressTypeEnum.IPV4)
                .withNetId(Long.valueOf(1)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo)
                .withAddressType(AddressTypeEnum.IPV4_prefix).withNetId(Long.valueOf(2)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNo)
                .withAddressType(AddressTypeEnum.IPV4_prefix).withNetId(null).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(billingOrderNoDifferent)
                .withAddressType(AddressTypeEnum.IPV4_prefix).withNetId(Long.valueOf(3)).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_full)
                .withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(4)).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(Long.valueOf(5)).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(null).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNoDifferent).withNetId(Long.valueOf(6)).build();
        List<IPAddress> ipList = cut.findAssignedNetsOnly4BillingOrder(billingOrderNo);
        assertNotNull(ipList);
        assertEquals(ipList.size(), 2);
    }

    public void saveIPAddress_Success() throws StoreException {
        IPAddress toSave = new IPAddress();
        toSave.setAddress("192.168.1.1");
        toSave.setIpType(AddressTypeEnum.IPV4);

        IPAddress saved = cut.saveIPAddress(toSave, -1L);

        assertNotNull(saved, "IP-Adresse konnte nicht gespeichert werden");
        assertNotNull(saved.getId(), "IP-Adresse konnte nicht gespeichert werden");
        assertNotNull(saved.getBinaryRepresentation(), "Binär-Darstellung der IP nicht definiert!");
    }

    @DataProvider
    public Object[][] saveRelativeIPv6AddressProvider() {
        return new Object[][] { { IPV6_relative }, { IPV6_relative_eui64 } };
    }

    /**
     * Bei relativen IPv6 Addressen soll die komplette Repraesentation am relativen Teil gespeichert werden. Es werden
     * nur die ersten 64 Bit und nicht die Interface ID gespeichert!
     */
    @Test(dataProvider = "saveRelativeIPv6AddressProvider")
    public void saveRelativeIPv6Address(AddressTypeEnum relativeAddressType) throws StoreException {
        IPAddress prefix = new IPAddress();
        prefix.setIpType(AddressTypeEnum.IPV6_prefix);
        prefix.setAddress("2001:db8:a001::/48");
        IPAddress savedPrefix = cut.saveIPAddress(prefix, -1L);

        IPAddress relative = new IPAddress();
        relative.setIpType(relativeAddressType);
        relative.setAddress("0:0:0:0:ffff:ffff:ffff:ffff/64");
        relative.setPrefixRef(savedPrefix);
        IPAddress saved = cut.saveIPAddress(relative, -1L);

        assertEquals(saved.getPrefixRef().getBinaryRepresentation(),
                "001000000000000100001101101110001010000000000001");
        assertEquals(saved.getBinaryRepresentation(),
                "0010000000000001000011011011100010100000000000010000000000000000");
    }

    @DataProvider(name = "assignIPV4_Success_DataProvider")
    public Object[][] assignIPV4_Success_DataProvider() {
        return new Object[][] {
                { "192.168.2.1", 0, AddressTypeEnum.IPV4 },
                { "192.168.2.1/16", 0, AddressTypeEnum.IPV4_with_prefixlength },
                { "192.168.2.1/16", 16, AddressTypeEnum.IPV4_prefix },
                { "192.168.2.1/31", 31, AddressTypeEnum.IPV4_prefix },
                { "192.168.2.1/32", 32, AddressTypeEnum.IPV4_with_prefixlength },
                { "192.168.2.1", 32, AddressTypeEnum.IPV4 },
        };
    }

    @Test(dataProvider = "assignIPV4_Success_DataProvider")
    public void assignIPV4_Success(String ip, int netmaskSize, AddressTypeEnum ipTypeExpected) throws StoreException,
            IOException, FindException {
        AssignIPV4ResponseDocument responseDoc = AssignIPV4ResponseDocument.Factory.newInstance();
        AssignIPV4Response response = responseDoc.addNewAssignIPV4Response();
        response.setSuccess(Success.YES);
        response.setNetAddress(ip);
        response.setNetmaskSize(netmaskSize);
        response.setNetId(1L);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference purpose = referenceService.findReference(IPPurpose.Kundennetz.getId());
        Reference site = referenceService.findReference(22400L);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withIpPool(IPPoolType.XDSL.getId());
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(produktBuilder);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        IPAddress assigned = cut.assignIPV4(auftragDaten.getAuftragId(), purpose, Integer.valueOf(0), site,
                Long.valueOf(-1));

        assertNotNull(assigned);
        assertTrue(NumberTools.equal(assigned.getNetId(), Long.valueOf(response.getNetId())),
                "Net-ID muss gleich sein!");
        assertTrue(NumberTools.equal(assigned.getBillingOrderNo(), auftragDaten.getAuftragNoOrig()),
                "Die Billing Auftragsnummer muss gleich sein!");
        assertTrue(assigned.getIpType() == ipTypeExpected, "Der Addresstyp muss IPV4 sein!");
        assertEquals(assigned.getPurpose(), purpose);
        assertEquals(assigned.getAddress(), response.getNetAddress());
    }

    @Test(expectedExceptions = { StoreException.class })
    /** In Response ist success == no */
    public void assignIPV4_Failed() throws StoreException, IOException, FindException {
        AssignIPV4ResponseDocument responseDoc = AssignIPV4ResponseDocument.Factory.newInstance();
        AssignIPV4Response response = responseDoc.addNewAssignIPV4Response();
        response.setSuccess(Success.NO);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference purpose = referenceService.findReference(IPPurpose.Kundennetz.getId());
        Reference site = referenceService.findReference(22400L);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withIpPool(IPPoolType.XDSL.getId());
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(produktBuilder);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        cut.assignIPV4(auftragDaten.getAuftragId(), purpose, Integer.valueOf(0), site, Long.valueOf(-1));
    }

    @Test(expectedExceptions = { StoreException.class })
    /** IPPoolType fehlt */
    public void assignIPV4_ExceptionExpected() throws StoreException, IOException, FindException {
        AssignIPV4ResponseDocument responseDoc = AssignIPV4ResponseDocument.Factory.newInstance();
        AssignIPV4Response response = responseDoc.addNewAssignIPV4Response();
        response.setSuccess(Success.YES);
        response.setNetmaskSize(0);
        response.setNetId(1L);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference purpose = referenceService.findReference(IPPurpose.Kundennetz.getId());
        Reference site = referenceService.findReference(22400L);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withIpPool(null);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(produktBuilder);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        cut.assignIPV4(auftragDaten.getAuftragId(), purpose, Integer.valueOf(0), site, Long.valueOf(-1));
    }

    @DataProvider(name = "assignIPV4_PurposeOfWrongTypeDataProvider")
    public Object[][] assignIPV4_PurposeOfWrongTypeDataProvider() {
        return new Object[][] {
                //@formatter:off
                { IPPurpose.DhcpV6Pd.getId() }
              //@formatter:on
        };
    }

    @Test(expectedExceptions = { StoreException.class }, dataProvider = "assignIPV4_PurposeOfWrongTypeDataProvider")
    public void assignIPV4_PurposeOfWrongType(Long ipPurposeRefId) throws StoreException, FindException {
        final ReferenceService referenceService = getCCService(ReferenceService.class);
        final Reference purpose = referenceService.findReference(ipPurposeRefId);
        final Reference site = referenceService.findReference(22400L);
        cut.assignIPV4(RandomTools.createLong(), purpose, RandomTools.createInteger(), site,
                Long.valueOf(-1));
    }

    @DataProvider(name = "assignIPV6_PurposeOfWrongTypeDataProvider")
    public Object[][] assignIPV6_PurposeOfWrongTypeDataProvider() {
        return new Object[][] {
                //@formatter:off
                { IPPurpose.Kundennetz.getId() },
                { IPPurpose.Transfernetz.getId() }
              //@formatter:on
        };
    }

    @Test(expectedExceptions = { StoreException.class }, dataProvider = "assignIPV6_PurposeOfWrongTypeDataProvider")
    public void assignIPV6_PurposeOfWrongType(Long ipPurposeRefId) throws StoreException, FindException {
        final ReferenceService referenceService = getCCService(ReferenceService.class);
        final Reference purpose = referenceService.findReference(ipPurposeRefId);
        final Reference site = referenceService.findReference(22400L);
        cut.assignIPV6(RandomTools.createLong(), purpose, RandomTools.createInteger(), site,
                Long.valueOf(-1));
    }

    @Test
    public void assignIPV6_Success() throws StoreException, IOException, FindException {
        AssignIPV6ResponseDocument responseDoc = AssignIPV6ResponseDocument.Factory.newInstance();
        AssignIPV6Response response = responseDoc.addNewAssignIPV6Response();
        response.setSuccess(Success.YES);
        response.setNetmaskSize(0);
        response.setNetId(1L);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference site = referenceService.findReference(22400L);
        final Reference purpose = referenceService.findReference(IPPurpose.DhcpV6Pd.getId());

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withIpPool(IPPoolType.XDSL.getId());
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(produktBuilder);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        IPAddress assigned = cut.assignIPV6(auftragDaten.getAuftragId(), purpose, Integer.valueOf(0), site,
                Long.valueOf(-1));

        assertNotNull(assigned);
        assertTrue(NumberTools.equal(assigned.getNetId(), Long.valueOf(response.getNetId())),
                "Net-ID muss gleich sein!");
        assertTrue(NumberTools.equal(assigned.getBillingOrderNo(), auftragDaten.getAuftragNoOrig()),
                "Die Billing Auftragsnummer muss gleich sein!");
        assertTrue(assigned.getIpType() == AddressTypeEnum.IPV6_full, "Der Addresstyp muss IPV6 sein!");
        assertEquals(assigned.getPurpose().getId(), IPPurpose.DhcpV6Pd.getId());
    }

    @Test(expectedExceptions = { StoreException.class })
    /** In Response ist success == no */
    public void assignIPV6_Failed() throws StoreException, IOException, FindException {
        AssignIPV6ResponseDocument responseDoc = AssignIPV6ResponseDocument.Factory.newInstance();
        AssignIPV6Response response = responseDoc.addNewAssignIPV6Response();
        response.setSuccess(Success.NO);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference site = referenceService.findReference(22400L);
        final Reference purpose = referenceService.findReference(IPPurpose.DhcpV6Pd.getId());

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withIpPool(IPPoolType.XDSL.getId());
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(produktBuilder);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        cut.assignIPV6(auftragDaten.getAuftragId(), purpose, Integer.valueOf(0), site, Long.valueOf(-1));
    }

    @Test(expectedExceptions = { StoreException.class })
    /** IPPoolType fehlt */
    public void assignIPV6_ExceptionExpected() throws StoreException, IOException, FindException {
        AssignIPV6ResponseDocument responseDoc = AssignIPV6ResponseDocument.Factory.newInstance();
        AssignIPV6Response response = responseDoc.addNewAssignIPV6Response();
        response.setSuccess(Success.YES);
        response.setNetmaskSize(0);
        response.setNetId(1L);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference site = referenceService.findReference(22400L);
        final Reference purpose = referenceService.findReference(IPPurpose.DhcpV6Pd.getId());

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withIpPool(null);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withProdBuilder(produktBuilder);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();

        cut.assignIPV6(auftragDaten.getAuftragId(), purpose, Integer.valueOf(0), site, Long.valueOf(-1));
    }

    public void findFixedIPs4TechnicalOrder_Success() throws FindException {
        AuftragDaten auftragDaten = (getBuilder(AuftragDatenBuilder.class)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_full).withNetId(Long.valueOf(1)).withAddress("2001::1").build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_prefix).withNetId(Long.valueOf(2)).withAddress("2001::/16")
                .build();

        List<IPAddress> ipList = cut.findFixedIPs4TechnicalOrder(auftragDaten.getAuftragId());

        assertNotNull(ipList);
        assertEquals(ipList.size(), 1);
        assertEquals(AddressTypeEnum.IPV6_full, ipList.get(0).getIpType());
    }

    public void findNets4TechnicalOrder_Success() throws FindException {
        AuftragDaten auftragDaten = (getBuilder(AuftragDatenBuilder.class)).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_full).withNetId(Long.valueOf(1)).withAddress("2001::1").build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_prefix).withNetId(Long.valueOf(2)).withAddress("2001::/16")
                .build();

        List<IPAddress> ipList = cut.findNets4TechnicalOrder(auftragDaten.getAuftragId());

        assertNotNull(ipList);
        assertEquals(ipList.size(), 1);
        assertEquals(AddressTypeEnum.IPV6_prefix, ipList.get(0).getIpType());
    }

    @Test(expectedExceptions = { StoreException.class })
    public void moveIPAddress_NotAssigned() throws FindException, StoreException {
        IPAddress toMove = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV4_prefix)
                .withAddress("192.168.1.1/29").build();
        cut.moveIPAddress(toMove, Long.valueOf(1L), -1L);
    }

    @Test(expectedExceptions = { StoreException.class })
    public void moveIPAddress_NoIpAddressGiven() throws StoreException {
        cut.moveIPAddress(null, Long.valueOf(1234), -1L);
    }

    @Test(expectedExceptions = { StoreException.class })
    public void moveIPAddress_BillingOrderNoEquals() throws FindException, StoreException {
        Long billingOrderNo = Long.valueOf(1L);
        IPAddress toMove = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV4_prefix)
                .withBillingOrderNumber(billingOrderNo).withAddress("192.168.1.1/29").withNetId(Long.valueOf(1))
                .build();
        cut.moveIPAddress(toMove, billingOrderNo, -1L);
    }

    @Test
    public void moveIPAddress_V4Success() throws FindException, StoreException {
        Long billingOrderNoOld = Long.valueOf(1L);
        Long billingOrderNoNew = Long.valueOf(2L);
        IPAddress toMove = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV4_prefix)
                .withBillingOrderNumber(billingOrderNoOld).withAddress("192.168.1.1/29").withNetId(Long.valueOf(1L))
                .build();

        IPAddress saved = cut.moveIPAddress(toMove, billingOrderNoNew, -1L);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(saved.getBillingOrderNo(), billingOrderNoNew);
        assertEquals(toMove.getGueltigBis(), saved.getGueltigVon());
        assertEquals(saved.getGueltigBis(), DateTools.getHurricanEndDate());
        assertFalse(NumberTools.equal(toMove.getBillingOrderNo(), saved.getBillingOrderNo()));
    }

    @Test
    public void moveIPAddress_V6Success() throws FindException, StoreException {
        Long billingOrderNoOld = Long.valueOf(1L);
        Long billingOrderNoNew = Long.valueOf(2L);
        IPAddress toMove = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNoOld).withAddress("2001:db8::/32").withNetId(Long.valueOf(1))
                .build();

        IPAddress saved = cut.moveIPAddress(toMove, billingOrderNoNew, -1L);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(saved.getBillingOrderNo(), billingOrderNoNew);
        assertEquals(toMove.getGueltigBis(), saved.getGueltigVon());
        assertEquals(saved.getGueltigBis(), DateTools.getHurricanEndDate());
        assertFalse(NumberTools.equal(toMove.getBillingOrderNo(), saved.getBillingOrderNo()));
    }

    @Test
    public void moveIPAddress_V6Success_WithRelativeIp() throws FindException, StoreException {
        Long billingOrderNoOld = Long.valueOf(1L);
        Long billingOrderNoNew = Long.valueOf(2L);
        IPAddress toMove = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNoOld).withAddress("2001:db8::/32").withNetId(Long.valueOf(1))
                .build();
        IPAddress relativeIp = getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_relative)
                .withBillingOrderNumber(billingOrderNoOld).withAddress("0:0:a:b::/48").withNetId(null)
                .withPrefixRef(toMove).build();

        IPAddress saved = cut.moveIPAddress(toMove, billingOrderNoNew, -1L);
        // Assert Prefix
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(saved.getBillingOrderNo(), billingOrderNoNew);
        assertEquals(toMove.getGueltigBis(), saved.getGueltigVon());
        assertEquals(saved.getGueltigBis(), DateTools.getHurricanEndDate());
        assertFalse(NumberTools.equal(toMove.getBillingOrderNo(), saved.getBillingOrderNo()));
        // Assert relative Ip
        assertNull(relativeIp.getPrefixRef());
    }

    @Test
    public void releaseIPAddressesNow_V4_Success() throws Exception {
        IPAddress ipV4 = createIPv4WithEndDateForRelease(null, Long.valueOf(1L));
        ReleaseIPV4ResponseDocument responseDoc = createReleaseIPV4ResponseDocument(true);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        AKWarnings warnings = cut.releaseIPAddressesNow(Arrays.asList(ipV4), -1L);
        assertTrue(warnings.isEmpty());
        assertEquals(ipV4.getGueltigBis(), ipV4.getFreigegeben());

        checkIsToday(ipV4.getGueltigBis());
    }

    @Test
    public void releaseIPAddressesNow_V4_SuccessWith2Orders() throws Exception {
        long netId = 1L;
        Date yesterday = DateTools.minusWorkDays(1);
        IPAddress ipV4_1 = createIPv4WithEndDateForRelease(Long.valueOf(1L), netId);
        IPAddress ipV4_2 = createIPv4ForRelease(Long.valueOf(2L), netId, yesterday);
        ReleaseIPV4ResponseDocument responseDoc = createReleaseIPV4ResponseDocument(true);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        AKWarnings warnings = cut.releaseIPAddressesNow(Arrays.asList(ipV4_1), -1L);
        assertTrue(warnings.isEmpty());
        checkIsToday(ipV4_1.getGueltigBis());
        assertTrue(ipV4_1.getFreigegeben() != null);
        assertEquals(DateTools.compareDates(ipV4_2.getGueltigBis(), yesterday), 0);
        assertTrue(ipV4_2.getFreigegeben() != null);
    }

    @Test
    public void releaseIPAddressesNow_V4_TwoOrdersAssigned() throws Exception {
        long netId = 200000000L;
        IPAddress ipV4_1 = createIPv4WithEndDateForRelease(Long.valueOf(1L), netId);
        // let it find 2 orders associated with the ip
        IPAddress ipV4_2 = createIPv4WithEndDateForRelease(Long.valueOf(2L), netId);
        ReleaseIPV4ResponseDocument responseDoc = createReleaseIPV4ResponseDocument(true);

        MockWebServiceServer mockServer = MockWebServiceServer.createServer(monlineWebServiceTemplate);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(responseDoc.newInputStream())));

        AKWarnings warnings = cut.releaseIPAddressesNow(Arrays.asList(ipV4_1), -1L);

        assertTrue(!warnings.isEmpty());
        checkIsToday(ipV4_1.getGueltigBis());
        assertTrue(ipV4_1.getFreigegeben() != null);
        checkIsToday(ipV4_2.getGueltigBis());
        assertTrue(ipV4_2.getFreigegeben() != null);
    }

    private IPAddress createIPv4WithEndDateForRelease(Long billingOrderNo, Long netId) {
        return createIPv4ForRelease(billingOrderNo, netId, null);
    }

    private IPAddress createIPv4ForRelease(Long billingOrderNo, Long netId, Date endDate) {
        return getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV4_prefix)
                .withBillingOrderNumber((billingOrderNo == null) ? RandomTools.createLong() : billingOrderNo)
                .withAddress("1.1.1.1/29").withGueltigVon(new Date())
                .withGueltigBis((endDate == null) ? DateTools.getHurricanEndDate() : endDate).withNetId(netId).build();
    }

    private ReleaseIPV4ResponseDocument createReleaseIPV4ResponseDocument(boolean success) {
        ReleaseIPV4ResponseDocument responseDoc = ReleaseIPV4ResponseDocument.Factory.newInstance();
        ReleaseIPV4Response response = responseDoc.addNewReleaseIPV4Response();
        response.setSuccess(success ? Success.YES : Success.NO);
        return responseDoc;
    }

    private void checkIsToday(Date toCheck) {
        assertNotNull(toCheck);
        assertEquals(DateTools.compareDates(toCheck, new Date()), 0);
    }

    @Test
    public void findIPs4NetId_Success() throws FindException {
        final long netId = Long.MAX_VALUE;
        IPAddress[] ips = new IPAddress[] {
                getBuilder(IPAddressBuilder.class).withNetId(netId).build(),
                getBuilder(IPAddressBuilder.class).withNetId(netId).build()
        };

        List<IPAddress> ipsFound = cut.findIPs4NetId(netId);

        assertEquals(ipsFound.size(), ips.length);
        for (IPAddress ip : ips) {
            assertTrue(ipsFound.contains(ip));
        }
    }

    @DataProvider(name = "filterIPsByBinaryRepresentationV4DP")
    public Object[][] filterIPsByBinaryRepresentationV4DP() {
        // @formatter:off
        return new Object[][] {
                { new IPAddressSearchQuery("1.2.0.0/16", true, false, Integer.valueOf(500)), 3},
                { new IPAddressSearchQuery("1.2.0.0/16", true, true,  Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1.2.3.0/24", true, false, Integer.valueOf(500)), 3},
                { new IPAddressSearchQuery("1.2.3.0/24", true, true,  Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1.2.3.1",    true, false, Integer.valueOf(500)), 3},
                { new IPAddressSearchQuery("1.2.3.1",    true, true,  Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1.2.1.0/24", true, false, Integer.valueOf(500)), 1},
                { new IPAddressSearchQuery("1.2.1.0/24", true, true,  Integer.valueOf(500)), 1},
                { new IPAddressSearchQuery("1.3.0.0/16", true, false, Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1.3.0.0/16", true, true,  Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1.3.1.2",    true, false, Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1.3.1.2",    true, true,  Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1.3.1.0/24", true, false, Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1.3.1.0/24", true, true,  Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1.4.0.0/16", true, false, Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1.4.0.0/16", true, true,  Integer.valueOf(500)), 0},};
        // @formatter:on
    }

    @Test(dataProvider = "filterIPsByBinaryRepresentationV4DP")
    public void testFilterIPsByBinaryRepresentationV4(IPAddressSearchQuery searchQuery, int expectedCountIPs) throws FindException {
        AuftragDaten auftragDaten = createIPs4FilterV4IPs();
        List<IPAddressSearchView> views = cut.filterIPsByBinaryRepresentation(searchQuery);
        assertTrue(checkIPAddressSearchView(views, auftragDaten.getAuftragNoOrig(), expectedCountIPs));
    }

    private AuftragDaten createIPs4FilterV4IPs() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);

        getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder).build();
        AuftragDaten auftragDaten = auftragDatenBuilder.withAuftragBuilder(auftragBuilder).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV4_prefix)
                .withAddress("1.2.0.0/16")
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV4_prefix)
                .withAddress("1.2.3.0/24")
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV4)
                .withAddress("1.2.3.1")
                .withGueltigBis(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV4_prefix)
                .withAddress("1.3.0.0/16")
                .withGueltigBis(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV4_with_prefixlength)
                .withAddress("1.3.1.2/16")
                .withGueltigBis(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress("2001:db8:a001::/48")
                .build();
        return auftragDaten;
    }

    @DataProvider(name = "filterIPsByBinaryRepresentationV6DP")
    public Object[][] filterIPsByBinaryRepresentationV6DP() {
        // @formatter:off
        return new Object[][] {
                { new IPAddressSearchQuery("1:2::/32",   false, false, Integer.valueOf(500)), 3},
                { new IPAddressSearchQuery("1:2::/32",   false, true,  Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1:2:3::/48", false, false, Integer.valueOf(500)), 3},
                { new IPAddressSearchQuery("1:2:3::/48", false, true,  Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1:2:3:1::",  false, false, Integer.valueOf(500)), 3},
                { new IPAddressSearchQuery("1:2:3:1::",  false, true,  Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1:2:1::/48", false, false, Integer.valueOf(500)), 1},
                { new IPAddressSearchQuery("1:2:1::/48", false, true,  Integer.valueOf(500)), 1},
                { new IPAddressSearchQuery("1:3::/32",   false, false, Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1:3::/32",   false, true,  Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1:3:1:2::",  false, false, Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1:3:1:2::",  false, true,  Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1:3:1::/48", false, false, Integer.valueOf(500)), 2},
                { new IPAddressSearchQuery("1:3:1::/48", false, true,  Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1:4::/32",   false, false, Integer.valueOf(500)), 0},
                { new IPAddressSearchQuery("1:4::/32",   false, true,  Integer.valueOf(500)), 0},};
        // @formatter:on
    }

    @Test(dataProvider = "filterIPsByBinaryRepresentationV6DP")
    public void testFilterIPsByBinaryRepresentationV6(IPAddressSearchQuery searchQuery, int expectedCountIPs) throws FindException {
        AuftragDaten auftragDaten = createIPs4FilterV6IPs();
        List<IPAddressSearchView> views = cut.filterIPsByBinaryRepresentation(searchQuery);
        assertTrue(checkIPAddressSearchView(views, auftragDaten.getAuftragNoOrig(), expectedCountIPs));
    }

    private AuftragDaten createIPs4FilterV6IPs() {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);

        getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder).build();
        AuftragDaten auftragDaten = auftragDatenBuilder.withAuftragBuilder(auftragBuilder).build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress("1:2::/32")
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress("1:2:3::/48")
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_full)
                .withAddress("1:2:3:1::")
                .withGueltigBis(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_prefix)
                .withAddress("1:3::/32")
                .withGueltigBis(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV6_with_prefixlength)
                .withAddress("1:3:1:2::")
                .withGueltigBis(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
                .build();
        getBuilder(IPAddressBuilder.class).withBillingOrderNumber(auftragDaten.getAuftragNoOrig())
                .withAddressType(AddressTypeEnum.IPV4_prefix)
                .withAddress("1.2.0.0/16")
                .build();
        return auftragDaten;
    }

    private boolean checkIPAddressSearchView(List<IPAddressSearchView> views, Long billingOrderNo, int expectedCountIPs) {
        if (CollectionTools.isEmpty(views)) {
            if (expectedCountIPs == 0) {
                return true;
            }
            return false;
        }

        int countIPs = 0;
        for (IPAddressSearchView ipAddressSearchView : views) {
            if (NumberTools.equal(ipAddressSearchView.getBillingOrderNo(), billingOrderNo)) {
                countIPs++;
            }
        }

        return (countIPs == expectedCountIPs) ? true : false;
    }

    @Test
    public void findDHCPv6PDPrefix_Success() throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference dhcpv6pdPurpose = referenceService.findReference(IPPurpose.DhcpV6Pd.getId());
        Long billingOrderNo = Long.valueOf(1234);
        Long netId = Long.valueOf(1L);
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(netId).withPurpose(dhcpv6pdPurpose).build();

        IPAddress dhcpv6pdPrefix = cut.findDHCPv6PDPrefix(billingOrderNo);

        assertNotNull(dhcpv6pdPrefix);
        assertEquals(dhcpv6pdPrefix.getNetId(), netId);
        assertEquals(dhcpv6pdPrefix.getIpType(), AddressTypeEnum.IPV6_prefix);
        assertEquals(dhcpv6pdPrefix.getPurpose().getId(), dhcpv6pdPurpose.getId());
    }

    @Test(expectedExceptions = { NonUniqueResultException.class })
    public void findDHCPv6PDPrefix_Fail() throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference dhcpv6pdPurpose = referenceService.findReference(IPPurpose.DhcpV6Pd.getId());
        Long billingOrderNo = Long.valueOf(1234);
        Long netId1 = Long.valueOf(1L);
        Long netId2 = Long.valueOf(1L);
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(netId1).withPurpose(dhcpv6pdPurpose).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(netId2).withPurpose(dhcpv6pdPurpose).build();

        cut.findDHCPv6PDPrefix(billingOrderNo);
        fail();
    }

    @Test
    public void findDHCPv6PDPrefix4TechnicalOrder_Success() throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference dhcpv6pdPurpose = referenceService.findReference(IPPurpose.DhcpV6Pd.getId());
        Long billingOrderNo = Long.valueOf(1234);
        Long netId = Long.valueOf(1L);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(
                billingOrderNo);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(netId).withPurpose(dhcpv6pdPurpose).build();

        IPAddress dhcpv6pdPrefix = cut.findDHCPv6PDPrefix4TechnicalOrder(auftragDaten.getAuftragId());

        assertNotNull(dhcpv6pdPrefix);
        assertEquals(dhcpv6pdPrefix.getNetId(), netId);
        assertEquals(dhcpv6pdPrefix.getIpType(), AddressTypeEnum.IPV6_prefix);
        assertEquals(dhcpv6pdPrefix.getPurpose().getId(), dhcpv6pdPurpose.getId());
    }

    @Test(expectedExceptions = { NonUniqueResultException.class })
    public void findDHCPv6PDPrefix4TechnicalOrder_Fail() throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference dhcpv6pdPurpose = referenceService.findReference(IPPurpose.DhcpV6Pd.getId());
        Long billingOrderNo = Long.valueOf(1234);
        Long netId1 = Long.valueOf(1L);
        Long netId2 = Long.valueOf(1L);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragNoOrig(
                billingOrderNo);
        AuftragDaten auftragDaten = auftragDatenBuilder.get();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(netId1).withPurpose(dhcpv6pdPurpose).build();
        getBuilder(IPAddressBuilder.class).withAddressType(AddressTypeEnum.IPV6_prefix)
                .withBillingOrderNumber(billingOrderNo).withNetId(netId2).withPurpose(dhcpv6pdPurpose).build();

        cut.findDHCPv6PDPrefix4TechnicalOrder(auftragDaten.getAuftragId());
        fail();
    }
}

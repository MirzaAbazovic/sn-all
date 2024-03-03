/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2011 14:48:41
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.dao.cc.IPAddressDAO;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IPPoolType;
import de.augustakom.hurrican.model.cc.IPPurpose;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressAssignmentRemoteService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.impl.ipaddress.ReleaseDateCalculator;
import de.augustakom.hurrican.service.internet.INetNumService;
import de.mnet.monline.ipProviderService.ipp.Site;

/**
 * Modultests fuer {@link IPAddressServiceImpl}.
 */
@Test(groups = { BaseTest.UNIT })
public class IPAddressServiceImplUnitTest extends BaseTest {

    @Spy
    private IPAddressServiceImpl sut = new IPAddressServiceImpl();
    private IPAddress ipV4, ipV6;

    @Mock
    private IPAddressAssignmentRemoteService addrAssignmentService;
    @Mock
    private IPAddressDAO ipAddressDao;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private ReleaseDateCalculator releaseDateCalculator;
    @Mock
    private ProduktService produktService;
    @Mock
    private ReferenceService referenceService;

    private Reference kundennetz;
    private Reference transfernetz;

    @BeforeClass
    public void setupClass() {
        kundennetz = new ReferenceBuilder().setPersist(false).withId(Long.MAX_VALUE).build();
        transfernetz = new ReferenceBuilder().setPersist(false).withId(Long.MAX_VALUE - 1).build();
    }

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        sut.setIpAddressAssignmentService(addrAssignmentService);
        sut.setIpAddrDao(ipAddressDao);
        sut.setAuftragService(auftragService);
        sut.setProduktService(produktService);
        sut.setReferenceService(referenceService);
        Mockito.doReturn(releaseDateCalculator).when(sut).getReleaseDateCalculator();
    }

    @Test(expectedExceptions = { StoreException.class })
    public void testAssignIPV4_NoValidPurpose() throws StoreException, FindException {
        Reference invalidPurpose = new ReferenceBuilder().setPersist(false).withIntValue(1234).withStrValue("asdf")
                .build();
        Reference site = new ReferenceBuilder().setPersist(false).withIntValue(1).withStrValue(Site.AGB.toString())
                .build();
        when(produktService.findProdukt4Auftrag(anyLong())).thenReturn(
                new ProduktBuilder().setPersist(false).withIpPool(IPPoolType.XDSL.getId()).build());
        sut.assignIPV4(1234L, invalidPurpose, 1234, site, -1L);
    }

    @DataProvider(name = "releaseIPAddressesNowDP")
    public Object[][] releaseIPAddressesNowDP() {
        ipV4 = createIPv4();
        ipV6 = createIPv6();

        IPAddress ipV4_2 = new IPAddressBuilder().setPersist(false).withAddressType(AddressTypeEnum.IPV4_prefix)
                .build();

        IPAddress ipV6_2 = new IPAddressBuilder().setPersist(false).withAddressType(AddressTypeEnum.IPV6_prefix)
                .build();

        return new Object[][] {
                // @formatter:off
                { ipV4, Collections.singletonList(0L), Arrays.asList(ipV4, ipV4_2)},
                { ipV4, Collections.emptyList(), Arrays.asList(ipV4, ipV4_2)},
                { ipV4, Collections.singletonList(1L), Collections.singletonList(ipV4)},
                { ipV4, Collections.emptyList(), Collections.singletonList(ipV4)},
                { ipV6, Collections.singletonList(0L), Arrays.asList(ipV6, ipV6_2)},
                { ipV6, Collections.emptyList(), Arrays.asList(ipV6, ipV6_2)},
                { ipV6, Collections.singletonList(1L), Collections.singletonList(ipV6)},
                { ipV6, Collections.emptyList(), Collections.singletonList(ipV6)}
                // @formatter:on
        };
    }

    @Test(dataProvider = "releaseIPAddressesNowDP")
    public void releaseIPAddressesNow(IPAddress ipAddr, List<Long> billingOrderNos, List<IPAddress> ipList)
            throws Exception {
        final int serviceCallCnt = ((!billingOrderNos.isEmpty()) ? 0 : 1);
        final boolean warningsExp = (serviceCallCnt == 0);
        when(ipAddressDao.findActiveIPs4OtherOrders(eq(ipAddr.getNetId()), any(Long.class)))
                .thenReturn(billingOrderNos);
        when(ipAddressDao.findNonReleasedIPs4NetId(eq(ipAddr.getNetId()))).thenReturn(ipList);

        AKWarnings warnings = sut.releaseIPAddressesNow(Collections.singletonList(ipAddr), -1L);
        verify(ipAddressDao, times(1)).store(eq(ipAddr));
        verify(ipAddressDao, times(ipList.size())).store(any(IPAddress.class));
        if (ipAddr.isIPV4()) {
            verify(addrAssignmentService, times(1)).releaseIPv4(eq(ipAddr.getAddress()),
                    eq(ipAddr.getNetId()), eq(IPToolsV4.instance().netmaskSize(ipAddr.getAddress())));
        }
        else if (ipAddr.isIPV6()) {
            verify(addrAssignmentService, times(1)).releaseIPv6(eq(ipAddr.getAddress()),
                    eq(ipAddr.getNetId()), eq(IPToolsV6.instance().netmaskSize(ipAddr.getAddress())));
        }
        assertEquals(warnings.isNotEmpty(), warningsExp);
    }

    private IPAddress createIPv4() {
        return createIPv4(DateTools.getHurricanEndDate());
    }

    private IPAddress createIPv4(Date gueltigBis) {
        return new IPAddressBuilder().setPersist(false).withAddress("192.0.2.0/24")
                .withBillingOrderNumber(RandomTools.createLong()).withNetId(RandomTools.createLong())
                .withAddressType(AddressTypeEnum.IPV4_prefix).withGueltigBis(gueltigBis).build();
    }

    private IPAddress createIPv6() {
        return new IPAddressBuilder().setPersist(false).withAddress("2001:0db8:85a3:08d3::/64")
                .withBillingOrderNumber(RandomTools.createLong()).withNetId(RandomTools.createLong())
                .withAddressType(AddressTypeEnum.IPV6_prefix).build();
    }

    private Set<IPAddress> createIPAddressSet(IPAddress... ips) {
        Set<IPAddress> toReturn = new HashSet<>();
        if ((ips != null) && (ips.length > 0)) {
            for (IPAddress ip : ips) {
                toReturn.add(ip);
            }
        }
        return toReturn;
    }

    @DataProvider(name = "findAllNetIdsWithPurposeDP")
    public Object[][] findAllNetIdsWithPurposeDP() {
        return new Object[][] { { new Pair<>(RandomTools.createLong(), "asdfLINKjkl"), transfernetz },
                { new Pair<>(RandomTools.createLong(), "asdfjlk"), kundennetz }, };
    }

    @Test(dataProvider = "findAllNetIdsWithPurposeDP")
    public void findAllNetIdsWithPurposeFromMonline(Pair<Long, String> netIdsWithPool, Reference expPurpose)
            throws ServiceNotFoundException, FindException {
        INetNumService inetNumService = mock(INetNumService.class);
        doReturn(inetNumService).when(sut).getInternetService(INetNumService.class);
        when(referenceService.findReference(IPPurpose.Kundennetz.getId())).thenReturn(kundennetz);
        when(referenceService.findReference(IPPurpose.Transfernetz.getId())).thenReturn(transfernetz);
        when(inetNumService.findAllNetIdsWithPoolName()).thenReturn(Collections.singletonList(netIdsWithPool));
        Pair<Long, Reference> result = sut.findAllNetIdsWithPurposeFromMonline().iterator().next();
        assertEquals(result.getSecond(), expPurpose);
    }

    @DataProvider(name = "findAllIPAddressPanelViews_CheckStatusDP")
    public Object[][] findAllIPAddressPanelViews_CheckStatusDP() {
        final Date now = new Date();
        //@formatter:off
        return new Object[][] {
                { RandomTools.createInteger(), now, DateTools.getHurricanEndDate(), null,
                    Collections.singletonList(1L), IPAddressServiceImpl.IPADDRESSPANELVIEW_STATE_ACTIVE},
                { 0, now, now, null, Collections.singletonList(1L), IPAddressServiceImpl.IPADDRESSPANELVIEW_STATE_KARENZZEIT },
                { 0, now, now, now, Collections.singletonList(1L), IPAddressServiceImpl.IPADDRESSPANELVIEW_STATE_FREIGEGEBEN },
                { RandomTools.createInteger(), now, now, now, Collections.singletonList(1L), IPAddressServiceImpl.IPADDRESSPANELVIEW_STATE_ASSIGNED_TO_OTHER },
                { RandomTools.createInteger(), now, now, now, Collections.emptyList(), IPAddressServiceImpl.IPADDRESSPANELVIEW_STATE_ASSIGNED_TO_SAME },
        };
       //@formatter:on
    }

    @Test(dataProvider = "findAllIPAddressPanelViews_CheckStatusDP")
    public void findAllIPAddressPanelViews_CheckStatus(int orderCnt, Date gueltigVon, Date gueltigBis,
            Date freigegeben, List<Long> otherOrdersCnt, String statusExp) throws FindException {
        long netId = RandomTools.createLong();
        IPAddress ip = new IPAddressBuilder().setPersist(false).withFreigegeben(freigegeben).withNetId(netId)
                .withGueltigVon(gueltigVon).withGueltigBis(gueltigBis).build();
        when(ipAddressDao.findAllAssignedIPs4BillingOrder(anyLong())).thenReturn(Collections.singletonList(ip));
        when(releaseDateCalculator.get()).thenReturn(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, 30));
        when(ipAddressDao.findIpToOrdersAssignmentCount(eq(netId))).thenReturn(orderCnt);
        when(ipAddressDao.findActiveIPs4OtherOrders(eq(netId), anyLong())).thenReturn(otherOrdersCnt);
        doReturn(releaseDateCalculator).when(sut).getReleaseDateCalculator();
        IPAddressPanelView views = sut.findAllIPAddressPanelViews(RandomTools.createLong()).iterator().next();
        assertEquals(views.getStatus(), statusExp);
    }

    @DataProvider(name = "checkKuendigungenDP")
    public Object[][] checkKuendigungenDP() {
        Date now = new Date();
        Date future = DateTools.plusWorkDays(10);
        Long inBetrieb = AuftragStatus.IN_BETRIEB;
        Long gekuendigt = AuftragStatus.AUFTRAG_GEKUENDIGT;
        Long inKuendigung = AuftragStatus.KUENDIGUNG_ERFASSEN;
        Date coolingPeriodExpired = DateTools.minusWorkDays(250);
        //@formatter:off
        return new Object[][] {
                { true, createAuftragDatenSet() },
                { true, createAuftragDatenSet(createAuftragDaten(inBetrieb, null))},
                { true, createAuftragDatenSet(createAuftragDaten(inKuendigung, null))},
                { true, createAuftragDatenSet(createAuftragDaten(gekuendigt, coolingPeriodExpired))},
                { true, createAuftragDatenSet(createAuftragDaten(gekuendigt, coolingPeriodExpired), createAuftragDaten(inBetrieb, null))},
                { true, createAuftragDatenSet(createAuftragDaten(gekuendigt, coolingPeriodExpired), createAuftragDaten(inKuendigung, null))},
                { true, createAuftragDatenSet(createAuftragDaten(gekuendigt, coolingPeriodExpired), createAuftragDaten(gekuendigt, coolingPeriodExpired))},
                { false, createAuftragDatenSet(createAuftragDaten(gekuendigt, coolingPeriodExpired), createAuftragDaten(gekuendigt, future))},
                { false, createAuftragDatenSet(createAuftragDaten(gekuendigt, coolingPeriodExpired), createAuftragDaten(gekuendigt, now))},
                { false, createAuftragDatenSet(createAuftragDaten(gekuendigt, future))},
                { false, createAuftragDatenSet(createAuftragDaten(gekuendigt, now))},
        };
       //@formatter:on
    }

    private AuftragDaten createAuftragDaten(Long statusId, Date kuendigung) {
        return new AuftragDatenBuilder().setPersist(false).withStatusId(statusId).withKuendigung(kuendigung).build();
    }

    private Set<AuftragDaten> createAuftragDatenSet(AuftragDaten... auftragDatenArgs) {
        Set<AuftragDaten> toReturn = new HashSet<>();
        if ((auftragDatenArgs != null) && (auftragDatenArgs.length > 0)) {
            for (AuftragDaten auftragDaten : auftragDatenArgs) {
                toReturn.add(auftragDaten);
            }
        }
        return toReturn;
    }

    @Test(dataProvider = "checkKuendigungenDP")
    public void checkKuendigungen(boolean expectedResult, Set<AuftragDaten> auftragDatenSet) throws FindException {
        when(releaseDateCalculator.get()).thenReturn(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -30));
        assertEquals(expectedResult, sut.checkKuendigungen(auftragDatenSet));
    }

    @DataProvider(name = "checkAuftragStatusDP")
    public Object[][] checkAuftragStatusDP() {
        Long storno = AuftragStatus.STORNO;
        Long abgesagt = AuftragStatus.ABSAGE;
        Long inBetrieb = AuftragStatus.IN_BETRIEB;
        Long konsolidiert = AuftragStatus.KONSOLIDIERT;
        Long gekuendigt = AuftragStatus.AUFTRAG_GEKUENDIGT;
        Long inKuendigung = AuftragStatus.KUENDIGUNG_ERFASSEN;
        //@formatter:off
        return new Object[][] {
                { true, createAuftragDatenSet() },
                { true, createAuftragDatenSet(createAuftragDaten(storno, null))},
                { true, createAuftragDatenSet(createAuftragDaten(abgesagt, null))},
                { true, createAuftragDatenSet(createAuftragDaten(gekuendigt, null))},
                { true, createAuftragDatenSet(createAuftragDaten(konsolidiert, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inBetrieb, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inBetrieb, null), createAuftragDaten(storno, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inBetrieb, null), createAuftragDaten(abgesagt, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inBetrieb, null), createAuftragDaten(gekuendigt, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inBetrieb, null), createAuftragDaten(konsolidiert, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inBetrieb, null), createAuftragDaten(storno, null), createAuftragDaten(abgesagt, null),
                        createAuftragDaten(gekuendigt, null), createAuftragDaten(konsolidiert, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inKuendigung, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inKuendigung, null), createAuftragDaten(storno, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inKuendigung, null), createAuftragDaten(abgesagt, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inKuendigung, null), createAuftragDaten(gekuendigt, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inKuendigung, null), createAuftragDaten(konsolidiert, null))},
                { false, createAuftragDatenSet(createAuftragDaten(inKuendigung, null), createAuftragDaten(storno, null), createAuftragDaten(abgesagt, null),
                        createAuftragDaten(gekuendigt, null), createAuftragDaten(konsolidiert, null))},
        };
       //@formatter:on
    }

    @Test(dataProvider = "checkAuftragStatusDP")
    public void checkAuftragStatus(boolean expectedResult, Set<AuftragDaten> auftragDatenSet) throws FindException {
        assertEquals(expectedResult, sut.checkAuftragStatus(auftragDatenSet));
    }

    @SuppressWarnings("unchecked")
    public void releaseIPAdresses_Success() throws FindException {
        doReturn(true).when(sut).isReady2Release(any(Set.class));
        Map<Long, Set<IPAddress>> map = new HashMap<>();
        map.put(1L, createIPAddressSet(createIPv4()));
        when(ipAddressDao.findAllActiveAndAssignedIPs()).thenReturn(map);
        AKWarnings deAllocWarnings = new AKWarnings();
        deAllocWarnings.addAKWarning(this, "Test!");
        doReturn(deAllocWarnings).when(sut).deallocateIPs(any(Set.class), any(Long.class));

        AKWarnings warnings = sut.releaseIPAddresses(Long.valueOf(-1));
        assertTrue((warnings.getAKMessages().size() == 2));
        verify(sut, times(1)).deallocateIPs(any(Set.class), any(Long.class));
    }

    @SuppressWarnings("unchecked")
    public void releaseIPAdresses_WithEmptyMap() throws FindException {
        doReturn(true).when(sut).isReady2Release(any(Set.class));
        when(ipAddressDao.findAllActiveAndAssignedIPs()).thenReturn(new HashMap<>());
        doReturn(new AKWarnings()).when(sut).deallocateIPs(any(Set.class), any(Long.class));

        AKWarnings warnings = sut.releaseIPAddresses(Long.valueOf(-1));
        assertTrue((warnings.getAKMessages() == null) || (warnings.getAKMessages().size() == 0));
        verify(sut, times(0)).deallocateIPs(any(Set.class), any(Long.class));
    }

    @SuppressWarnings("unchecked")
    public void releaseIPAdresses_WithoutIPs() throws FindException {
        doReturn(true).when(sut).isReady2Release(any(Set.class));
        Map<Long, Set<IPAddress>> map = new HashMap<>();
        map.put(1L, createIPAddressSet());
        when(ipAddressDao.findAllActiveAndAssignedIPs()).thenReturn(map);
        doReturn(new AKWarnings()).when(sut).deallocateIPs(any(Set.class), any(Long.class));

        AKWarnings warnings = sut.releaseIPAddresses(Long.valueOf(-1));
        assertTrue((warnings.getAKMessages() == null) || (warnings.getAKMessages().size() == 0));
        verify(sut, times(0)).deallocateIPs(any(Set.class), any(Long.class));
    }

    @SuppressWarnings("unchecked")
    public void releaseIPAdresses_NotReady() throws FindException {
        doReturn(false).when(sut).isReady2Release(any(Set.class));
        Map<Long, Set<IPAddress>> map = new HashMap<>();
        map.put(1L, createIPAddressSet(createIPv4()));
        when(ipAddressDao.findAllActiveAndAssignedIPs()).thenReturn(map);
        doReturn(new AKWarnings()).when(sut).deallocateIPs(any(Set.class), any(Long.class));

        AKWarnings warnings = sut.releaseIPAddresses(Long.valueOf(-1));
        assertTrue((warnings.getAKMessages() == null) || (warnings.getAKMessages().size() == 0));
        verify(sut, times(0)).deallocateIPs(any(Set.class), any(Long.class));
    }

    public void getAuftragDaten4ActiveOrFuture_ActiveOnly() throws FindException {
        Map<Long, Set<IPAddress>> ipAddressmap = new HashMap<>();
        Set<IPAddress> ipAddressSet = createIPAddressSet(createIPv4());
        ipAddressmap.put(ipAddressSet.iterator().next().getNetId(), ipAddressSet);
        List<AuftragDaten> auftragDatenList = new ArrayList<>();
        auftragDatenList.add(createAuftragDaten(AuftragStatus.IN_BETRIEB, null));
        when(auftragService.findAuftragDaten4OrderNoOrig(ipAddressSet.iterator().next().getBillingOrderNo()))
                .thenReturn(auftragDatenList);
        Set<AuftragDaten> auftragDatenSet = new HashSet<>();
        sut.getAuftragDaten4ActiveOrFuture(ipAddressSet, auftragDatenSet, new Date());
        assertTrue(auftragDatenSet.size() == 1);
        verify(auftragService, times(1)).findAuftragDaten4OrderNoOrig(
                ipAddressSet.iterator().next().getBillingOrderNo());
        assertTrue(auftragDatenSet.iterator().next().equals(auftragDatenList.get(0)));
    }

    public void getAuftragDaten4ActiveOrFuture_OutDatedOnly() throws FindException {
        Map<Long, Set<IPAddress>> ipAddressmap = new HashMap<>();
        Set<IPAddress> ipAddressSet = createIPAddressSet(createIPv4(DateTools.minusWorkDays(1)));
        ipAddressmap.put(ipAddressSet.iterator().next().getNetId(), ipAddressSet);
        Set<AuftragDaten> auftragDatenSet = new HashSet<>();
        sut.getAuftragDaten4ActiveOrFuture(ipAddressSet, auftragDatenSet, new Date());
        assertTrue(auftragDatenSet.isEmpty());
        verify(auftragService, never()).findAuftragDaten4OrderNoOrig(any(Long.class));
    }

    public void getAuftragDaten4LastOutDatedAddress_BothOutDated() throws FindException {
        Map<Long, Set<IPAddress>> ipAddressmap = new HashMap<>();
        IPAddress ipAddress1 = createIPv4(DateTools.minusWorkDays(2));
        IPAddress ipAddress2 = createIPv4(DateTools.minusWorkDays(1));
        Set<IPAddress> ipAddressSet = createIPAddressSet(ipAddress1, ipAddress2);
        ipAddressmap.put(ipAddressSet.iterator().next().getNetId(), ipAddressSet);
        List<AuftragDaten> auftragDatenList = new ArrayList<>();
        auftragDatenList.add(createAuftragDaten(AuftragStatus.ABSAGE, null));
        when(auftragService.findAuftragDaten4OrderNoOrig(ipAddress2.getBillingOrderNo()))
                .thenReturn(auftragDatenList);
        Set<AuftragDaten> auftragDatenSet = new HashSet<>();
        sut.getAuftragDaten4LastOutDatedAddress(ipAddressSet, auftragDatenSet, new Date());
        assertTrue(auftragDatenSet.size() == 1);
        verify(auftragService, times(1)).findAuftragDaten4OrderNoOrig(ipAddress2.getBillingOrderNo());
        assertTrue(auftragDatenSet.iterator().next().equals(auftragDatenList.get(0)));
    }

}

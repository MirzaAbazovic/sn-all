/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 15:57:58
 */
package de.augustakom.hurrican.service.billing.impl;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.time.DateUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.billing.RufnummerDAO;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.DNTNBBuilder;
import de.augustakom.hurrican.model.billing.DnOnkz2Carrier;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.billing.RufnummerPortierungSelection;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;

@Test(groups = UNIT)
public class RufnummerServiceTest extends BaseTest {

    private static final Long AUFTRAG_NO_ORIG_TEST = 1L;

    @InjectMocks
    private RufnummerServiceImpl testling;

    @Mock
    private BillingAuftragService billingAuftragService;

    @Mock
    private RufnummerDAO rufnummerDAO;

    @BeforeMethod
    public void setUp() {
        testling = spy(new RufnummerServiceImpl());
        MockitoAnnotations.initMocks(this);
        testling.setDAO(rufnummerDAO);
    }

    public void findDNs4TalOrder() throws FindException {
        Rufnummer rn1 = new Rufnummer();
        rn1.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rn1.setRealDate(new Date());
        Rufnummer rn2 = new Rufnummer();
        Rufnummer rn3 = new Rufnummer();
        rn3.setPortMode(Rufnummer.PORT_MODE_KOMMEND);
        rn3.setLastCarrierDnTnb(new DNTNBBuilder().withTnb(TNB.NEFKOM.carrierName).setPersist(false).build());
        doReturn(newArrayList(rn1, rn2, rn3)).when(testling).findRNs4Auftrag(AUFTRAG_NO_ORIG_TEST);

        List<RufnummerPortierungSelection> result = testling.findDNs4TalOrder(AUFTRAG_NO_ORIG_TEST, new Date());

        RufnummerPortierungSelection resultDn = getOnlyElement(result);
        assertTrue(resultDn.getSelected());
        assertEquals(resultDn.getRufnummer(), rn1);
    }

    public void findDnsKommendForWbci() throws FindException {
        Preconditions.checkNotNull(AUFTRAG_NO_ORIG_TEST);

        List<Rufnummer> rufnummern = Arrays.asList(
                new RufnummerBuilder()
                        .withDnBase("00000001")
                        .withOnKz("089")
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .withLastCarrier(null, null)
                        .withRealDate(new Date())
                        .build(),
                new RufnummerBuilder()
                        .withDnBase("00000002")
                        .withOnKz("089")
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .withLastCarrier("DTAG", "D001")
                        .withRealDate(DateUtils.addDays(new Date(), 1))
                        .build(),
                new RufnummerBuilder() // Rufnummer ist bereits zu uns portiert worden (gehoert AKom)
                        .withDnBase("00000003")
                        .withOnKz("089")
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .withLastCarrier(TNB.AKOM.carrierName, TNB.AKOM.tnbKennung)
                        .withRealDate(new Date())
                        .build(),
                new RufnummerBuilder() // Rufnummer ist bereits zu uns portiert worden (gehoert MNet)
                        .withDnBase("00000004")
                        .withOnKz("089")
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .withLastCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                        .withRealDate(new Date())
                        .build(),
                new RufnummerBuilder() // Rufnummer ist bereits zu uns portiert worden (gehoert NefKom)
                        .withDnBase("00000005")
                        .withOnKz("089")
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .withLastCarrier(TNB.NEFKOM.carrierName, TNB.NEFKOM.tnbKennung)
                        .withRealDate(new Date())
                        .build(),
                new RufnummerBuilder() // tatsaechlicher Termin liegt in der Vergangenheit
                        .withDnBase("00000006")
                        .withOnKz("089")
                        .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                        .withLastCarrier(null, null)
                        .withRealDate(DateUtils.addDays(new Date(), -1))
                        .build(),
                new RufnummerBuilder() // Portierungsmode ist nicht KOMMEND
                        .withDnBase("00000007")
                        .withOnKz("089")
                        .withPortMode(Rufnummer.PORT_MODE_ABGEHEND)
                        .withLastCarrier(null, null)
                        .withRealDate(new Date())
                        .build()
        );
        when(rufnummerDAO.findByAuftragNoOrig(eq(AUFTRAG_NO_ORIG_TEST))).thenReturn(rufnummern);
        List<Rufnummer> allRufnummern = testling.findDnsKommendForWbci(AUFTRAG_NO_ORIG_TEST);
        assertEquals(allRufnummern.size(), 2);
        assertEquals(allRufnummern.get(0).getRufnummer(), "089/00000001");
        assertEquals(allRufnummern.get(1).getRufnummer(), "089/00000002");
    }

    @Test(expectedExceptions = FindException.class)
    public void findAuftragIdsByEinzelrufnummerExceptionThrown() throws FindException {
        String onkz = "89";
        String dnBase = "1234567890";
        when(rufnummerDAO.findAuftragIdsByEinzelrufnummer("0" + onkz, dnBase)).thenThrow(RuntimeException.class);
        testling.findAuftragIdsByEinzelrufnummer(onkz, dnBase);
    }

    @Test
    public void findAuftragIdsByEinzelrufnummer() throws FindException {
        String onkz = "89";
        String dnBase = "1234567890";
        Set<Long> expectedAuftragIds = new HashSet<>();
        expectedAuftragIds.addAll(Arrays.asList(1L, 2L, 3L));
        when(rufnummerDAO.findAuftragIdsByEinzelrufnummer("0" + onkz, dnBase)).thenReturn(expectedAuftragIds);
        final Set<Long> auftragIdsByEinzelrufnummer = testling.findAuftragIdsByEinzelrufnummer(onkz, dnBase);
        verify(rufnummerDAO).findAuftragIdsByEinzelrufnummer("0" + onkz, dnBase);
        assertNotNull(auftragIdsByEinzelrufnummer);
        assertEquals(auftragIdsByEinzelrufnummer.size(), expectedAuftragIds.size());
    }

    @Test(expectedExceptions = FindException.class)
    public void findAuftragIdsByBlockrufnummerExceptionThrown() throws FindException {
        String onkz = "89";
        String dnBase = "1234567890";
        when(rufnummerDAO.findAuftragIdsByBlockrufnummer("0" + onkz, dnBase)).thenThrow(RuntimeException.class);
        testling.findAuftragIdsByBlockrufnummer(onkz, dnBase);
    }

    @Test
    public void findAuftragIdsByBlockrufnummer() throws FindException {
        String onkz = "89";
        String dnBase = "1234567890";
        Set<Long> expectedAuftragIds = new HashSet<>();
        expectedAuftragIds.addAll(Arrays.asList(1L, 2L, 3L));
        when(rufnummerDAO.findAuftragIdsByBlockrufnummer("0" + onkz, dnBase)).thenReturn(expectedAuftragIds);
        final Set<Long> auftragIdsByBlockrufnummer = testling.findAuftragIdsByBlockrufnummer(onkz, dnBase);
        verify(rufnummerDAO).findAuftragIdsByBlockrufnummer("0" + onkz, dnBase);
        assertNotNull(auftragIdsByBlockrufnummer);
        assertEquals(auftragIdsByBlockrufnummer.size(), expectedAuftragIds.size());
    }

    @Test
    public void testGetCorrespondingOrderNoOrigsAssertEmpty() throws Exception {
        doReturn(null).when(testling).findRNs4Auftrag(anyLong());

        assertEmpty(testling.getCorrespondingOrderNoOrigs(null));
        assertEmpty(testling.getCorrespondingOrderNoOrigs(Long.valueOf(1)));

        verify(testling).findRNs4Auftrag(Long.valueOf(1));
        verify(testling, never()).findNonBillableAuftragIds(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testGetCorrespondingOrderNoOrigs() throws Exception {
        Rufnummer rufnummer = new RufnummerBuilder()
                .withOnKz("089")
                .withDnBase("123546")
                .withRangeFrom("00")
                .withRangeTo("99")
                .withAuftragNoOrig(Long.valueOf(77))
                .setPersist(false)
                .build();

        Set<Long> auftragNoOrigs = new HashSet<>();
        auftragNoOrigs.add(rufnummer.getAuftragNoOrig());
        auftragNoOrigs.add(rufnummer.getAuftragNoOrig() + 1);

        doReturn(Arrays.asList(rufnummer)).when(testling).findRNs4Auftrag(anyLong());
        doReturn(auftragNoOrigs).when(testling).findNonBillableAuftragIds(
                rufnummer.getOnKz(), rufnummer.getDnBase(), rufnummer.getRangeFrom(), rufnummer.getRangeTo());

        Set<Long> result = testling.getCorrespondingOrderNoOrigs(rufnummer.getAuftragNoOrig());
        assertNotEmpty(result);
        assertEquals(result.size(), 2);

        verify(testling).findRNs4Auftrag(rufnummer.getAuftragNoOrig());
        verify(testling).findNonBillableAuftragIds(rufnummer.getOnKz(), rufnummer.getDnBase(),
                rufnummer.getRangeFrom(), rufnummer.getRangeTo());
    }

    @Test
    public void testGetCorrespondingOrderNoOrigs4KlammerNoCorrespongingOrdersFound() throws Exception {
        Long billingOrderNo = 1L;
        BAuftrag bAuftrag = new BAuftragBuilder().setPersist(false).withAstatus(BAuftrag.STATUS_ERFASST).build();
        when(billingAuftragService.findAuftrag(billingOrderNo)).thenReturn(bAuftrag);
        doReturn(Collections.EMPTY_SET).when(testling).getCorrespondingOrderNoOrigs(billingOrderNo);

        final Set<Long> orders4Klammer = testling.getCorrespondingBillingOrders4Klammer(billingOrderNo);
        assertEquals(orders4Klammer.size(), 1);
        assertTrue(orders4Klammer.contains(billingOrderNo));

        verify(testling).getCorrespondingOrderNoOrigs(billingOrderNo);
    }

    @Test
    public void testGetCorrespondingOrderNoOrigs4Klammer() throws Exception {
        Long billingOrderNo = 1L;
        BAuftrag bAuftrag = new BAuftragBuilder().setPersist(false).withAstatus(BAuftrag.STATUS_ERFASST).build();
        when(billingAuftragService.findAuftrag(billingOrderNo)).thenReturn(bAuftrag);
        Long correspondingBillingOrderNo1 = 2L;
        Long correspondingBillingOrderNo2 = 3L;
        Long correspondingBillingOrderNo3 = 4L;
        Long correspondingBillingOrderNo4 = 5L;
        doReturn(new HashSet(Arrays.asList(correspondingBillingOrderNo1, correspondingBillingOrderNo2, correspondingBillingOrderNo3, correspondingBillingOrderNo4)))
                .when(testling).getCorrespondingOrderNoOrigs(billingOrderNo);
        final BAuftrag correspongingAuftrag2 = new BAuftragBuilder().setPersist(false).withAstatus(BAuftrag.STATUS_EINGEGANGEN).build();
        final BAuftrag correspongingAuftrag3 = new BAuftragBuilder().setPersist(false).withAstatus(BAuftrag.STATUS_ERFASST).build();
        when(billingAuftragService.findAuftrag(correspondingBillingOrderNo1)).thenReturn(null);
        when(billingAuftragService.findAuftrag(correspondingBillingOrderNo2)).thenReturn(correspongingAuftrag2);
        when(billingAuftragService.findAuftrag(correspondingBillingOrderNo3)).thenReturn(correspongingAuftrag3);
        when(billingAuftragService.findAuftrag(correspondingBillingOrderNo4)).thenThrow(FindException.class);

        final Set<Long> orders4Klammer = testling.getCorrespondingBillingOrders4Klammer(billingOrderNo);
        assertEquals(orders4Klammer.size(), 2);
        assertTrue(orders4Klammer.contains(billingOrderNo));
        assertTrue(orders4Klammer.contains(correspondingBillingOrderNo3));

        verify(testling).getCorrespondingOrderNoOrigs(billingOrderNo);
        verify(billingAuftragService).findAuftrag(billingOrderNo);
        verify(billingAuftragService).findAuftrag(correspondingBillingOrderNo1);
        verify(billingAuftragService).findAuftrag(correspondingBillingOrderNo2);
        verify(billingAuftragService).findAuftrag(correspondingBillingOrderNo3);
        verify(billingAuftragService).findAuftrag(correspondingBillingOrderNo4);
    }

    @Test
    public void findNonBillingAuftragIds() throws FindException {
        String onkz = "89";
        String dnBase = "1234567890";
        String rangeFrom = "00";
        String rangeTo = "99";

        Set<Long> expectedAuftragIds = new HashSet<>();
        expectedAuftragIds.addAll(Arrays.asList(1L, 2L, 3L));

        when(rufnummerDAO.findNonBillableAuftragIds("0" + onkz, dnBase, null, null)).thenReturn(expectedAuftragIds);
        when(rufnummerDAO.findNonBillableAuftragIds("0" + onkz, dnBase, rangeFrom, rangeTo)).thenReturn(
                Collections.singleton(1L));

        Set<Long> auftragIdsByEinzelrufnummer = testling.findNonBillableAuftragIds(onkz, dnBase, null, null);
        assertNotNull(auftragIdsByEinzelrufnummer);
        assertEquals(auftragIdsByEinzelrufnummer.size(), expectedAuftragIds.size());

        auftragIdsByEinzelrufnummer = testling.findNonBillableAuftragIds(onkz, dnBase, rangeFrom, rangeTo);
        assertNotNull(auftragIdsByEinzelrufnummer);
        assertEquals(auftragIdsByEinzelrufnummer.size(), 1L);

        verify(rufnummerDAO).findNonBillableAuftragIds("0" + onkz, dnBase, null, null);
        verify(rufnummerDAO).findNonBillableAuftragIds("0" + onkz, dnBase, rangeFrom, rangeTo);
    }

    @DataProvider
    public Object[][] dataProviderOnkz() {
        // @formatter:off
        return new Object[][] {
                { "089", "089" },
                { "89", "089" },
                { "0089", "0089" },
                { "", "" },
                { null, null },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderOnkz")
    public void addLeadingZeros(String onkz, String expectedOnkz) throws FindException {
        Assert.assertEquals(testling.addLeadingZeros(onkz), expectedOnkz);
    }

    public void testFindTnbKennung4Onkz() throws FindException {
        String onkz = "12345";

        DnOnkz2Carrier result = new DnOnkz2Carrier();
        result.setOnkz(onkz);
        result.setCarrier(TNB.AKOM.carrierName);

        when(rufnummerDAO.findById(any(String.class), eq(DnOnkz2Carrier.class))).thenReturn(result);

        String tnb = testling.findTnbKennung4Onkz(onkz);
        assertEquals(tnb, TNB.AKOM.tnbKennung);
    }

    public void testFindTnbKennung4OnkzReturnNullOnEmptyResult() throws FindException {
        when(rufnummerDAO.queryByExample(any(DnOnkz2Carrier.class), eq(DnOnkz2Carrier.class))).thenReturn(null);

        assertNull(testling.findTnbKennung4Onkz("12345"));

    }

    @Test
    public void findRNCount_orderHasDialNumbers_ReturnPairs() throws FindException {
        when(rufnummerDAO.findAllRNs4Auftrag(anyLong())).thenReturn(
                Arrays.asList(new Rufnummer(), new Rufnummer(), new Rufnummer(), new Rufnummer()));
        List<Pair<Long, Integer>> result = testling.findRNCount(Arrays.asList(1L, 2L));
        assertTrue(result.size() > 0);
    }

    @Test
    public void findRNCount_noOrderId_ReturnEmptyList() throws FindException {
        List<Pair<Long, Integer>> result = testling.findRNCount(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void findRNCount_InputIsNull_ReturnEmptyList() throws FindException {
        List<Pair<Long, Integer>> result = testling.findRNCount(null);
        assertTrue(result.isEmpty());
    }

}

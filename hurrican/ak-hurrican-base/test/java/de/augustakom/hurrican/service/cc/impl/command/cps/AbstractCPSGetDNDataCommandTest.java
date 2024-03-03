/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2009 15:18:16
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;


/**
 * Test NG class for {@link AbstractCPSGetDNDataCommand}
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class AbstractCPSGetDNDataCommandTest extends AbstractHurricanBaseServiceTest {

    /**
     * Initialize the tests
     *
     * @throws FindException
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {

    }

    /* Erwartet als Result die Rufnummer mit Status 'AKT' */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNByAktOrNewExpectActWithNew() {
        Long orderNoOrig = Long.valueOf(55555);
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig);

        Rufnummer aktDN = rb.withRandomDnNo()
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        Rufnummer newDN = rb.withRandomDnNo()
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(DateTools.changeDate(new Date(), Calendar.DATE, 10))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        List<Rufnummer> filtered = new ArrayList<Rufnummer>();

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDNByAktOrNew(filtered, new Date(), orderNoOrig, newDN, aktDN);
        assertNotEmpty(filtered, "Filter is not valid!");
        assertEquals(filtered.size(), 1);
        assertEquals(filtered.get(0).getDnNo(), aktDN.getDnNo());
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_AKT);
    }

    /* Erwartet als Result die Rufnummer mit Status 'NEU' - keine Angabe von 'AKT' */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNByAktOrNewExpectNew() {
        Long orderNoOrig = Long.valueOf(55555);
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig);

        Rufnummer aktDN = rb.withRandomDnNo()
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        Rufnummer newDN = rb.withRandomDnNo()
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(DateTools.changeDate(new Date(), Calendar.DATE, 10))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        Date execDate = DateTools.changeDate(new Date(), Calendar.DATE, 15);
        List<Rufnummer> filtered = new ArrayList<Rufnummer>();

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDNByAktOrNew(filtered, execDate, orderNoOrig, newDN, aktDN);
        assertNotEmpty(filtered, "Filter is not valid!");
        assertEquals(filtered.size(), 1);
        assertEquals(filtered.get(0).getDnNo(), newDN.getDnNo());
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_NEU);
    }

    /* Erwartet als Result die Rufnummer mit Status 'NEW' - ohne Angabe von 'AKT' */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNByAktOrNewExpectNewWithoutAct() {
        Long orderNoOrig = Long.valueOf(55555);
        Rufnummer newDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig)
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(new Date())
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        Date execDate = DateTools.changeDate(new Date(), Calendar.DATE, 5);
        List<Rufnummer> filtered = new ArrayList<Rufnummer>();

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDNByAktOrNew(filtered, execDate, orderNoOrig, newDN, null);
        assertNotEmpty(filtered, "Filter is not valid!");
        assertEquals(filtered.size(), 1);
        assertEquals(filtered.get(0).getDnNo(), newDN.getDnNo());
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_NEU);
    }

    /* Erwartet als Result KEINE Rufnummer! (z.B. ungueltiges Datum) */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNByAktOrNewExpectNone() {
        Long orderNoOrig = Long.valueOf(55555);
        Rufnummer newDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(new Date())
                .withGueltigBis(DateTools.changeDate(new Date(), Calendar.DATE, 5))
                .build();

        Date execDate = DateTools.changeDate(new Date(), Calendar.DATE, 15);
        List<Rufnummer> filtered = new ArrayList<Rufnummer>();

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDNByAktOrNew(filtered, execDate, orderNoOrig, newDN, null);
        assertEquals(filtered.size(), 0);
    }

    /*
     * Erwartet als Result KEINE Rufnummer - DN mit HistLast=1 ist nicht mehr dem Auftrag zugeordnet.
     * Ausfuehrungszeitpunkt == History-Zeitpunkt
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNExpectNoneBecauseOfHistory() throws FindException {
        Long orderNoOrig = Long.valueOf(55555);
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig()
                .withGueltigBis(DateTools.getBillingEndDate());

        // Rufnummer 'AKT' - ist dem Auftrag zugeordnet
        Rufnummer aktDN = rb.withRandomDnNo()
                .withRandomDnNo()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.FALSE)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .build();
        Map<Long, Rufnummer> aktDNs = new HashMap<Long, Rufnummer>();
        aktDNs.put(aktDN.getDnNoOrig(), aktDN);

        // Rufnummer 'NEU' - keine Auftragszuordnung
        Rufnummer newDN = rb.withRandomDnNo()
                .withRandomDnNo()
                .withAuftragNoOrig(null)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(DateTools.createDate(2009, 8, 30))
                .build();
        Map<Long, Rufnummer> newDNs = new HashMap<Long, Rufnummer>();
        newDNs.put(newDN.getDnNoOrig(), newDN);

        Date execDate = DateTools.createDate(2009, 8, 30);
        Date dateToCheck = DateTools.changeDate(execDate, Calendar.DATE, 1);

        List<Rufnummer> filtered = new ArrayList<Rufnummer>();
        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDN(filtered, aktDN.getDnNoOrig(), execDate, dateToCheck, orderNoOrig, aktDNs, null, newDNs);

        assertEmpty(filtered, "Filter not ok! Result found but not expected!");
    }

    /*
     * Erwartet als Result die Rufnummer mit Hist-Status 'AKT' - keine Angabe von 'NEU'.
     * Ausfuehrungszeitpunkt innerhalb von Gueltigkeits-Datum der Rufnummer.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNExpectActWithoutNew() throws FindException {
        Long orderNoOrig = Long.valueOf(55555);
        // Rufnummer 'AKT' - ist dem Auftrag zugeordnet
        Rufnummer aktDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig()
                .withRandomDnNo()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        Map<Long, Rufnummer> aktDNs = new HashMap<Long, Rufnummer>();
        aktDNs.put(aktDN.getDnNoOrig(), aktDN);

        Date execDate = DateTools.createDate(2009, 8, 30);
        Date dateToCheck = DateTools.changeDate(execDate, Calendar.DATE, 1);

        List<Rufnummer> filtered = new ArrayList<Rufnummer>();
        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDN(filtered, aktDN.getDnNoOrig(), execDate,
                dateToCheck, orderNoOrig, aktDNs, null, new HashMap<Long, Rufnummer>());

        assertNotEmpty(filtered, "Filter not ok! Filtered list is empty but expected was 'aktDN'!");
        assertEquals(filtered.get(0).getDnNo(), aktDN.getDnNo());
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_AKT);
    }

    /*
     * Erwartet als Result die Rufnummer mit Hist-Status 'AKT'.
     * Die 'AKT' Rufnummer ist nicht als HistLast=1 gekennzeichnet. Die HistLast=1
     * wird ueber einen Mock geladen.
     * Der Ausfuehrungszeitpunkt ist innerhalb der Gueltigkeit der AKT Rufnummer.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNExpectActWithHistLast() throws FindException {
        Long orderNoOrig = Long.valueOf(55555);
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig();

        // Rufnummer 'AKT' - ist dem Auftrag zugeordnet
        Rufnummer aktDN = rb
                .withRandomDnNo()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.FALSE)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        Map<Long, Rufnummer> aktDNs = new HashMap<Long, Rufnummer>();
        aktDNs.put(aktDN.getDnNoOrig(), aktDN);

        Rufnummer newDN = rb
                .withRandomDnNo()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(DateTools.createDate(2009, 10, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        Date execDate = DateTools.createDate(2009, 8, 30);
        Date dateToCheck = DateTools.changeDate(execDate, Calendar.DATE, 1);

        RufnummerService rufnummerService = mock(RufnummerService.class);
        when(rufnummerService.findLastRN(aktDN.getDnNoOrig())).thenReturn(newDN);

        List<Rufnummer> filtered = new ArrayList<Rufnummer>();
        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.setRufnummerService(rufnummerService);
        command.filterDN(filtered, aktDN.getDnNoOrig(), execDate,
                dateToCheck, orderNoOrig, aktDNs, null, new HashMap<Long, Rufnummer>());

        assertNotEmpty(filtered, "Filter not ok! Filtered list is empty but expected was 'aktDN'!");
        assertEquals(filtered.get(0).getDnNo(), aktDN.getDnNo());
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_AKT);
    }

    /*
     * Erwartet als Result die Rufnummer mit Hist-Status 'NEU'.
     * Es ist sowohl eine AKT als auch eine NEU Rufnummer angegeben.
     * Der Ausfuehrungszeitpunkt liegt innerhalb des Gueltigkeitszeitraums
     * der NEU Rufnummer.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNExpectNewWithAktDN() throws FindException {
        Long orderNoOrig = Long.valueOf(55555);
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class)
                .withRandomDnNoOrig();

        // Rufnummer 'AKT' - ist dem Auftrag zugeordnet
        Rufnummer aktDN = rb
                .withRandomDnNo()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.FALSE)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        Map<Long, Rufnummer> aktDNs = new HashMap<Long, Rufnummer>();
        aktDNs.put(aktDN.getDnNoOrig(), aktDN);

        Rufnummer newDN = rb
                .withRandomDnNo()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(DateTools.createDate(2009, 8, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        Map<Long, Rufnummer> newDNs = new HashMap<Long, Rufnummer>();
        newDNs.put(aktDN.getDnNoOrig(), newDN);

        Date execDate = DateTools.createDate(2009, 8, 30);
        Date dateToCheck = DateTools.changeDate(execDate, Calendar.DATE, 1);

        List<Rufnummer> filtered = new ArrayList<Rufnummer>();
        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDN(filtered, aktDN.getDnNoOrig(), execDate,
                dateToCheck, orderNoOrig, aktDNs, null, newDNs);

        assertNotEmpty(filtered, "Filter not ok! Filtered list is empty but expected size is 1!");
        assertEquals(filtered.get(0).getDnNo(), newDN.getDnNo(), "Filtered list contains other DN than newDN");
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_NEU);
    }

    /*
     * Erwartet als Result die Rufnummer mit Hist-Status 'NEU'.
     * Es wird keine AKT Rufnummer angegeben.
     * Der Gueltigkeitszeitraum der NEU Rufnummer liegt in der Zukunft; der
     * Ausfuehrungszeitpunkt liegt innerhalb des Gueltigkeitszeitraums.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNExpectNewWithoutAktDN() throws FindException {
        Long orderNoOrig = Long.valueOf(55555);
        Rufnummer newDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNo()
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(DateTools.changeDate(new Date(), Calendar.DATE, 10))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        Map<Long, Rufnummer> newDNs = new HashMap<Long, Rufnummer>();
        newDNs.put(newDN.getDnNoOrig(), newDN);

        Date execDate = DateTools.changeDate(new Date(), Calendar.DATE, 10);
        Date dateToCheck = DateTools.changeDate(execDate, Calendar.DATE, 1);

        List<Rufnummer> filtered = new ArrayList<Rufnummer>();
        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDN(filtered, newDN.getDnNoOrig(), execDate,
                dateToCheck, orderNoOrig, new HashMap<Long, Rufnummer>(), null, newDNs);

        assertNotEmpty(filtered, "Filter not ok! Filtered list is empty but expected size is 1!");
        assertEquals(filtered.get(0).getDnNo(), newDN.getDnNo(), "Filtered list contains other DN than newDN");
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_NEU);
    }

    /*
     * Erwartet als Result die Rufnummer mit Hist-Status 'ALT'.
     * Ausfuehrungszeitpunkt liegt innerhalb der Gueltigkeitsdauer der
     * ALT Rufnummer.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNExpectOld() throws FindException {
        Long orderNoOrig = Long.valueOf(55555);
        Rufnummer oldDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNo()
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_ALT)
                .withGueltigVon(new Date())
                .withGueltigBis(DateTools.changeDate(new Date(), Calendar.DATE, 10))
                .build();
        Map<Long, Rufnummer> oldDNs = new HashMap<Long, Rufnummer>();
        oldDNs.put(oldDN.getDnNoOrig(), oldDN);

        Date execDate = DateTools.changeDate(new Date(), Calendar.DATE, 10);

        List<Rufnummer> filtered = new ArrayList<Rufnummer>();
        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.filterDN(filtered, oldDN.getDnNoOrig(), execDate,
                null, orderNoOrig, new HashMap<Long, Rufnummer>(), oldDNs, new HashMap<Long, Rufnummer>());

        assertNotEmpty(filtered, "Filter not ok! Filtered list is empty but expected size is 1!");
        assertEquals(filtered.get(0).getDnNo(), oldDN.getDnNo(), "Filtered list contains other DN than oldDN");
        assertEquals(filtered.get(0).getHistStatus(), BillingConstants.HIST_STATUS_ALT);
    }

    /*
     * Kombinations-Test fuer 'filterDN' und 'filterDNByAktOrNew'.
     * Testet nur einen einfachen Fall. <br>
     * Die anderen moeglichen Faelle sind ueber die einzelnen Test-Methoden der
     * beiden genannten Methoden aufgenommen.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFilterDNsByExecutionDate() throws FindException {
        Long orderNoOrig = Long.valueOf(55555);
        List<Rufnummer> dnsToFilter = new ArrayList<Rufnummer>();

        // Rufnummer 'AKT' - ist dem Auftrag zugeordnet
        Rufnummer aktDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNo()
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withGueltigVon(DateTools.createDate(2009, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        dnsToFilter.add(aktDN);

        Rufnummer newDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNo()
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_NEU)
                .withGueltigVon(DateTools.changeDate(new Date(), Calendar.DATE, 10))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        dnsToFilter.add(newDN);

        Rufnummer oldDN = getBuilder(RufnummerBuilder.class)
                .withRandomDnNo()
                .withRandomDnNoOrig()
                .withAuftragNoOrig(orderNoOrig)
                .withHistLast(Boolean.TRUE)
                .withHistStatus(BillingConstants.HIST_STATUS_ALT)
                .withGueltigVon(DateTools.changeDate(new Date(), Calendar.DATE, -200))
                .withGueltigBis(DateTools.changeDate(new Date(), Calendar.DATE, -10))
                .build();
        dnsToFilter.add(oldDN);

        Date execDate = new Date();
        Date dateToCheck = DateTools.changeDate(execDate, Calendar.DATE, 1);

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        List<Rufnummer> result = command.filterDNsByExecutionDate(dnsToFilter, orderNoOrig, execDate, dateToCheck);
        assertNotEmpty(result, "Filter is not ok! Result is empty!");
        assertEquals(result.size(), 1, "Result size is not as expected!");
        assertEquals(result.get(0).getDnNo(), aktDN.getDnNo(), "Filtered DN is not as expected! Expected: aktDN");
        assertEquals(result.get(0).getHistStatus(), BillingConstants.HIST_STATUS_AKT);
    }

    @SuppressWarnings("unchecked")
    @Test(groups = BaseTest.SERVICE)
    public void testFilterRoutings() throws FindException, ServiceNotFoundException {
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class);
        Rufnummer routingDN = rb
                .withRandomDnNo()
                .withOeNoOrig(Rufnummer.OE__NO_ROUTING)
                .build();
        Rufnummer noRoutingDN = rb
                .withRandomDnNo()
                .withOeNoOrig(Rufnummer.OE__NO_DEFAULT)
                .build();

        List<Rufnummer> dns = new ArrayList<Rufnummer>();
        dns.add(routingDN);
        dns.add(noRoutingDN);

        CCRufnummernService ccRufnummerService = mock(CCRufnummernService.class);
        when(ccRufnummerService.findLeistung2DN(any(Long.class), any(List.class), any(Date.class))).thenReturn(null);

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.prepare(AbstractCPSGetDNDataCommand.KEY_CPS_TRANSACTION, new CPSTransaction());
        command.setCcRufnummernService(ccRufnummerService);

        List<Rufnummer> result = command.filterRoutings(dns);
        assertNotEmpty(result, "Filter 'routing' not ok!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getOeNoOrig(), Rufnummer.OE__NO_DEFAULT);
    }

    @SuppressWarnings("unchecked")
    @Test(groups = BaseTest.SERVICE)
    public void testFilterKeinAnschluss() throws FindException {
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class);
        Rufnummer defaultDN = rb
                .withRandomDnNo()
                .withOeNoOrig(Rufnummer.OE__NO_DEFAULT)
                .build();
        Rufnummer noLineDN = rb
                .withRandomDnNo()
                .withOeNoOrig(Rufnummer.OE__NO_KEIN_ANSCHLUSS)
                .build();

        List<Rufnummer> dns = new ArrayList<Rufnummer>();
        dns.add(noLineDN);
        dns.add(defaultDN);

        CCRufnummernService ccRufnummerService = mock(CCRufnummernService.class);
        when(ccRufnummerService.findLeistung2DN(any(Long.class), any(List.class), any(Date.class))).thenReturn(null);

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.prepare(AbstractCPSGetDNDataCommand.KEY_CPS_TRANSACTION, new CPSTransaction());
        command.setCcRufnummernService(ccRufnummerService);

        List<Rufnummer> result = command.filterKeinAnschluss(dns);
        assertNotEmpty(result, "Filter 'kein Anschluss' not ok!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getOeNoOrig(), Rufnummer.OE__NO_DEFAULT);
    }

    @SuppressWarnings("unchecked")
    @Test(groups = BaseTest.SERVICE)
    public void testFilterAGRU() throws FindException, ServiceNotFoundException {
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class);
        Rufnummer agruDN = rb
                .withRandomDnNo()
                .build();
        Rufnummer noAgruDN = rb
                .withRandomDnNo()
                .build();

        List<Rufnummer> dns = new ArrayList<Rufnummer>();
        dns.add(agruDN);
        dns.add(noAgruDN);

        Leistung2DN agruService = new Leistung2DN();
        List<Leistung2DN> agruServices = new ArrayList<Leistung2DN>();
        agruServices.add(agruService);

        CCRufnummernService ccRufnummerService = mock(CCRufnummernService.class);
        when(ccRufnummerService.findLeistung2DN(eq(agruDN.getDnNo()), any(List.class), any(Date.class))).thenReturn(agruServices);
        when(ccRufnummerService.findLeistung2DN(eq(noAgruDN.getDnNo()), any(List.class), any(Date.class))).thenReturn(null);

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.prepare(AbstractCPSGetDNDataCommand.KEY_CPS_TRANSACTION, new CPSTransaction());
        command.setCcRufnummernService(ccRufnummerService);

        List<Rufnummer> result = command.filterAGRU(dns);
        assertNotEmpty(result, "Filter 'AGRU' not ok!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getDnNo(), noAgruDN.getDnNo());
    }

    @Test(groups = BaseTest.SERVICE)
    public void testGetLastActiveDNsForOrder() throws FindException {
        RufnummerBuilder rb = getBuilder(RufnummerBuilder.class);
        Rufnummer dnOld = rb
                .withRandomDnNo()
                .withOnKz("0821")
                .withDnBase("123456")
                .withGueltigBis(DateTools.changeDate(new Date(), Calendar.DATE, -10))
                .build();

        String dnBaseOfLastDN = "456789";
        Rufnummer dnNotSoOld = rb
                .withRandomDnNo()
                .withOnKz("0821")
                .withDnBase(dnBaseOfLastDN)
                .withGueltigBis(DateTools.changeDate(new Date(), Calendar.DATE, -5))
                .build();

        List<Rufnummer> dns = new ArrayList<Rufnummer>();
        dns.add(dnOld);
        dns.add(dnNotSoOld);

        RufnummerService rufnummerService = mock(RufnummerService.class);
        when(rufnummerService.findAllRNs4Auftrag(any(Long.class))).thenReturn(dns);

        AbstractCPSGetDNDataCommandUnitTestImpl command = new AbstractCPSGetDNDataCommandUnitTestImpl();
        command.prepare(AbstractCPSGetDNDataCommand.KEY_CPS_TRANSACTION, new CPSTransaction());
        command.setRufnummerService(rufnummerService);

        List<Rufnummer> filteredDNs = command.getLastActiveDNsForOrder(Long.valueOf(123));
        assertNotEmpty(filteredDNs, "Es wurden keine Rufnummern ermittelt!");
        assertEquals(filteredDNs.size(), 1, "Anzahl der zuletzt gueltigen Rufnummern des Auftrags nicht i.O.");
        assertEquals(filteredDNs.get(0).getDnBase(), dnBaseOfLastDN);
    }

    /* Dummy-Implementierung fuer Unit-Tests von AbstractCPSGetDNDataCommand */
    class AbstractCPSGetDNDataCommandUnitTestImpl extends AbstractCPSGetDNDataCommand {
        @Override
        public Object execute() throws Exception {
            return null;
        }

        /**
         * @see de.augustakom.hurrican.service.cc.impl.command.cps.AbstractCPSGetDNDataCommand#init()
         */
        @Override
        public void init() throws ServiceNotFoundException {
            super.init();
        }
    }
}



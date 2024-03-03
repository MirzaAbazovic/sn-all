/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2009 08:54:18
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Leistung2DNBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSDNPortation;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.RufnummerService;


/**
 * Unit Test for {@link CPSGetDNPortationGoingDataCommandTest}
 *
 *
 */
public class CPSGetDNPortationGoingDataCommandTest extends AbstractHurricanBaseServiceTest {

    private static final int EXPECTED_RESULT_SIZE = 4;

    private Date execDate = new Date();
    private Date portationDate = DateTools.changeDate(execDate, Calendar.DATE, 1);

    private Long orderNo = Long.valueOf(789456);
    private Long differentOrderNo = Long.valueOf(789457);

    private static final String ONKZ = "0821";
    private static final Long AGRU_DN_NO_ORIG = Long.valueOf(1);
    private static final String AGRU_DN_BASE = "789456123";
    private static final String AGRU_DESTINATION = "082199999999";
    private static final Long ROUTING_DN_NO_ORIG = Long.valueOf(2);
    private static final String ROUTING_DN_BASE = "11223344";
    private static final String ROUTING_DESTINATION = "Routing auf 0821 / 4500-0 xxx";
    private static final String ROUTING_DESTINATION_RESULT = "082145000";
    private static final Long ROUTING_HISTORY_DN_NO_ORIG = Long.valueOf(3);
    private static final String ROUTING_HISTORY_DN_BASE = "554466";
    private static final Long ROUTING_HISTORY_DN_NO_ORIG_DIFFERENT_ORDER = Long.valueOf(4);
    private static final String ROUTING_HISTORY_DN_BASE_DIFFERENT_ORDER = "668844";
    private static final String ROUTING_HISTORY_DESTINATION = "Routing auf 0821/45555 (test 0)";
    private static final String ROUTING_HISTORY_DESTINATION_RESULT = "082145555";
    private static final Long GOING_DN_NO_ORIG = Long.valueOf(4);
    private static final String GOING_DN_BASE = "665544";
    private static final String GOING_FUTURE_CARRIER = "ARCOR";
    private static final String GOING_PORT_KENNUNG = TNB.NEFKOM.tnbKennung;

    private RufnummerService rufnummerService;

    private CPSTransaction cpsTx;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws Exception {
        rufnummerService = mock(RufnummerService.class);

        // 'normale' Rufnummer erstellen (kein Routing/AGRU etc.)
        // (sollte nicht im Result enthalten sein!)
        Rufnummer defaultDN = new RufnummerBuilder()
                .withRandomDnNo()
                .withDnNoOrig(Long.valueOf(0))
                .withOnKz("0821")
                .withDnBase("11111111")
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withHistLast(Boolean.TRUE)
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        when(rufnummerService.findDNsByDnNoOrig(defaultDN.getDnNoOrig(), portationDate)).thenReturn(Arrays.asList(defaultDN));

        // ROUTING Rufnummer erstellen
        Rufnummer routingDN = new RufnummerBuilder()
                .withRandomDnNo()
                .withAuftragNoOrig(orderNo)
                .withDnNoOrig(ROUTING_DN_NO_ORIG)
                .withOnKz(ONKZ)
                .withDnBase(ROUTING_DN_BASE)
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withRemarks(ROUTING_DESTINATION)
                .withOeNoOrig(Rufnummer.OE__NO_ROUTING)
                .withHistLast(Boolean.TRUE)
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        when(rufnummerService.findDNsByDnNoOrig(routingDN.getDnNoOrig(), portationDate)).thenReturn(Arrays.asList(routingDN));

        // ROUTING Rufnummer mit History
        Rufnummer routingHistOldDN = new RufnummerBuilder()
                .withRandomDnNo()
                .withAuftragNoOrig(orderNo)
                .withDnNoOrig(ROUTING_HISTORY_DN_NO_ORIG)
                .withOnKz(ONKZ)
                .withDnBase(ROUTING_HISTORY_DN_BASE)
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withHistLast(Boolean.FALSE)
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        Rufnummer routingHistActDN = new RufnummerBuilder()
                .withRandomDnNo()
                .withAuftragNoOrig(orderNo)
                .withDnNoOrig(ROUTING_HISTORY_DN_NO_ORIG)
                .withOnKz(ONKZ)
                .withDnBase(ROUTING_HISTORY_DN_BASE)
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withRemarks(ROUTING_HISTORY_DESTINATION)
                .withOeNoOrig(Rufnummer.OE__NO_ROUTING)
                .withHistLast(Boolean.TRUE)
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        when(rufnummerService.findDNsByDnNoOrig(routingHistActDN.getDnNoOrig(), portationDate))
                .thenReturn(Arrays.asList(routingHistOldDN, routingHistActDN));

        // ROUTING Rufnummer mit History (Last DN auf anderem Taifun Auftrag)
        Rufnummer routingHistOldDNActOrder = new RufnummerBuilder()
                .withRandomDnNo()
                .withAuftragNoOrig(orderNo)
                .withDnNoOrig(ROUTING_HISTORY_DN_NO_ORIG_DIFFERENT_ORDER)
                .withOnKz(ONKZ)
                .withDnBase(ROUTING_HISTORY_DN_BASE_DIFFERENT_ORDER)
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withHistLast(Boolean.FALSE)
                .withGueltigBis(new Date())
                .build();
        Rufnummer routingHistActDNDifferentOrder = new RufnummerBuilder()
                .withRandomDnNo()
                .withAuftragNoOrig(differentOrderNo)
                .withDnNoOrig(ROUTING_HISTORY_DN_NO_ORIG_DIFFERENT_ORDER)
                .withOnKz(ONKZ)
                .withDnBase(ROUTING_HISTORY_DN_BASE_DIFFERENT_ORDER)
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withRemarks(ROUTING_HISTORY_DESTINATION)
                .withOeNoOrig(Rufnummer.OE__NO_ROUTING)
                .withHistLast(Boolean.TRUE)
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();
        when(rufnummerService.findDNsByDnNoOrig(routingHistOldDNActOrder.getDnNoOrig(), portationDate))
                .thenReturn(Arrays.asList(routingHistOldDNActOrder, routingHistActDNDifferentOrder));

        List<Rufnummer> rufnummern = new ArrayList<Rufnummer>();
        rufnummern.add(defaultDN);
        rufnummern.add(routingDN);
        rufnummern.add(routingHistOldDN);
        rufnummern.add(buildDNWithAGRU());
        rufnummern.add(buildGoingPortation());
        rufnummern.add(routingHistOldDNActOrder);

        when(rufnummerService.findAllRNs4Auftrag(orderNo)).thenReturn(rufnummern);
        when(rufnummerService.findLastRN(routingHistActDN.getDnNoOrig())).thenReturn(routingHistActDN);
        when(rufnummerService.findLastRN(routingHistOldDNActOrder.getDnNoOrig())).thenReturn(routingHistActDNDifferentOrder);
    }

    /* Erstellt eine Rufnummer mit AGRU Leistung */
    private Rufnummer buildDNWithAGRU() throws FindException {
        Rufnummer agruDN = new RufnummerBuilder()
                .withRandomDnNo()
                .withAuftragNoOrig(orderNo)
                .withDnNoOrig(AGRU_DN_NO_ORIG)
                .withOnKz(ONKZ)
                .withDnBase(AGRU_DN_BASE)
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withHistLast(Boolean.TRUE)
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        when(rufnummerService.findDNsByDnNoOrig(agruDN.getDnNoOrig(), portationDate)).thenReturn(Arrays.asList(agruDN));

        // AGRU Leistung eintragen
        getBuilder(Leistung2DNBuilder.class)
                .withDnNo(agruDN.getDnNo())
                .withLeistung4DnId(Leistung4Dn.DN_SERVICE_AGRU)
                .withLeistungParameter(AGRU_DESTINATION)
                .withScvRealisierung(DateTools.changeDate(new Date(), Calendar.DATE, -1))
                .build();

        return agruDN;
    }

    private Rufnummer buildGoingPortation() throws FindException {
        Rufnummer goingDN = new RufnummerBuilder()
                .withRandomDnNo()
                .withDnNoOrig(GOING_DN_NO_ORIG)
                .withHistLast(Boolean.TRUE)
                .withOnKz(ONKZ)
                .withDnBase(GOING_DN_BASE)
                .withFutureCarrier(GOING_FUTURE_CARRIER, GOING_PORT_KENNUNG)
                .withPortMode(Rufnummer.PORT_MODE_ABGEHEND)
                .withRealDate(new Date())
                .build();

        when(rufnummerService.findDNsByDnNoOrig(goingDN.getDnNoOrig(), portationDate)).thenReturn(Arrays.asList(goingDN));

        return goingDN;
    }

    @Test(groups = BaseTest.SERVICE)
    public void testExecute() throws Exception {
        cpsTx = getBuilder(CPSTransactionBuilder.class)
                .withEstimatedExecTime(execDate)
                .withOrderNoOrig(orderNo)
                .build();

        CPSServiceOrderData cpsServiceOrderData = new CPSServiceOrderData();
        CPSGetDNPortationGoingDataCommand command = (CPSGetDNPortationGoingDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDNPortationGoingDataCommand");
        command.setRufnummerService(rufnummerService);
        command.prepare(AbstractCPSDataCommand.KEY_CPS_TRANSACTION, cpsTx);
        command.prepare(AbstractCPSDataCommand.KEY_SERVICE_ORDER_DATA, cpsServiceOrderData);

        Object result = command.execute();
        assertTrue((result instanceof ServiceCommandResult), "Command returned other object as expected!");
        assertNotEmpty(cpsServiceOrderData.getDnPortations(), "No portations found!");

        List<CPSDNPortation> dnPortations = cpsServiceOrderData.getDnPortations();
        assertEquals(dnPortations.size(), EXPECTED_RESULT_SIZE, "Count of DNs for portation is unexpected!");

        for (CPSDNPortation dnPortation : dnPortations) {
            if (StringUtils.equals(dnPortation.getDn(), ROUTING_DN_BASE)) {
                assertEquals(dnPortation.getTarget(), ROUTING_DESTINATION_RESULT);
                assertEquals(dnPortation.getPortationType(), CPSDNPortation.PTYPE_PORTATION_RUW);
            }
            if (StringUtils.equals(dnPortation.getDn(), ROUTING_HISTORY_DN_BASE)) {
                assertEquals(dnPortation.getTarget(), ROUTING_HISTORY_DESTINATION_RESULT);
                assertEquals(dnPortation.getPortationType(), CPSDNPortation.PTYPE_PORTATION_RUW);
            }
            if (StringUtils.equals(dnPortation.getDn(), AGRU_DN_BASE)) {
                assertEquals(dnPortation.getTarget(), AGRU_DESTINATION);
                assertEquals(dnPortation.getPortationType(), CPSDNPortation.PTYPE_PORTATION_AGRU);
            }
            if (StringUtils.equals(dnPortation.getDn(), GOING_DN_BASE)) {
                assertEquals(dnPortation.getTarget(), GOING_PORT_KENNUNG);
                assertEquals(dnPortation.getPortationType(), CPSDNPortation.PTYPE_PORTATION_GOING);
            }
            if (StringUtils.equals(dnPortation.getDn(), ROUTING_HISTORY_DN_BASE_DIFFERENT_ORDER)) {
                assertTrue(false, "DN should not be in portation block: " + ROUTING_HISTORY_DN_BASE_DIFFERENT_ORDER);
            }
        }
    }

    @Test(groups = BaseTest.SERVICE)
    public void testGetLatestDnByPortationDate() throws FindException, HurricanServiceCommandException {
        Rufnummer dnOld = new RufnummerBuilder()
                .withRandomDnNo()
                .withDnNoOrig(Long.valueOf(0))
                .withOnKz("0821")
                .withDnBase("123456987")
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withHistLast(Boolean.FALSE)
                .withGueltigVon(DateTools.createDate(2010, 0, 1))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        Rufnummer dnNew = new RufnummerBuilder()
                .withRandomDnNo()
                .withDnNoOrig(Long.valueOf(0))
                .withOnKz("0821")
                .withDnBase("123456987")
                .withActCarrier(TNB.MNET.carrierName, TNB.MNET.tnbKennung)
                .withHistLast(Boolean.TRUE)
                .withGueltigVon(DateTools.createDate(2010, 0, 15))
                .withGueltigBis(DateTools.getBillingEndDate())
                .build();

        List<Rufnummer> dns = Arrays.asList(dnOld, dnNew);

        CPSGetDNPortationGoingDataCommand command = (CPSGetDNPortationGoingDataCommand)
                getBean("de.augustakom.hurrican.service.cc.impl.command.cps.CPSGetDNPortationGoingDataCommand");
        command.setRufnummerService(rufnummerService);

        when(rufnummerService.findDNsByDnNoOrig(dnNew.getDnNoOrig(), dnNew.getGueltigVon())).thenReturn(dns);

        Rufnummer result = command.getLatestDnByPortationDate(dnOld, dnNew.getGueltigVon());
        assertNotNull(result);
        assertEquals(result.getDnNo(), dnNew.getDnNo());
    }
}

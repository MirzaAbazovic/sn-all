/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2010 12:57:37
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionSubOrder;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * TestNG Klasse fuer Unit-Tests von {@link CreateCPSTxCommand}
 */
@Test(groups = { BaseTest.UNIT })
public class CreateCPSTxCommandUnitTest extends BaseTest {

    private CreateCPSTxCommand cut;
    private CPSTransaction cpsTx;
    private List<CPSTransactionSubOrder> subOrders;
    private Produkt produkt;
    private AuftragDaten auftragDaten;

    private CCAuftragService auftragServiceMock;
    private CPSService cpsServiceMock;
    private ProduktService produktServiceMock;
    private CCLeistungsService ccLeistungsServiceMock;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = new CreateCPSTxCommand();

        ProduktBuilder produktBuilder = new ProduktBuilder().withCpsMultiDraht(Boolean.TRUE).setPersist(false);
        produkt = produktBuilder.build();

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId().setPersist(false);
        auftragDaten = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB)
                .withAuftragBuilder(auftragBuilder).withProdBuilder(produktBuilder).setPersist(false).build();
        cut.setAuftragDaten(auftragDaten);

        CPSTransactionSubOrder subOrder = new CPSTransactionSubOrder();
        subOrder.setAuftragId(auftragDaten.getAuftragId());

        subOrders = new ArrayList<>();
        subOrders.add(subOrder);

        List<CPSTransaction> successfulCpsTx = new ArrayList<CPSTransaction>();
        CPSTransaction tx1 = new CPSTransaction();
        tx1.setId(Long.valueOf(1L)); // Bereits 'alte' erfolgreiche Tx
        CPSTransaction tx2 = new CPSTransaction();
        tx2.setId(Long.valueOf(2L)); // Neue bereits persistierte Tx tx2.id == cpsTx.id!
        successfulCpsTx.add(tx1);
        successfulCpsTx.add(tx2);

        cpsServiceMock = mock(CPSService.class);
        when(cpsServiceMock.findSuccessfulCPSTransaction4TechOrder(auftragDaten.getAuftragId())).thenReturn(
                successfulCpsTx);

        auftragServiceMock = mock(CCAuftragService.class);
        when(auftragServiceMock.findAuftragDatenByAuftragId(auftragDaten.getAuftragId())).thenReturn(auftragDaten);

        produktServiceMock = mock(ProduktService.class);
        ccLeistungsServiceMock = mock(CCLeistungsService.class);

        cpsTx = new CPSTransactionBuilder().withAuftragBuilder(auftragBuilder).withEstimatedExecTime(new Date())
                .setPersist(false).withId(2L).build();
        cut.setCpsTx(cpsTx);
        cut.setCpsService(cpsServiceMock);
        cut.setCcAuftragService(auftragServiceMock);
        cut.setProduktService(produktServiceMock);
        cut.setCcLeistungsService(ccLeistungsServiceMock);
    }

    @DataProvider
    public Object[][] dataProviderCheckAndModify() {
        return new Object[][] {
                // soTypeIn, soTypeOut, forceExecType, useSubOrders
                { CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, false,
                        true },
                { CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB, true,
                        true },
                { CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, false,
                        true },
                { CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB, CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB, true,
                        true },
                { CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, false,
                        false },
                { CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB, true,
                        false }, };
    }

    @Test(dataProvider = "dataProviderCheckAndModify")
    public void testCheckAndModifyCPSServiceOrderType(Long soTypeIn, Long soTypeOut, Boolean forceExecType,
            Boolean useSubOrders) throws FindException, ServiceCommandException {
        cpsTx.setServiceOrderType(soTypeIn);
        cut.setForceExecType(forceExecType);

        cut.checkAndModifyCPSServiceOrderType((BooleanTools.nullToFalse(useSubOrders)) ? subOrders : null);
        assertEquals(cpsTx.getServiceOrderType(), soTypeOut);
    }

    public void testIsMultiDraht() throws FindException, ServiceNotFoundException {
        when(produktServiceMock.findProdukt4Auftrag(auftragDaten.getAuftragId())).thenReturn(produkt);

        Pair<Boolean, Boolean> result = cut.isMultiDraht(null);
        assertTrue(result.getFirst());
        assertTrue(result.getSecond());
    }

    public void testIsMultiDrahtOnTk() throws FindException, ServiceNotFoundException {
        List<Long> auftragIds = Arrays.asList(auftragDaten.getAuftragId());

        when(produktServiceMock.findProdukt4Auftrag(any(Long.class))).thenReturn(null);
        when(
                ccLeistungsServiceMock.isTechLeistungActive(auftragIds, ExterneLeistung.ISDN_TYP_TK.leistungNo,
                        cpsTx.getEstimatedExecTime())
        ).thenReturn(true);

        Pair<Boolean, Boolean> result = cut.isMultiDraht(Arrays.asList(auftragDaten));
        assertTrue(result.getFirst());
        assertFalse(result.getSecond());
    }

    public void testIsOrderStateValid4CpsBundleInBetrieb() {
        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false)
                .build();
        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, null, false);
        assertTrue(addBundledOrder);
    }

    public void testIsOrderStateValid4CpsBundleCancelWithActVerlauf() {
        Verlauf actVerlauf = new VerlaufBuilder()
                .withRealisierungstermin(DateTools.changeDate(cpsTx.getEstimatedExecTime(), Calendar.DATE, -1))
                .setPersist(false).build();

        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false)
                .build();
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);

        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, actVerlauf, false);
        assertFalse(addBundledOrder);
    }

    public void testIsOrderStateValid4CpsBundleInBetriebNDrahtProdukt() {
        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false)
                .build();
        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, null, true);
        assertTrue(addBundledOrder);
    }

    public void testIsOrderStateValid4CpsBundleCancelSubInBetriebNDrahtProdukt() {
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);
        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false)
                .build();
        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, null, true);
        assertFalse(addBundledOrder);
    }

    public void testIsOrderStateValid4CpsBundleInKuendigungNDrahtProdukt() {
        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.KUENDIGUNG_TECHN_REAL)
                .setPersist(false).build();
        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, null, true);
        assertFalse(addBundledOrder);
    }

    public void testIsOrderStateValid4CpsBundleCancelTxInKuendigungNDrahtProdukt() {
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);
        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.KUENDIGUNG_TECHN_REAL)
                .setPersist(false).build();
        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, null, true);
        assertTrue(addBundledOrder);
    }

    public void testIsOrderStateValid4CpsBundleInitialLoad() {
        auftragDaten.setKuendigung(new Date());
        cut.setAuftragDaten(auftragDaten);

        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withKuendigung(auftragDaten.getKuendigung()).setPersist(false).build();
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);

        cut.prepare(CreateCPSTxCommand.KEY_LAZY_INIT_MODE, LazyInitMode.initialLoad);

        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, null, false);
        assertTrue(addBundledOrder);
    }

    public void testIsOrderStateValid4CpsBundleInitialLoadNoValidDate() {
        auftragDaten.setKuendigung(new Date());
        cut.setAuftragDaten(auftragDaten);

        AuftragDaten bundledOrder = new AuftragDatenBuilder().withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withKuendigung(DateTools.changeDate(auftragDaten.getKuendigung(), Calendar.DATE, -1))
                .setPersist(false).build();
        cpsTx.setServiceOrderType(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB);

        cut.prepare(CreateCPSTxCommand.KEY_LAZY_INIT_MODE, LazyInitMode.initialLoad);

        boolean addBundledOrder = cut.isOrderStateValid4CpsBundle(bundledOrder, null, false);
        assertFalse(addBundledOrder);
    }

}

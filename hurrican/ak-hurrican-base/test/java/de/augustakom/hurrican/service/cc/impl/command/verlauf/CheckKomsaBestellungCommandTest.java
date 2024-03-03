/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2012 10:54:49
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.model.billing.PurchaseOrderBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.service.billing.PurchaseOrderService;

@Test(groups = { BaseTest.UNIT })
public class CheckKomsaBestellungCommandTest extends BaseTest {

    @InjectMocks
    @Spy
    private CheckKomsaBestellungCommand cut;

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @BeforeMethod
    public void setUp() throws ServiceNotFoundException {
        cut = new CheckKomsaBestellungCommand();
        cut.prepare(CheckKomsaBestellungCommand.KEY_ANLASS_ID, BAVerlaufAnlass.NEUSCHALTUNG);
        MockitoAnnotations.initMocks(this);

        BAuftrag billingOrder = new BAuftragBuilder().withAuftragNoOrig(Long.valueOf(123)).build();
        doReturn(billingOrder).when(cut).getBillingAuftrag();
    }


    public void testCheckKomsaWithNullAsPurchaseOrders() throws Exception {
        when(purchaseOrderService.findPurchaseOrders4Auftrag(anyLong())).thenReturn(null);
        Object result = cut.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertEquals(((ServiceCommandResult) result).getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
    }

    public void testCheckKomsaWithEmptyPurchaseOrders() throws Exception {
        List<PurchaseOrder> emptyList = Collections.emptyList();
        when(purchaseOrderService.findPurchaseOrders4Auftrag(anyLong())).thenReturn(emptyList);
        Object result = cut.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertEquals(((ServiceCommandResult) result).getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
    }

    public void testCheckKomsaWithAllPurchaseOrdersActivated() throws Exception {
        PurchaseOrder purchaseOrder1 = new PurchaseOrderBuilder().withStatus("new").build();
        PurchaseOrder purchaseOrder2 = new PurchaseOrderBuilder().withStatus("new").build();
        when(purchaseOrderService.findPurchaseOrders4Auftrag(anyLong())).thenReturn(Arrays.asList(purchaseOrder1, purchaseOrder2));
        Object result = cut.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertEquals(((ServiceCommandResult) result).getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
    }

    public void testCheckKomsaWithAtLeastOnePurchaseOrdersNotActivated() throws Exception {
        PurchaseOrder purchaseOrder1 = new PurchaseOrderBuilder().withStatusPlanned().build();
        PurchaseOrder purchaseOrder2 = new PurchaseOrderBuilder().withStatusPlanned().build();
        when(purchaseOrderService.findPurchaseOrders4Auftrag(anyLong())).thenReturn(Arrays.asList(purchaseOrder1, purchaseOrder2));
        Object result = cut.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertEquals(((ServiceCommandResult) result).getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
        AKWarnings warnings = cut.getWarnings();
        assertTrue(warnings != null);
        assertTrue(warnings.isNotEmpty());
    }

    public void testCheckWithInactivePurchaseOrdersButNotKomsa() throws Exception {
        PurchaseOrder purchaseOrderNotKomsa = new PurchaseOrderBuilder().withSupplier(Long.valueOf(999)).withStatusPlanned().build();
        when(purchaseOrderService.findPurchaseOrders4Auftrag(anyLong())).thenReturn(Arrays.asList(purchaseOrderNotKomsa));
        Object result = cut.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertEquals(((ServiceCommandResult) result).getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
    }

    @DataProvider(name = "executeKomsaCheckDataProvider")
    public Object[][] executeKomsaCheckDataProvider() {
        // @formatter:off
        return new Object[][]{
                { BAVerlaufAnlass.NEUSCHALTUNG          , true },
                { BAVerlaufAnlass.ABW_TKG46_AENDERUNG   , true },
                { BAVerlaufAnlass.ABW_TKG46_NEUSCHALTUNG, true },
                { BAVerlaufAnlass.AENDERUNG_BANDBREITE  , true },
                { BAVerlaufAnlass.ANSCHLUSSUEBERNAHME   , true },
                { BAVerlaufAnlass.DSL_KREUZUNG          , false },
                { BAVerlaufAnlass.BANDBREITENAENDERUNG  , false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "executeKomsaCheckDataProvider")
    public void testExecuteKomsaCheck(Long anlassId, boolean expectedValue) {
        CheckKomsaBestellungCommand cmd = new CheckKomsaBestellungCommand();
        cmd.prepare(CheckKomsaBestellungCommand.KEY_ANLASS_ID, anlassId);
        assertEquals(cmd.executeKomsaCheck(), expectedValue);
    }

}



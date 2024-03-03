/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH

 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2013 10:58:54
 */
package de.augustakom.hurrican.service.billing.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.billing.PurchaseOrderDAO;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.PurchaseOrder;
import de.augustakom.hurrican.model.billing.PurchaseOrderBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Testklasse fuer PurchaseOrderServiceImpl
 */
@Test(groups = { BaseTest.UNIT })
public class PurchaseOrderServiceImplTest extends BaseTest {

    @Spy
    private PurchaseOrderServiceImpl sut;

    @Mock
    private PurchaseOrderDAO purchaseOrderDaoMock;

    @BeforeMethod
    void setUp() {
        sut = new PurchaseOrderServiceImpl();
        initMocks(this);
        sut.setDAO(purchaseOrderDaoMock);
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = FindException.class)
    public void testFindPurchaseOrders4AuftragWithException() throws FindException {
        when(purchaseOrderDaoMock.queryByExample(any(PurchaseOrder.class), any(Class.class))).thenThrow(new RuntimeException());

        sut.findPurchaseOrders4Auftrag(Long.valueOf(1));
    }

    @DataProvider(name = "findPurchaseOrders4AuftragDataProvider")
    public Object[][] findPurchaseOrders4AuftragDataProvider() {
        Long auftragNoOrig = Long.valueOf(1);
        BAuftrag billingAuftrag = new BAuftragBuilder().withAuftragNoOrig(auftragNoOrig).setPersist(false).build();
        PurchaseOrder purchaseOrder = new PurchaseOrderBuilder().withServiceNoOrig(billingAuftrag.getAuftragNoOrig())
                .withStatusPlanned().setPersist(false).build();
        List<PurchaseOrder> purchaseOrderList = new LinkedList<PurchaseOrder>();
        purchaseOrderList.add(purchaseOrder);

        // @formatter:off
        return new Object[][] {
                { null,             null },
                { auftragNoOrig,    purchaseOrderList }
        };
        // @formatter:on
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "findPurchaseOrders4AuftragDataProvider")
    public void testFindPurchaseOrders4Auftrag(Long auftragNoOrig, List<PurchaseOrder> purchaseOrderList) throws FindException {
        when(purchaseOrderDaoMock.queryByExample(any(PurchaseOrder.class), any(Class.class))).thenReturn(purchaseOrderList);

        List<PurchaseOrder> res = sut.findPurchaseOrders4Auftrag(auftragNoOrig);
        assertEquals(res, purchaseOrderList);
    }
}

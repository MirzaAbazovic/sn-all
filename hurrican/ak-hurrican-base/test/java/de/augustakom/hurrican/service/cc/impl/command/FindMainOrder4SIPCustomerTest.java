/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 09:24:54
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;


/**
 * TestNG Klasse fuer {@link FindMainOrder4SIPCustomer}
 */
@Test(groups = { "unit" })
public class FindMainOrder4SIPCustomerTest extends BaseTest {

    private Kunde kunde;
    private FindMainOrder4SIPCustomer cut;
    private KundenService kundenServiceMock;
    private CCKundenService ccKundenServiceMock;
    private CCAuftragService ccAuftragServiceMock;

    @BeforeMethod
    public void setUp() {
        cut = new FindMainOrder4SIPCustomer();

        kunde = new KundeBuilder()
                .withKundeNo(Long.valueOf(Long.MAX_VALUE))
                .withHauptKundeNo(Long.valueOf(Long.MAX_VALUE - 1))
                .setPersist(false)
                .build();

        kundenServiceMock = mock(KundenService.class);
        cut.setBillingKundenService(kundenServiceMock);

        ccKundenServiceMock = mock(CCKundenService.class);
        cut.setCcKundenService(ccKundenServiceMock);

        ccAuftragServiceMock = mock(CCAuftragService.class);
        cut.setCcAuftragService(ccAuftragServiceMock);
    }

    public void testFindHauptKundenNo() throws FindException, HurricanServiceCommandException, ServiceNotFoundException {
        when(kundenServiceMock.findKunde(kunde.getKundeNo())).thenReturn(kunde);

        Long result = cut.findHauptKundenNo(kunde.getKundeNo());
        assertEquals(result, kunde.getHauptKundenNo());
    }

    @Test(expectedExceptions = HurricanServiceCommandException.class)
    public void testFindHauptKundenNoExpectErrorBecauseIsHauptkunde() throws FindException, HurricanServiceCommandException, ServiceNotFoundException {
        kunde.setHauptkunde(Boolean.TRUE);
        when(kundenServiceMock.findKunde(kunde.getKundeNo())).thenReturn(kunde);

        cut.findHauptKundenNo(kunde.getKundeNo());
    }

    public void testFindMainOrder() throws FindException, ServiceCommandException, ServiceNotFoundException {
        List<Long> prodGroup = new ArrayList<Long>();
        prodGroup.add(ProduktGruppe.SIP_INTER_TRUNK);

        List<Long> auftragIds = new ArrayList<Long>();
        auftragIds.add(Long.MAX_VALUE);

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .setPersist(false).build();

        when(ccKundenServiceMock.findActiveAuftragInProdGruppe(kunde.getHauptKundenNo(), prodGroup)).thenReturn(auftragIds);
        when(ccAuftragServiceMock.findAuftragDatenByAuftragIdTx(auftragIds.get(0))).thenReturn(auftragDaten);

        Object result = cut.findMainOrder(kunde.getHauptKundenNo());
        assertEquals(result, auftragDaten);
    }

}



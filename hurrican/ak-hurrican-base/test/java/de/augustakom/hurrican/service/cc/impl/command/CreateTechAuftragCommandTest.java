/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.2012 15:58:24
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Testklasse fuer {@link CreateTechAuftragCommand}
 */
@Test(groups = { BaseTest.UNIT })
public class CreateTechAuftragCommandTest extends BaseTest {

    @Spy
    private CreateTechAuftragCommand cut = new CreateTechAuftragCommand();
    @Mock
    private BillingAuftragService bas;
    @Mock
    private ProduktService ps;
    @Mock
    private CCAuftragService cas;
    @Mock
    private NiederlassungService ns;
    @Mock
    private AccountService as;
    @Captor
    private ArgumentCaptor<AuftragDaten> auftragDatenCaptor;

    @BeforeMethod
    void setup() {
        initMocks(this);
    }

    @DataProvider
    private Object[][] checkAuftragDatenCreationDataProvider() {
        return new Object[][] {
                { Produkt.PROD_ID_SDSL_1024, null, AuftragStatus.AUS_TAIFUN_UEBERNOMMEN },
                { Produkt.PROD_ID_SDSL_1024, AuftragStatus.ANSCHREIBEN_KUNDE_KUEND, AuftragStatus.ANSCHREIBEN_KUNDE_KUEND },
                { Produkt.PROD_ID_TV_SIGNALLIEFERUNG, null, AuftragStatus.AUS_TAIFUN_UEBERNOMMEN },
                { Produkt.PROD_ID_TV_SIGNALLIEFERUNG, AuftragStatus.AUFTRAG_GEKUENDIGT, AuftragStatus.AUFTRAG_GEKUENDIGT },
                { Produkt.PROD_ID_TV_SIGNALLIEFERUNG, AuftragStatus.IN_BETRIEB, AuftragStatus.IN_BETRIEB },
        };
    }

    @Test(dataProvider = "checkAuftragDatenCreationDataProvider")
    public void execute_CheckAuftragDatenCreation(Long produktId, Long erstellStatusId, Long expectedStatusId) throws Exception {
        final Long auftragNo = 42L;
        final Date gueltigVon = new Date(), vertragsDatum = new Date();
        //@formatter:off
        final BAuftrag ba = new BAuftragBuilder()
                            .setPersist(false)
                            .withGueltigVon(gueltigVon)
                            .withVertragsdatum(vertragsDatum)
                            .build();

        boolean isTvProduct = produktId.compareTo(Produkt.PROD_ID_TV_SIGNALLIEFERUNG) == 0;
        final Produkt produkt = new ProduktBuilder()
                                .setPersist(false)
                                .withId(produktId)
                                .withErstellStatusId(erstellStatusId)
                                .withEndstellenTyp(Produkt.ES_TYP_KEINE_ENDSTELLEN)
                                .withBuendelBillingHauptauftrag(isTvProduct)
                                .withSendStatusUpdates(false)
                                .build();
        //@formatter:on
        cut.prepare(CreateTechAuftragCommand.AUFTRAG_NO, auftragNo);
        cut.prepare(CreateTechAuftragCommand.KUNDE_NO, RandomTools.createLong());
        cut.prepare(CreateTechAuftragCommand.SESSION_ID, RandomTools.createLong());
        doReturn(bas).when(cut).getBillingService(BillingAuftragService.class.getName(), BillingAuftragService.class);
        doReturn(ps).when(cut).getCCService(ProduktService.class);
        doReturn(cas).when(cut).getCCService(CCAuftragService.class);
        doReturn(ns).when(cut).getCCService(NiederlassungService.class);
        doReturn(as).when(cut).getCCService(AccountService.class);
        when(cas.findAuftragDaten4OrderNoOrig(eq(auftragNo))).thenReturn(Collections.<AuftragDaten>emptyList());
        when(bas.findAuftrag(eq(auftragNo))).thenReturn(ba);
        when(ps.doProduktMapping4AuftragNo(eq(auftragNo))).thenReturn(produkt);
        cut.execute();

        verify(cas).createAuftrag(anyLong(), auftragDatenCaptor.capture(), any(AuftragTechnik.class),
                anyLong(), any(IServiceCallback.class));
        verify(cas, times(1)).createAuftrag(anyLong(), any(AuftragDaten.class),
                any(AuftragTechnik.class), anyLong(), any(IServiceCallback.class));
        assertEquals(auftragNo, auftragDatenCaptor.getValue().getAuftragNoOrig());
        assertEquals(auftragDatenCaptor.getValue().getBearbeiter(), "WebService");
        assertEquals(auftragDatenCaptor.getValue().getStatusId(), expectedStatusId);
        assertEquals(auftragDatenCaptor.getValue().getVorgabeSCV(), ba.getGueltigVon());
        assertEquals(auftragDatenCaptor.getValue().getAuftragDatum(), ba.getVertragsdatum());
        assertEquals(auftragDatenCaptor.getValue().getProdId(), produkt.getId());
        assertEquals(auftragDatenCaptor.getValue().getStatusmeldungen(), Boolean.FALSE);
        verify(cas, times(isTvProduct ? 1 : 0))
                .createBillingHauptauftragsBuendel(any(AuftragDaten.class), any(BAuftrag.class));
    }
}

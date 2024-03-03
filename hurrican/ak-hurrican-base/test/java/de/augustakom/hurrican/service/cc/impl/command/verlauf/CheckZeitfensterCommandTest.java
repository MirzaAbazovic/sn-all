/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2012 08:26:08
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.BAuftragKombi;
import de.augustakom.hurrican.model.billing.BAuftragKombiBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;

@Test(groups = { BaseTest.UNIT })
public class CheckZeitfensterCommandTest extends BaseTest {

    private static final Long AUFTRAG_ID = Long.MAX_VALUE;

    @InjectMocks
    @Spy
    private CheckZeitfensterCommand cut;

    @Mock
    private CCAuftragService ccAuftragServiceMock;
    @Mock
    private BillingAuftragService billingAuftragServiceMock;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private HVTService hvtService;

    @BeforeMethod
    public void setUp() {
        cut = new CheckZeitfensterCommand();
        MockitoAnnotations.initMocks(this);

        doReturn(AUFTRAG_ID).when(cut).getAuftragId();
    }


    @DataProvider(name = "executeDataProvider")
    public Object[][] executeDataProvider() {
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragNoOrig(Long.MAX_VALUE).setPersist(false).build();
        BAuftrag billingAuftrag = new BAuftragBuilder().build();
        BAuftragKombi auftragKombiTimeSlotDefined = new BAuftragKombiBuilder().withTimeSlotNo(Long.valueOf(1)).build();
        BAuftragKombi auftragKombiTimeSlotNotDefined = new BAuftragKombiBuilder().withTimeSlotNo(null).build();

        // @formatter:off
        return new Object[][] {
                { null, null, null, false },
                { auftragDaten, null, null, false },
                { auftragDaten, billingAuftrag, null, false },
                { auftragDaten, billingAuftrag, auftragKombiTimeSlotNotDefined, false },
                { auftragDaten, billingAuftrag, auftragKombiTimeSlotDefined, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "executeDataProvider")
    public void testExecute(AuftragDaten auftragDaten, BAuftrag billingAuftrag, BAuftragKombi auftragKombi, boolean expectOk) throws Exception {
        when(ccAuftragServiceMock.findAuftragDatenByAuftragId(AUFTRAG_ID)).thenReturn(auftragDaten);
        when(billingAuftragServiceMock.findAuftrag(any(Long.class))).thenReturn(billingAuftrag);
        when(billingAuftragServiceMock.findAuftragKombiByAuftragNo(any(Long.class))).thenReturn(auftragKombi);

        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertEquals(result.isOk(), expectOk);
    }


    @Test
    public void verifyNoCheckOnFttc() throws Exception {
        HVTStandortBuilder hvtStdBuilder = new HVTStandortBuilder()
                .withRandomId()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)
                .setPersist(false);

        Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStdBuilder)
                .setPersist(false).build();

        when(endstellenService.findEndstelle4Auftrag(AUFTRAG_ID, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        when(hvtService.findHVTStandort(hvtStdBuilder.get().getId())).thenReturn(hvtStdBuilder.get());
        verify(ccAuftragServiceMock, times(0)).findAuftragDatenByAuftragId(AUFTRAG_ID);

        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertEquals(result.isOk(), true);
    }

}



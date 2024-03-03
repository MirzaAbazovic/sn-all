/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2015 11:00:25
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.ProduktService;

@Test(groups = { BaseTest.UNIT })
public class CheckRufnummerTALCommandTest {

    @InjectMocks
    @Spy
    private CheckRufnummerTALCommand testling;

    @Mock
    private RufnummerService rufnummerService;

    @Mock
    private ProduktService produktService;

    @BeforeMethod
    public void setup() {
        testling = new CheckRufnummerTALCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteCheckOk() throws Exception {
        Long auftragId = 100L;
        testling.prepare(AbstractTALCommand.KEY_AUFTRAG_ID, auftragId);
        testling.prepare(AbstractTALCommand.KEY_CARRIERBESTELLUNG_ID, 10L);
        testling.prepare(AbstractTALCommand.KEY_CBVORGANG_TYP, Reference.REF_TYPE_TAL_BESTELLUNG_TYP);

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(1000L)
                .withProdId(Produkt.PROD_ID_DSL_VOIP)
                .build();

        Rufnummer einzelRufnummer = new RufnummerBuilder().withDnNoOrig(55L).withOnKz("0821").withDnBase("123212")
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .withDirectDial(null).build();
        Rufnummer anlageAnschluss = new RufnummerBuilder().withDnNoOrig(56L).withOnKz("089").withDnBase("123212")
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .withDirectDial("1").withRangeFrom("00").withRangeTo("99").build();
        Rufnummer anlageAnschlussDektiviert = new RufnummerBuilder().withDnNoOrig(57L).withOnKz("089").withDnBase("987654")
                .withPortMode(Rufnummer.PORT_MODE_DEAKTIVIERUNG)
                .withDirectDial("1").withRangeFrom("000").withRangeTo("999").build();

        when(produktService.findProdukt(auftragDaten.getProdId())).thenReturn(new Produkt());
        when(rufnummerService.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.TRUE, Boolean.TRUE }))
                        .thenReturn(Arrays.asList(einzelRufnummer, anlageAnschluss, anlageAnschlussDektiviert));
        doReturn(auftragDaten).when(testling).getAuftragDatenTx(auftragId);

        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        Assert.assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);
    }

    @Test
    public void testExecuteCheckNotOk() throws Exception {
        Long auftragId = 101L;
        testling.prepare(AbstractTALCommand.KEY_AUFTRAG_ID, auftragId);
        testling.prepare(AbstractTALCommand.KEY_CARRIERBESTELLUNG_ID, 10L);
        testling.prepare(AbstractTALCommand.KEY_CBVORGANG_TYP, Reference.REF_TYPE_TAL_BESTELLUNG_TYP);

        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(1001L)
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .build();

        Rufnummer einzelRufnummer = new RufnummerBuilder().withDnNoOrig(58L).withOnKz("0821").withDnBase("123212")
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .withDirectDial(null).build();
        Rufnummer anlageAnschluss = new RufnummerBuilder().withDnNoOrig(59L).withOnKz("089").withDnBase("123212")
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .withDirectDial("1").withRangeFrom("0000").withRangeTo("9999").build();

        when(produktService.findProdukt(auftragDaten.getProdId())).thenReturn(new Produkt());
        when(rufnummerService.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                new Object[] { auftragDaten.getAuftragNoOrig(), Boolean.TRUE, Boolean.TRUE }))
                .thenReturn(Arrays.asList(einzelRufnummer, anlageAnschluss));
        doReturn(auftragDaten).when(testling).getAuftragDatenTx(auftragId);

        ServiceCommandResult result = (ServiceCommandResult) testling.execute();
        Assert.assertEquals(result.getCheckStatus(), ServiceCommandResult.CHECK_STATUS_INVALID);
    }
}
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import java.time.*;
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
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalCommonCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private PhysikService physikService;
    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private RufnummerService rufnummerService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalCommonCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalCommonCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        prepareFfmCommand(testling);
        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(!((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }


    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        VerbindungsBezeichnung vbz = new VerbindungsBezeichnungBuilder().withVbz("XY123").setPersist(false).build();
        when(physikService.findVerbindungsBezeichnungByAuftragId(anyLong())).thenReturn(vbz);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withId(5555L).withKundeNo(10000001L).setPersist(false);
        Auftrag auftrag = auftragBuilder.build();
        AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragBuilder(auftragBuilder).withAuftragNoOrig(789L).build();
        doReturn(auftragDaten).when(testling).getAuftragDaten();

        OrderTechnicalParams.Common.Porting porting = new OrderTechnicalParams.Common.Porting();
        porting.setPortingDate("2014-10-30");
        doReturn(porting).when(testling).loadPortingInformation();
        doReturn(auftrag).when(ccAuftragService).findAuftragById(anyLong());

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertNotNull(workforceOrder.getDescription().getTechParams().getCommon());
        Assert.assertEquals(workforceOrder.getDescription().getTechParams().getCommon().getLineId(), vbz.getVbz());
        Assert.assertEquals(workforceOrder.getDescription().getTechParams().getCommon().getContractId(), ""+auftrag.getAuftragId());
        Assert.assertEquals(workforceOrder.getDescription().getTechParams().getCommon().getAdditionalContractInfo(), "" + auftragDaten.getAuftragNoOrig());
        Assert.assertEquals(workforceOrder.getDescription().getTechParams().getCommon().getPorting(), porting);
        Assert.assertNotNull(workforceOrder.getDescription().getTechParams().getCommon().getCustomerNumber());
        Assert.assertEquals(workforceOrder.getDescription().getTechParams().getCommon().getCustomerNumber(), "10000001");

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }


    @Test
    public void testLoadPortingInformation() throws FindException {
        prepareFfmCommand(testling, true);

        Rufnummer mnetDn = new RufnummerBuilder().setPersist(false).build();
        Rufnummer kommendDn1 = new RufnummerBuilder()
                .withRealDate(Date.from(LocalDateTime.of(2014, 10, 20, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant()))
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .withLastCarrier("DTAG", "D001")
                .setPersist(false).build();
        Rufnummer kommendDn2 = new RufnummerBuilder()
                .withRealDate(Date.from(LocalDateTime.of(2014, 10, 22, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant()))
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .withLastCarrier("DTAG", "D001")
                .setPersist(false).build();

        doReturn(new AuftragDatenBuilder().withAuftragNoOrig(1L).build()).when(testling).getAuftragDaten();
        when(rufnummerService.findRNs4Auftrag(anyLong(), any(Date.class)))
                .thenReturn(Arrays.asList(mnetDn, kommendDn1, kommendDn2));

        OrderTechnicalParams.Common.Porting result = testling.loadPortingInformation();
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getPortingDate(), "20.10.2014");
        Assert.assertEquals(result.getLastCarrier(), "DTAG");
    }

}

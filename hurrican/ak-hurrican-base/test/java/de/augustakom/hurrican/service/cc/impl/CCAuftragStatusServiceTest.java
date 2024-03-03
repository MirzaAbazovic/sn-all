/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2012 13:41:07
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.tools.AuftragFtthSuFBuilder;
import de.augustakom.hurrican.model.tools.AuftragSdslBuilder;
import de.augustakom.hurrican.model.tools.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.tools.CpsTx4AuftragBuilder;
import de.augustakom.hurrican.model.tools.StandortFtthBuilder;
import de.augustakom.hurrican.model.tools.StandortHvtBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;

@Test(groups = { BaseTest.SERVICE })
public class CCAuftragStatusServiceTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private CCAuftragService ccAuftragService;

    @DataProvider
    Object[][] kuendigeAuftragDataProvider() {
        return new Object[][] {
                { null, AuftragStatus.KUENDIGUNG_ERFASSEN },
                { AuftragStatus.AUFTRAG_GEKUENDIGT, AuftragStatus.AUFTRAG_GEKUENDIGT },
        };
    }

    @Test(dataProvider = "kuendigeAuftragDataProvider")
    public void testKuendigeAuftrag(final Long prodKueStatus, final Long erwarteterAuftStatus) throws Exception {
        //@formatter:off
        final ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class)
                .withKuendigungStatusId(prodKueStatus);
        final AuftragDaten auftragDaten = getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(
                        getBuilder(AuftragBuilder.class)
                            .withAuftragTechnikBuilder(
                                    getBuilder(AuftragTechnikBuilder.class)))
                .withProdBuilder(produktBuilder)
                .build();
        //@formatter:on

        CCAuftragStatusService service = getCCService(CCAuftragStatusService.class);
        service.kuendigeAuftrag(auftragDaten.getAuftragId(), new Date(), getSessionId());
        flushAndClear();

        AuftragDaten ad = ccAuftragService.findAuftragDatenByAuftragIdTx(
                auftragDaten.getAuftragId());
        AuftragTechnik at = ccAuftragService.findAuftragTechnikByAuftragIdTx(
                auftragDaten.getAuftragId());

        assertNotNull(ad, "Es konnten keine Auftrags-Daten fuer die Auftrags-ID gefunden werden.");
        assertEquals(ad.getAuftragId(), auftragDaten.getAuftragId(),
                "Die AuftragID des Auftrags ist nicht korrekt");
        assertEquals(ad.getStatusId(), erwarteterAuftStatus, String.format("Der Status des Auftrags ist nicht korrekt!" +
                " Aktueller Status ist '%s', erwartet wurde '%s'", ad.getStatusId(), erwarteterAuftStatus));
        assertEquals(at.getAuftragsart(), BAVerlaufAnlass.KUENDIGUNG,
                "Der Status des Auftrags ist nicht auf 'KUENDIGUNG_ERFASSEN'");
    }

    @Test
    public void testPerformAuftragAbsagenFtth() throws Exception {
        Object[] objects = createSuFFtth(new Date(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        final CCAuftragStatusService service = (CCAuftragStatusService) objects[0];
        final CPSService cpsServiceMock = (CPSService) objects[1];
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = (AuftragFtthSuFBuilder.AuftragFtthSuF) objects[2];
        service.performAuftragAbsagen(auftragFtthSuF.auftragBuilder.get().getAuftragId(), -1L);

        assertEquals(auftragFtthSuF.auftragDatenBuilder.get().getStatusId(), AuftragStatus.ABSAGE);
        verify(cpsServiceMock, times(1)).createCPSTransaction(any(CreateCPSTransactionParameter.class));
        verify(cpsServiceMock, times(1)).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), anyLong());
    }

    @Test
    public void testPerformAuftragAbsagenFtthWithNoDeleteSub() throws Exception {
        Object[] objects = createSuFFtth(new Date(), CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA);
        final CCAuftragStatusService service = (CCAuftragStatusService) objects[0];
        final CPSService cpsServiceMock = (CPSService) objects[1];
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = (AuftragFtthSuFBuilder.AuftragFtthSuF) objects[2];
        service.performAuftragAbsagen(auftragFtthSuF.auftragBuilder.get().getAuftragId(), -1L);

        assertEquals(auftragFtthSuF.auftragDatenBuilder.get().getStatusId(), AuftragStatus.ABSAGE);
        verify(cpsServiceMock, times(0)).createCPSTransaction(any(CreateCPSTransactionParameter.class));
        verify(cpsServiceMock, times(0)).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), anyLong());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testPerformAuftragAbsagenFtthFails() throws Exception {
        Object[] objects = createSuFFtth(null, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_SUB);
        final CCAuftragStatusService service = (CCAuftragStatusService) objects[0];
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = (AuftragFtthSuFBuilder.AuftragFtthSuF) objects[2];
        service.performAuftragAbsagen(auftragFtthSuF.auftragBuilder.get().getAuftragId(), -1L);
    }

    private Object[] createSuFFtth(Date kuendigungAm, Long serviceOrderType) throws Exception {

        final StandortFtthBuilder standortFtthBuilder = new StandortFtthBuilder();
        final AuftragFtthSuFBuilder auftragFtthSuFBuilder = new AuftragFtthSuFBuilder()
                .withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        final CpsTx4AuftragBuilder cpsTx4AuftragBuilder = new CpsTx4AuftragBuilder()
                .withServiceOrderType(serviceOrderType);
        final CarrierbestellungBuilder carrierbestellungBuilder = new CarrierbestellungBuilder()
                .withKuendigungAm(kuendigungAm);

        final StandortFtthBuilder.StandortFtth standortFtth = standortFtthBuilder.prepare(this, null);
        final AuftragFtthSuFBuilder.AuftragFtthSuF auftragFtthSuF = auftragFtthSuFBuilder.prepare(this, standortFtth);
        final CpsTx4AuftragBuilder.CpsTx4Auftrag cpsTx4Auftrag = cpsTx4AuftragBuilder.prepare(this, auftragFtthSuF);
        carrierbestellungBuilder.prepare(this, auftragFtthSuF);

        auftragFtthSuFBuilder.build(auftragFtthSuF);
        cpsTx4AuftragBuilder.build(cpsTx4Auftrag);
        carrierbestellungBuilder.build(auftragFtthSuF);

        final CPSTransaction tx = new CPSTransactionBuilder().withRandomId().setPersist(false).build();
        final AKWarnings warnings = new AKWarnings();
        final CPSTransactionResult result = new CPSTransactionResult(ImmutableList.of(tx), warnings);

        CCAuftragStatusService service = getCCService(CCAuftragStatusService.class);
        CPSService cpsServiceMock = Mockito.mock(CPSService.class);
        doReturn(result).when(cpsServiceMock).createCPSTransaction(any(CreateCPSTransactionParameter.class));
        List<CPSTransaction> successfulCpsTxs = new ArrayList<>();
        successfulCpsTxs.add(cpsTx4Auftrag.cpsTransactionBuilder.get());
        doReturn(successfulCpsTxs).when(cpsServiceMock).findSuccessfulCPSTransaction4TechOrder(anyLong());
        doNothing().when(cpsServiceMock).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), anyLong());
        service.setCpsService(cpsServiceMock);
        return new Object[] { service, cpsServiceMock, auftragFtthSuF };
    }

    @Test
    public void testPerformAuftragAbsagenSdsl() throws Exception {
        Object[] objects = createSdsl();
        final CCAuftragStatusService service = (CCAuftragStatusService) objects[0];
        final CPSService cpsServiceMock = (CPSService) objects[1];
        final AuftragSdslBuilder.AuftragSdsl auftragSdsl = (AuftragSdslBuilder.AuftragSdsl) objects[2];

        // n-Draht Auftrag
        service.performAuftragAbsagen(auftragSdsl.auftraege[1].auftragBuilder.get().getAuftragId(), -1L);
        assertEquals(auftragSdsl.auftraege[1].auftragDatenBuilder.get().getStatusId(), AuftragStatus.ABSAGE);
        ArgumentCaptor<CreateCPSTransactionParameter> argumentModify = ArgumentCaptor
                .forClass(CreateCPSTransactionParameter.class);
        verify(cpsServiceMock, times(1)).createCPSTransaction(argumentModify.capture());
        assertEquals(CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB,
                argumentModify.getValue().getServiceOrderType());
        verify(cpsServiceMock, times(1)).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), anyLong());

        // SDSL Auftrag
        service.performAuftragAbsagen(auftragSdsl.auftraege[0].auftragBuilder.get().getAuftragId(), -1L);
        assertEquals(auftragSdsl.auftraege[0].auftragDatenBuilder.get().getStatusId(), AuftragStatus.ABSAGE);
        ArgumentCaptor<CreateCPSTransactionParameter> argumentCancel = ArgumentCaptor
                .forClass(CreateCPSTransactionParameter.class);
        verify(cpsServiceMock, times(2)).createCPSTransaction(argumentCancel.capture());
        assertEquals(CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB,
                argumentCancel.getValue().getServiceOrderType());
        verify(cpsServiceMock, times(2)).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), anyLong());
    }

    private Object[] createSdsl() throws Exception {

        final StandortHvtBuilder standortHvtBuilder = new StandortHvtBuilder();
        final AuftragSdslBuilder auftragSdslBuilder = new AuftragSdslBuilder()
                .withStatusId(AuftragStatus.AUS_TAIFUN_UEBERNOMMEN);
        final CpsTx4AuftragBuilder cpsTx4AuftragBuilder = new CpsTx4AuftragBuilder();
        final CarrierbestellungBuilder carrierbestellungBuilder = new CarrierbestellungBuilder();

        final StandortHvtBuilder.StandortHvt standortHvt = standortHvtBuilder.prepare(this, null);
        final AuftragSdslBuilder.AuftragSdsl auftragSdsl = auftragSdslBuilder.prepare(this, standortHvt);
        final CpsTx4AuftragBuilder.CpsTx4Auftrag cpsTx4Auftrag = cpsTx4AuftragBuilder.prepare(this,
                auftragSdsl.auftraege[0]);
        carrierbestellungBuilder.prepare(this, auftragSdsl.auftraege[0]);

        auftragSdslBuilder.build(auftragSdsl);
        cpsTx4AuftragBuilder.build(cpsTx4Auftrag);
        carrierbestellungBuilder.build(auftragSdsl.auftraege[0]);

        final CPSTransaction tx = new CPSTransactionBuilder().withRandomId().setPersist(false).build();
        final AKWarnings warnings = new AKWarnings();
        final CPSTransactionResult result = new CPSTransactionResult(ImmutableList.of(tx), warnings);

        CCAuftragStatusService service = getCCService(CCAuftragStatusService.class);
        CPSService cpsServiceMock = Mockito.mock(CPSService.class);
        doReturn(result).when(cpsServiceMock).createCPSTransaction(any(CreateCPSTransactionParameter.class));
        List<CPSTransaction> successfulCpsTxs = new ArrayList<>();
        successfulCpsTxs.add(cpsTx4Auftrag.cpsTransactionBuilder.get());
        doReturn(successfulCpsTxs).when(cpsServiceMock).findSuccessfulCPSTransaction4TechOrder(anyLong());
        doNothing().when(cpsServiceMock).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), anyLong());
        service.setCpsService(cpsServiceMock);
        return new Object[] { service, cpsServiceMock, auftragSdsl };
    }
}



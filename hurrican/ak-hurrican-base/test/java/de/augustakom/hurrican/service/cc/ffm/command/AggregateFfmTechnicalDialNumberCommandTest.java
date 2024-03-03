/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDNBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.BlockDNView;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalDialNumberCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private RufnummerService rufnummerService;

    @Mock
    private VoIPService voIPService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalDialNumberCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalDialNumberCommand();
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
    public void testExecuteAuftragWithoutTaifunReference() throws Exception {
        prepareFfmCommand(testling, true);

        doReturn(new AuftragDatenBuilder().withAuftragNoOrig(null).build()).when(testling).getAuftragDaten();

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.DialNumber> dialNumbers = workforceOrder.getDescription().getTechParams().getDialNumber();
        Assert.assertTrue(dialNumbers.isEmpty());
    }

    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        Rufnummer singleDn = new RufnummerBuilder()
                .withRandomDnNoOrig()
                .withDialnumber("089", "123456", null, null, null)
                .withPortMode(Rufnummer.PORT_MODE_KOMMEND)
                .setPersist(false).build();
        Rufnummer blockDn = new RufnummerBuilder()
                .withDnNoOrig(singleDn.getDnNoOrig()+1)
                .withDialnumber("089", "55556666", "0", "30", "99")
                .withPortMode(null)
                .setPersist(false).build();

        final String dnBase = "98765";
        final String onkz = "821";

        final VoipDnBlock voipDnBlockOhneZentrale = new VoipDnBlock("10", "99", false);
        final VoipDnBlock voipDnBlockMitZentrale = new VoipDnBlock("00", null, true);

        final AuftragVoipDNView auftragVoipDNView =
                getAuftragVoipDNView(blockDn, dnBase, onkz, voipDnBlockOhneZentrale, voipDnBlockMitZentrale);
        final AuftragVoIPDN auftragVoipDn = getAuftragVoIPDN();

        final AuftragDaten auftragDaten = new AuftragDatenBuilder().withAuftragNoOrig(1L).build();
        doReturn(auftragDaten).when(testling).getAuftragDaten();
        doReturn(Optional.of(new VerlaufBuilder().withRandomId().withRealisierungstermin(new Date()).build())).when(testling).getBauauftrag();
        when(rufnummerService.findRNs4Auftrag(anyLong(), any(Date.class))).thenReturn(Arrays.asList(singleDn, blockDn));
        when(voIPService.findByAuftragIDDN(anyLong(), eq(singleDn.getDnNoOrig()))).thenReturn(auftragVoipDn);
        when(voIPService.findVoIPDNView(auftragDaten.getAuftragId())).thenReturn(Lists.newArrayList(auftragVoipDNView));

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.DialNumber> dialNumbers = workforceOrder.getDescription().getTechParams().getDialNumber();
        Assert.assertNotNull(dialNumbers);
        Assert.assertTrue(dialNumbers.size() == 2);

        boolean singleDnExist = false;
        boolean blockDnExist = false;
        for (OrderTechnicalParams.DialNumber dialNumber : dialNumbers) {
            Assert.assertNotNull(dialNumber.getValidFrom());
            Assert.assertNotNull(dialNumber.getValidTo());

            if (singleDn.getDnBase().equals(dialNumber.getNumber())) {
                singleDnExist = true;
                Assert.assertEquals(singleDn.getOnKz(), dialNumber.getAreaDialingCode());
                Assert.assertEquals(singleDn.getPortMode(), dialNumber.getPortMode());
                Assert.assertNotNull(dialNumber.getValidFrom());
                Assert.assertNotNull(dialNumber.getValidTo());
                Assert.assertNull(dialNumber.getNumberRange());
                assertVoipLogin(auftragVoipDn, dialNumber);
            }
            else if (blockDn.getDnBase().equals(dialNumber.getNumber())) {
                blockDnExist = true;
                Assert.assertEquals(blockDn.getOnKz(), dialNumber.getAreaDialingCode());
                Assert.assertEquals(blockDn.getPortMode(), dialNumber.getPortMode());
                Assert.assertEquals(blockDn.getDirectDial(), dialNumber.getNumberRange().getCentral());
                Assert.assertEquals(blockDn.getRangeFrom(), dialNumber.getNumberRange().getFrom());
                Assert.assertEquals(blockDn.getRangeTo(), dialNumber.getNumberRange().getTo());
                Assert.assertNull(dialNumber.getVoIPLogin());
                Assert.assertEquals(dialNumber.getDialNumberPlan().size(), 2);
                assertRufnummernplan(voipDnBlockMitZentrale, dnBase, onkz, extractOnlyPlanMitZentrale(dialNumber));
                assertRufnummernplan(voipDnBlockOhneZentrale, dnBase, onkz, extractOnlyPlanOhneZentrale(dialNumber));
            }
        }
        Assert.assertTrue(singleDnExist);
        Assert.assertTrue(blockDnExist);

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }

    private AuftragVoIPDN getAuftragVoIPDN() {
        return new AuftragVoIPDNBuilder()
                    .withRandomId()
                    .withSIPPassword("aSDfjKl")
                    .withRufnummernplaene(Collections.singletonList(new VoipDnPlanBuilder()
                        .withVoipDnBlocks(Collections.singletonList(new VoipDnBlock("0", "10", true)))
                        .withSipLogin("+499945205320@biz.m-call.de")
                        .withSipHauptrufnummer("+499945205320")
                        .build()))
                    .build();
    }

    private VoipDnPlan getVoipDnPlan(Date gueltigAb, VoipDnBlock... blocks) {
        return new VoipDnPlanBuilder()
                    .withRandomId()
                    .withGueltigAb(gueltigAb)
                    .withVoipDnBlocks(Lists.newArrayList(blocks))
                .build();
    }

    private AuftragVoipDNView getAuftragVoipDNView(Rufnummer blockDn, String dnBase, String onkz, VoipDnBlock... blocks) {
        final VoipDnPlan voipDnPlanGueltig = getVoipDnPlan(new Date(), blocks);
        final VoipDnPlan voipDnPlanNichtMehrGueltig =
                getVoipDnPlan(DateConverterUtils.asDate(LocalDate.now().minusDays(1)), new VoipDnBlock("00", null, true));
        final VoipDnPlan voipDnPlanNochNichtGueltig =
                getVoipDnPlan(DateConverterUtils.asDate(LocalDate.now().plusDays(1)), new VoipDnBlock("00", null, true));
        final AuftragVoipDNView auftragVoipDNView = new AuftragVoipDNView();
        auftragVoipDNView.setDnNoOrig(blockDn.getDnNoOrig());
        auftragVoipDNView.setBlock(new BlockDNView("0", "00", "99"));
        auftragVoipDNView.addVoipDnPlanView(new VoipDnPlanView(onkz, dnBase, voipDnPlanGueltig));
        auftragVoipDNView.addVoipDnPlanView(new VoipDnPlanView(onkz, dnBase, voipDnPlanNichtMehrGueltig));
        auftragVoipDNView.addVoipDnPlanView(new VoipDnPlanView(onkz, dnBase, voipDnPlanNochNichtGueltig));
        auftragVoipDNView.setGueltigVon(new Date());
        auftragVoipDNView.setGueltigBis(DateTools.getHurricanEndDate());
        return auftragVoipDNView;
    }

    private void assertRufnummernplan(VoipDnBlock voipDnBlock, String dnBase, String onkz,
            OrderTechnicalParams.DialNumber.DialNumberPlan plan) {
        Assert.assertEquals(plan.getNumber(), dnBase);
        Assert.assertEquals(plan.getAreaDialingCode(), onkz);
        Assert.assertEquals(plan.getNumberRange().getFrom(), voipDnBlock.getAnfang());
        Assert.assertEquals(plan.getNumberRange().getTo(), voipDnBlock.getEnde());
        Assert.assertEquals(plan.getNumberRange().getCentral(), String.valueOf(voipDnBlock.getZentrale()));
    }

    private OrderTechnicalParams.DialNumber.DialNumberPlan extractOnlyPlanOhneZentrale(OrderTechnicalParams.DialNumber dialNumber) {
        return dialNumber.getDialNumberPlan()
                .stream()
                .filter(plan -> !Boolean.valueOf(plan.getNumberRange().getCentral()))
                .findFirst()
                .get();
    }

    private OrderTechnicalParams.DialNumber.DialNumberPlan extractOnlyPlanMitZentrale(OrderTechnicalParams.DialNumber dialNumber) {
        return dialNumber.getDialNumberPlan()
                .stream()
                .filter(plan -> Boolean.valueOf(plan.getNumberRange().getCentral()))
                .findFirst()
                .get();
    }

    private void assertVoipLogin(AuftragVoIPDN auftragVoipDn, OrderTechnicalParams.DialNumber dialNumber) {
        VoipDnPlan plan = auftragVoipDn.getActiveRufnummernplan(new Date());

        if (plan != null) {
            Assert.assertEquals(plan.getSipHauptrufnummer(), dialNumber.getVoIPLogin().getSIPMainNumber());
            Assert.assertEquals(plan.getSipLogin(), dialNumber.getVoIPLogin().getSIPLogin());
        } else {
            Assert.assertNull(dialNumber.getVoIPLogin().getSIPMainNumber());
            Assert.assertNull(dialNumber.getVoIPLogin().getSIPLogin());
        }

        Assert.assertEquals(auftragVoipDn.getSipPassword(), dialNumber.getVoIPLogin().getSIPPassword());
    }

}

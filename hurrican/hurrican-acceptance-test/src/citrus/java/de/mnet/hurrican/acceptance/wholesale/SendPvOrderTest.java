/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 09.02.2017

 */

package de.mnet.hurrican.acceptance.wholesale;


import static org.junit.Assert.*;

import java.text.*;
import java.time.*;
import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.builder.WbciTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.wholesale.dao.WholesaleAuditDAO;
import de.mnet.hurrican.wholesale.model.PvStatus;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.builder.PreAgreementVOBuilder;

/**
 * This test simulates the Hurrican Client - sending a wholesale order for PV (Providerwechsel) and
 * expects a createOrder message to atlas.
 * <p>
 * <p>
 * Created by wieran on 09.02.2017.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class SendPvOrderTest extends AbstractHurricanTestBuilder {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final String TEST_VORABSTIMMUNG_ID = "testVaId-123";

    @Autowired
    private WholesaleAuditDAO wholesaleAuditDAO;


    @CitrusTest
    public void sendPvOrderTest() {
        simulatorUseCase(SimulatorUseCase.WholesaleOrder, WholesaleOrderOutboundTestVersion.V2);

        //given
        String loginName = "LoginName";
        Date expectedWechseltermin = new Date();

        VerlaufTestBuilder.CreatedData createdData = createAuftragsdaten();
        PreAgreementVOBuilder preAgreementVOBuilder = createWbciData(createdData);
        PreAgreementVO preAgreementVO = preAgreementVOBuilder.build();

        variables().add("wunschtermin", sdf.format(expectedWechseltermin));

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                //when
                hurrican().getWholesleOrderOutboundService().sendWholesaleCreateOrderPV(preAgreementVO, loginName);
                //then (muss hier geprueft werden, sonst ist DB schon wieder aufgeraeumt)
                assertWholesaleAudit(preAgreementVO.getVaid());
            }
        });

        //then
        atlas().receiveWholesaleOrderService("createOrderPV");
    }

    private void assertWholesaleAudit(String vaid) {
        List<WholesaleAudit> wholesaleAudits;
        try {
            wholesaleAudits = wholesaleAuditDAO.findByVorabstimmungsId(vaid);
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
        assertThat(wholesaleAudits, Matchers.hasSize(1));

        WholesaleAudit wholesaleAudit = wholesaleAudits.get(0);
        assertThat(wholesaleAudit.getVorabstimmungsId(), Matchers.is(TEST_VORABSTIMMUNG_ID));
        assertThat(wholesaleAudit.getStatus(), Matchers.is(PvStatus.GESENDET));
    }

    private PreAgreementVOBuilder createWbciData(VerlaufTestBuilder.CreatedData createdData) {
        Long auftragNoOrig = createdData.auftragDaten.getAuftragNoOrig();

        WbciTestBuilder wbciTestBuilder = hurrican().getWbciTestBuilder();
        wbciTestBuilder.withVorabstimmungsId(TEST_VORABSTIMMUNG_ID);
        wbciTestBuilder.withKundenwunschtermin(LocalDate.now());
        wbciTestBuilder.build();

        PreAgreementVOBuilder preAgreementVOBuilder = new PreAgreementVOBuilder();
        preAgreementVOBuilder
                .withAuftragNoOrig(auftragNoOrig)
                .withVaid(TEST_VORABSTIMMUNG_ID);
        return preAgreementVOBuilder;
    }

    private VerlaufTestBuilder.CreatedData createAuftragsdaten() {
        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_WHOLESALE_FTTX)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();

        final Pair<Auftrag, AuftragDaten> subOrder = hurrican().getHurricanAuftragBuilder()
                .buildHurricanAuftrag(
                        createdData.auftragDaten.getProdId(),
                        createdData.auftrag.getKundeNo(),
                        createdData.auftragDaten.getAuftragNoOrig(),
                        HVTStandort.HVT_STANDORT_TYP_HVT,
                        false, false, false);

        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setSubAuftragsIds(new HashSet<>(Collections.singletonList(subOrder.getFirst().getAuftragId())));
        hurrican().getAuftragDAO().store(bauauftrag);

        Auftrag2TechLeistung downstream = new Auftrag2TechLeistungBuilder()
                .withTechLeistungId(301L)         // = VOIP_TK
                .withAktivVon(bauauftrag.getRealisierungstermin())
                .withVerlaufIdReal(bauauftrag.getId())
                .setPersist(false).build();
        downstream.setAuftragId(bauauftrag.getAuftragId());
        hurrican().getAuftragDAO().store(downstream);
        return createdData;
    }
}


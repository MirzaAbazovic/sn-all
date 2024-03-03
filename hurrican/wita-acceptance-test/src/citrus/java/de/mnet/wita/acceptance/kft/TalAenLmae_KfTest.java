/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.augustakom.hurrican.model.cc.Carrier.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;

@Test(groups = BaseTest.ACCEPTANCE)
public class TalAenLmae_KfTest extends AbstractWitaAcceptanceTest {

    /**
     * Leistungsmerkmaländerung für das im Auftrag aus Vorbedingung bestellte Produkt (geänderte Übertragungsverfahren)
     * Auftrag verzögert sich.
     */
    @CitrusTest(name = "TalAenLmae_01_KfTest")
    public void talAenLmae01() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_AENDERUNG_AEN_LMAE_01_NEU)
                .withUetv(Uebertragungsverfahren.H04);
        //            .withHurricanProduktId(Produkt.AK_CONNECT);

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

        workflow().doWithWorkflow(wf -> { wf.assertStandortKundeSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertSchaltangabenSet(); });

        //////////////////////////////////////////////////////
        useCase(WitaAcceptanceUseCase.TAL_AENDERUNG_AEN_LMAE_01, getWitaVersionForAcceptanceTest());

        workflow().sendLmae(WitaSimulatorTestUser.TAL_AENDERUNG_AEN_LMAE_01)
                .testDataBuilder()
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(Uebertragungsverfahren.H13);

        atlas().receiveOrder("AEN_LMAE");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        variables().add("delayDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");

        atlas().sendNotification("VZM");
        workflow().waitForVZM();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.assertStandortKundeSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertSchaltangabenSet(); });
    }

    /**
     * <ol> <li>Vorbedingung für Leistungsmerkmaländerungauftrag - Auftrag muss erst erledigt werden</li>
     * <li>Leistungsmerkmaländerung für das im Auftrag aus Vorbedingung bestellte Produkt (geänderte
     * Übertragungsverfahren). Leistungsmerkmal nicht möglich (z.B.Übertragungsverfahren für Glasfaser Produkt).</li>
     * </ol>
     */
    @CitrusTest(name = "TalAenLmae_02_KfTest")
    public void talAenLmae02() throws Exception {
        useCase(WitaAcceptanceUseCase.LAE_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_AENDERUNG_AEN_LMAE_02_NEU)
                .withReferencingCbBuilder(
                        (new CarrierbestellungBuilder())
                                .withCarrier(Carrier.ID_DTAG)
                                .withLbz("96U/44010/44010/667788")
                                .withVtrNr(hurrican().getNextVertragsnummer()));
        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_PORTWECHSEL);


        atlas().receiveOrderWithRequestedCustomerDate("LAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });

        workflow().doWithWorkflow(wf -> { wf.assertStandortKundeSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertSchaltangabenSet(); });

        //////////////////////////////////////////////////////
        useCase(WitaAcceptanceUseCase.TAL_AENDERUNG_AEN_LMAE_02, getWitaVersionForAcceptanceTest());

        workflow().sendLmae(WitaSimulatorTestUser.TAL_AENDERUNG_AEN_LMAE_02)
                .testDataBuilder()
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(Uebertragungsverfahren.H08);

        atlas().receiveOrder("AEN_LMAE");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABBM");
        workflow().waitForABBM();

        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertSchaltangabenSet(); });

    }

    @CitrusTest(name = "TalAenLmae_03_KfTest")
    public void talAenLmae03() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_Verbundleistung_VBLT_01, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_AENDERUNG_AEN_LMAE_03_VBL)
                .withTaifunData(
                        getTaifunDataFactory()
                                .withActCarrier(TNB.AKOM)
                                .surfAndFonWithDns(3)
                )
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, ID_DTAG)
                .withVorgabeMnet(getPortDate());

        final CreatedData baseData = workflow.createData(testData);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_ANBIETERWECHSEL);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID, VariableNames.PRODUCT_IDENTIFIER);
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.closeCbVorgang(); });
        workflow().doWithWorkflow(wf -> { wf.assertBestandssucheSet(); });
        workflow().doWithWorkflow(wf -> { wf.assertRufnummernPortierungEinzelanschlussSet(3); });

        //////////////////////////////////////////////////////
        useCase(WitaAcceptanceUseCase.TAL_AENDERUNG_AEN_LMAE_03, getWitaVersionForAcceptanceTest());

        workflow().sendLmae(WitaSimulatorTestUser.TAL_AENDERUNG_AEN_LMAE_03)
                .testDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.addWorkingDays(LocalDate.now(), 14).atStartOfDay())
                .withCarrierbestellungAuftragId4TalNa(baseData.auftrag.getAuftragId())
                .withUetv(Uebertragungsverfahren.H13);

        atlas().receiveOrder("AEN_LMAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABBM");
        workflow().waitForABBM();

        workflow().doWithWorkflow(wf -> { wf.assertUebertragungsverfahrenSet(); });

    }

}

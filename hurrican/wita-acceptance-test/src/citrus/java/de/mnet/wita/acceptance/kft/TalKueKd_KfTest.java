/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.AbbmMeldungsCode.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.common.WbciTestDataBuilder;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.AbstractWitaWorkflowTestAction;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = BaseTest.ACCEPTANCE)
public class TalKueKd_KfTest extends AbstractWitaAcceptanceTest {

    /**
     * Gutfall.
     */
    @CitrusTest(name = "TalKueKd_01_KfTest")
    public void talKueKd01() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        final String vorabstimmungsId = wbciTestDataBuilder.getWbciVorabstimmungsId();

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder baseBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14).atStartOfDay())
                .withUserName(WitaSimulatorTestUser.TAL_KUENDIGUNG_KUE_KD_01_NEUBESTELLUNG);

        if (vorabstimmungsId != null) {
            (new WbciTestDataBuilder(wbciDao))
                    .withWbciVorabstimmungsId(vorabstimmungsId)
                    .createWbciVorabstimmungForAccpetanceTestBuilder(baseBuilder, WbciCdmVersion.V1,
                            GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.KUENDIGUNG_KUNDE);
        }

        final CreatedData baseData = workflow.createData(baseBuilder);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        action(new AbstractWitaWorkflowTestAction("workflowAction") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow wf, TestContext testContext) throws Exception {
                final WitaCBVorgang origCbVorgang = wf.getCbVorgang();
                testContext.setVariable("originalCbVorgang", origCbVorgang);
            }
        });

        ///////////////////////////////////////////////

        useCase(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_KD_01, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        final AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21)).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())   ////////////////
                .withUserName(TAL_KUENDIGUNG_KUE_KD_01)
                .withCudaKuendigung();

        workflow().doWithWorkflow(wf -> {
            kueBuilder.withVorabstimmungsId(vorabstimmungsId);
            final WbciGeschaeftsfall wbciGeschaeftsfall = wbciDao.findWbciGeschaeftsfall(vorabstimmungsId);
            wbciGeschaeftsfall.setWechseltermin(kueBuilder.getVorgabeMnet().toLocalDate());
            wbciDao.store(wbciGeschaeftsfall);
        });

        workflow().select(workflow2);

        parallel(
                sequential(workflow().send(workflow2.createData(kueBuilder), CBVorgang.TYP_KUENDIGUNG)),
                sequential(
                        atlas().receiveGetDocumentRequest("GETDOCUMENT_REQUEST"),
                        atlas().sendGetDocumentResponse("CUDAKUENDIGUNG_RESPONSE")
                )
        );
        atlas().receiveOrder();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> { wf.assertAnlageSet(1); });
        workflow().doWithWorkflow(wf -> { Assert.assertEquals(DateConverterUtils.asLocalDate(wf.getCbVorgang().getVorgabeMnet()),
                DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(21)); });
   }

    /**
     * "Auftrag aus der Vorbedingung wird gekündigt. Vertragsnummer nicht im Bestand."
     * <p>
     * z.Z. kein explizites Manipulieren der Vertragsnummer
     */
    @CitrusTest(name = "TalKueKd_02_KfTest")
    public void talKueKd02() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder baseBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14).atStartOfDay())
                .withUserName(WitaSimulatorTestUser.TAL_KUENDIGUNG_KUE_KD_02_NEUBESTELLUNG);

        final CreatedData baseData = workflow.createData(baseBuilder);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        ///////////////////////////////////////////////

        useCase(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_KD_02, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        final AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21)).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr("1234567")
                .withUserName(TAL_KUENDIGUNG_KUE_KD_02);

        workflow().select(workflow2).send(workflow2.createData(kueBuilder), CBVorgang.TYP_KUENDIGUNG);
        atlas().receiveOrder("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABBM");
        workflow().waitForNonClosingAbbm(VERTRAGSNUMMER_NICHT_IM_BESTAND);
    }

    /**
     * "Auftrag aus der Vorbedingung wird gekündigt. Vertragsnummer nicht im Bestand."
     * <p>
     * z.Z. kein explizites Manipulieren der Vertragsnummer
     */
    @CitrusTest(name = "TalKueKd_03_KfTest")
    public void talKueKd03() throws Exception {
        useCase(WitaAcceptanceUseCase.LAE_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withUserName(TAL_KUENDIGUNG_KUE_KD_03_LAE)
                .withReferencingCbBuilder(
                        (new CarrierbestellungBuilder())
                                .withCarrier(Carrier.ID_DTAG)
                                .withLbz("96U/44010/44010/557788")
                                .withVtrNr(hurrican().getNextVertragsnummer()),
                        Uebertragungsverfahren.N01, RangSchnittstelle.N);
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

        workflow().doWithWorkflow(wf -> { wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H); });

        ///////////////////////////////////////////////

        useCase(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_KD_03, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        final AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21)).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr("1234567")
                .withUserName(TAL_KUENDIGUNG_KUE_KD_03);

        workflow().select(workflow2).send(workflow2.createData(kueBuilder), CBVorgang.TYP_KUENDIGUNG);
        atlas().receiveOrder("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        ///////////////////////////////////////////////

        useCase(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_KD_03_KD2, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow3 = workflow().get();
        final AcceptanceTestDataBuilder kueBuilder3 = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(DateCalculationHelper.addWorkingDays(LocalDate.now(), 28)).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr("1234567")
                .withUserName(TAL_KUENDIGUNG_KUE_KD_03_KD2);

        workflow().select(workflow3).send(workflow3.createData(kueBuilder3), CBVorgang.TYP_KUENDIGUNG);
        atlas().receiveOrder("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABBM");
        workflow().waitForNonClosingAbbm(VERTRAGSNUMMER_NICHT_IM_BESTAND);
    }

    /**
     * "Auftrag aus der Vorbedingung wird gekündigt. Es wird eine weitere Kündigung gesendet, nach der Erledigung der
     * ersten Kündigung"
     */
    @CitrusTest(name = "TalKueKd_04_KfTest")
    public void talKueKd04() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder baseBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14).atStartOfDay())
                .withUserName(WitaSimulatorTestUser.TAL_KUENDIGUNG_KUE_KD_04_NEUBESTELLUNG);

        final CreatedData baseData = workflow.createData(baseBuilder);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        ///////////////////////////////////////////////

        useCase(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_KD_04, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        final AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21)).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withUserName(TAL_KUENDIGUNG_KUE_KD_04);

        workflow().select(workflow2).send(workflow2.createData(kueBuilder), CBVorgang.TYP_KUENDIGUNG);
        atlas().receiveOrder("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        workflow().sendStorno();
        atlas().receiveOrder("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }

    /**
     * "Auftrag aus der Vorbedingung wird gekündigt. Terminverschiebung der Kündigung nach ABM der Kündigungauftrages;
     * Neuer KWT wird auf einen späteren Termin gesetzt (KWT des Auftrages aus der Vorbedingung + 14Tage)"
     */
    @CitrusTest(name = "TalKueKd_05_KfTest")
    public void talKueKd05() throws Exception {

        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM_ERLM_ENTM, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder baseBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14).atStartOfDay())
                .withUserName(WitaSimulatorTestUser.TAL_KUENDIGUNG_KUE_KD_05_NEUBESTELLUNG);

        final CreatedData baseData = workflow.createData(baseBuilder);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        ///////////////////////////////////////////////

        useCase(WitaAcceptanceUseCase.TAL_KUENDIGUNG_KUE_KD_05, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow2 = workflow().get();
        final AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(DateCalculationHelper.addWorkingDays(LocalDate.now(), 21)).atStartOfDay())
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungRealDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withCarrierbestellungLbz("96W/44010/440100/1234")
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withUserName(TAL_KUENDIGUNG_KUE_KD_05);

        workflow().select(workflow2).send(workflow2.createData(kueBuilder), CBVorgang.TYP_KUENDIGUNG);
        atlas().receiveOrder("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        ///////////////////////////////////////////////

        workflow().sendTv();
        atlas().receiveNotification("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);

        atlas().sendNotification("ABM2");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();
    }

}
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.14
 */
package de.mnet.wita.acceptance;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.model.cc.Carrier.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAufhebungAufAnfrage;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AutomationTaskTestBuilder;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.common.WbciTestDataBuilder;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.AbstractWitaWorkflowTestAction;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Test for secure the conformance between WBCI and WITA.
 *
 *
 */
@Test(groups = ACCEPTANCE)
public class WitaWbciConformance_Test extends AbstractWitaAcceptanceTest {

    private static final Logger LOGGER = Logger.getLogger(WitaWbciConformance_Test.class);

    @CitrusTest
    public void verbundleistungWithKlammerungAndWbciVorabstimmungDTAG() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_01A, WitaCdmVersion.V1);
        verbundleistungWithKlammerungAndWbciVorabstimmunt(CarrierCode.DTAG, TAL_WITA_WBCI_CONFORM_01A);
    }

    @CitrusTest
    public void verbundleistungWithKlammerungAndWbciVorabstimmungOtherCarrier() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_01B, WitaCdmVersion.V1);
        verbundleistungWithKlammerungAndWbciVorabstimmunt(CarrierCode.VODAFONE, TAL_WITA_WBCI_CONFORM_01B);
    }

    /**
     * Test zweier Verbundleistung-Aufträge mit Auftragsklammer, die sich auf eine WBCI-Vorabstimmung bezieht. <ul>
     * <li>DTAG: Die Anzahl der VertragsNr. wird überprüft, jedoch wird keine Vertragsnummer zugeordnet.</li>
     * <li>Anderer Carrrier: Sowohl die Anzahl der VertragsNr. wird überprüft als auch das den Wita-Vorgang eine
     * ausgewählte VertragsNr. zugeordnet wird.</li> </ul>Da der Carrier DTAG ist,.
     */
    private void verbundleistungWithKlammerungAndWbciVorabstimmunt(CarrierCode carrierCode, WitaSimulatorTestUser testUser) throws Exception {
        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        GeneratedTaifunData taifunData = getTaifunDataFactory().surfAndFonWithDns(1);
        AcceptanceTestDataBuilder builder1 = createBuilderVbl(taifunData, wbciTestDataBuilder.getWbciVorabstimmungsId(), testUser);
        AcceptanceTestDataBuilder builder2 = createBuilderVbl(taifunData, wbciTestDataBuilder.getWbciVorabstimmungsId(), testUser);
        String witaVertragsNr1 = "VWBCIVBL01";
        String witaVertragsNr2 = "VWBCIVBL02";

        wbciTestDataBuilder
                .withAbgebenderEKP(carrierCode)
                .addAkmTrLeitung(new LeitungBuilder().withVertragsnummer(witaVertragsNr1).build())
                .addAkmTrLeitung(new LeitungBuilder().withVertragsnummer(witaVertragsNr2).build())
                .withRuemVaTechnologie(Technologie.ADSL_SH)
                .createWbciVorabstimmungForAccpetanceTestBuilder(builder1, WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.VERBUNDLEISTUNG);

        AcceptanceTestWorkflow workflow1 = workflow().get();
        AcceptanceTestWorkflow workflow2 = workflow().get();
        workflow().select(workflow1);

        hurrican().createCbVorgangWithKlammerung(CBVorgang.TYP_ANBIETERWECHSEL,  workflow1.createData(builder1), workflow2.createData(builder2));

        atlas().receiveOrder("VBL").extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        workflow().doWithWorkflow(workflow -> assertKlammerung(workflow1, workflow2, workflow));
    }

    private void assertKlammerung(AcceptanceTestWorkflow workflow1, AcceptanceTestWorkflow workflow2, AcceptanceTestWorkflow workflow) throws Exception {
        List<WitaCBVorgang> cbVorgaenge = workflow.getCbVorgaenge4Klammerung();
        assertThat(cbVorgaenge, hasSize(2));

        workflow1.cbVorgangId = cbVorgaenge.get(0).getId();
        workflow1.assertKlaerfall(false, null);

        workflow2.cbVorgangId = cbVorgaenge.get(1).getId();
        workflow2.assertKlaerfall(false, null);
    }


    /**
     * Test zweier Providerwechsel-Aufträge mit Auftragsklammer, die sich auf eine WBCI-Vorabstimmung beziehen und
     * dadurch jeweils eine der beiden Vertragsnummern zugeordnet bekommen.
     */
    @CitrusTest
    public void providerwechselWithKlammerungAndWbciVorabstimmung() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_02, WitaCdmVersion.V1);

        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        String vorabstimmungsId = wbciTestDataBuilder.getWbciVorabstimmungsId();
        AcceptanceTestDataBuilder builder1 = createBuilderPVL(vorabstimmungsId);
        AcceptanceTestDataBuilder builder2 = createBuilderPVL(wbciTestDataBuilder.getWbciVorabstimmungsId());
        String witaVertragsNr1 = "VWBCIPV001";
        String witaVertragsNr2 = "VWBCIPV002";

        wbciTestDataBuilder
                .addAkmTrLeitung(new LeitungBuilder().withVertragsnummer(witaVertragsNr1).build())
                .addAkmTrLeitung(new LeitungBuilder().withVertragsnummer(witaVertragsNr2).build())
                .withAbgebenderEKP(CarrierCode.VODAFONE)
                .createWbciVorabstimmungForAccpetanceTestBuilder(builder1, WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.PROVIDERWECHSEL);

        AcceptanceTestWorkflow workflow1 = workflow().get();
        AcceptanceTestWorkflow workflow2 = workflow().get();
        workflow().select(workflow1);

        hurrican().createCbVorgangWithKlammerung(CBVorgang.TYP_ANBIETERWECHSEL, workflow1.createData(builder1), workflow2.createData(builder2));

        atlas().receiveOrder("PV").extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        workflow().doWithWorkflow(workflow -> assertKlammerung(workflow1, workflow2, workflow));
    }

    @CitrusTest
    public void neubestellungMitTerminverschiebungAndWbci() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_03, WitaCdmVersion.V1);

        LocalDate tvDate = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(26);

        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        AcceptanceTestDataBuilder builder = createBuilderForWbci(wbciTestDataBuilder.getWbciVorabstimmungsId())
                .withUserName(TAL_WITA_WBCI_CONFORM_03);

        VorabstimmungsAnfrage va = wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(builder, WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG);

        workflow().select(workflow().get()).sendBereitstellung(TAL_WITA_WBCI_CONFORM_03);

        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(workflow -> assertFalse(workflow.getCbVorgang().isKlaerfall()));

        action(new AbstractWitaWorkflowTestAction("Change Wechseltermin") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                va.getWbciGeschaeftsfall().setWechseltermin(tvDate);
                wbciDao.store(va);
            }
        });

        workflow().sendTv(tvDate);
        atlas().receiveNotification("TV").extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE_TV);

        atlas().sendNotification("ABM_2");
        workflow().waitForABM();

        workflow().doWithWorkflow(workflow -> assertFalse(workflow.getCbVorgang().isKlaerfall()));
    }

    @CitrusTest
    public void neubestellungMitTerminverschiebungAndWrongWbciWechseltermin() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_04, WitaCdmVersion.V1);

        final LocalDate tvDate = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(26);
        final LocalDate wbciDate = DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(20);

        AcceptanceTestWorkflow workflow = workflow().get();

        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        AcceptanceTestDataBuilder builder = createBuilderForWbci(wbciTestDataBuilder.getWbciVorabstimmungsId())
                .withUserName(TAL_WITA_WBCI_CONFORM_04);

        VorabstimmungsAnfrage va = wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(builder, WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG);

        workflow().select(workflow).send(workflow.createData(builder), CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> assertFalse(wf.getCbVorgang().isKlaerfall()));

        action(new AbstractWitaWorkflowTestAction("Change Wechseltermin") {
            @Override
            protected void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                va.getWbciGeschaeftsfall().setWechseltermin(wbciDate);
                wbciDao.store(va);
            }
        });

        action(new AbstractWitaWorkflowTestAction("sendTv") {
            @Override
            public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) throws Exception {
                try {
                    workflow.sendTv(tvDate);
                    Assert.fail("Expected WbciValidationException");
                }
                catch (Exception e) {
                    if (!(e instanceof WbciValidationException)) {
                        Assert.fail("Expected WbciValidationException");
                    }
                }
            }
        });
    }

    @CitrusTest
    public void abmWithInvalidDate() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_05, WitaCdmVersion.V1);

        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        AcceptanceTestWorkflow workflow = workflow().get();

        AcceptanceTestDataBuilder builder = createBuilderForWbci(wbciTestDataBuilder.getWbciVorabstimmungsId())
                .withUserName(TAL_WITA_WBCI_CONFROM_05);

        wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(builder, WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG);

        // set in XMl template of the simulator
        LocalDate abbmDate = DateCalculationHelper.getDateInWorkingDaysFromNow(25).toLocalDate();

        workflow().select(workflow).send(workflow.createData(builder), CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();

        workflow().doWithWorkflow(wf -> assertTrue(wf.getCbVorgang().isKlaerfall(),
                "Der angegebene Termin '"
                        + abbmDate.format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR))
                        + "' stimmt nicht mit dem vorabgestimmten Termin '"
                        + wbciTestDataBuilder.getWechseltermin().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DAY_MONTH_YEAR))
                        + "' der WBCI-Vorabstimmung '"
                        + wbciTestDataBuilder.getWbciVorabstimmungsId()
                        + "' .*berein! \\(.*\\)"));
    }

    /**
     * Testet die automatisierte WITA TV Prozessierung nach Erhalt einer WBCI ERLM-TV (M-net = aufnehmend).
     * <p/>
     * WBCI Entitaeten werden manuell angelegt (VA, TV und ERLM-TV). <br/> WITA-Workflow (Neubestellung) wird getriggert
     * bis zur ersten ABM; im Anschluss wird die ERLM-TV Prozessierung gestartet (als Ersatz fuer den Scheduler-Aufruf).
     * Es wird dann geprueft, ob die richtige WITA TV erstellt wird.
     *
     * @throws Exception
     */
    @CitrusTest
    public void sendTvAfterWbciErlmTvProcessing() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_06, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();

        // automatable ERLM-TVs disablen
        disableErlmTvAutomationCandidates();

        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        AcceptanceTestDataBuilder builder = createBuilderForWbci(wbciTestDataBuilder.getWbciVorabstimmungsId())
                .withUserName(WitaSimulatorTestUser.TAL_WITA_WBCI_CONFORM_06);

        VorabstimmungsAnfrage va = wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(builder, WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG);
        LOGGER.info(String.format("WBCI VA-Id: %s", va.getVorabstimmungsId()));

        workflow().select(workflow).send(workflow.createData(builder), CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> assertFalse(wf.getCbVorgang().isKlaerfall()));

        //        LOGGER.info(String.format("WITA ext. Auftragsnummer: %s", accTestWorkflow.getCbVorgang().getCarrierRefNr()));

        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder<>()
                .withTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(30))
                .withVorabstimmungsIdRef(va.getVorabstimmungsId())
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        // TV-Request generieren und speichern
        workflow().doWithWorkflow(wf -> {
            tv.setWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
            tv.setRequestStatus(WbciRequestStatus.TV_ERLM_EMPFANGEN);
            wbciDao.store(tv);
        });

        // TV Termin auf WBCI GF schreiben und GF als automatisierbar flaggen
        workflow().doWithWorkflow(wf -> {
            va.getWbciGeschaeftsfall().setAutomatable(Boolean.TRUE);
            va.getWbciGeschaeftsfall().setWechseltermin(tv.getTvTermin());
            wbciDao.store(va);
        });

        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder()
                .withVorabstimmungsIdRef(tv.getVorabstimmungsId())
                .withWechseltermin(tv.getTvTermin())
                .buildValidForTv(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        // ERLM-TV meldung generieren und speichern
        workflow().doWithWorkflow(wf -> {
            erlmTv.setAbsender(CarrierCode.DTAG);
            erlmTv.setWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
            erlmTv.setAenderungsIdRef(tv.getAenderungsId());
            wbciDao.store(erlmTv);
            wbciDao.flushSession();
        });

        variables().add(VariableNames.PRE_AGREEMENT_ID, va.getVorabstimmungsId());
        workflow().doWithWorkflow(AcceptanceTestWorkflow::startWbciErlmTvAutoProcessing);

        // WITA TV und ABM pruefen
        atlas().receiveNotification("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), "tvRequestedCustomerDate");
        atlas().sendNotification("ABM_2");

        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> assertFalse(wf.getCbVorgang().isKlaerfall()));

        workflow().doWithWorkflow(wf -> {
            WbciGeschaeftsfall wbciGf = wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId());
            assertAutomationTaskExist(wbciGf, new AutomationTaskTestBuilder()
                    .withMeldung(erlmTv)
                    .withName(AutomationTask.TaskName.WITA_SEND_TV)
                    .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));
        });
    }

    /**
     * Testet die automatisierte WITA AKM-TR Prozessierung (M-net = aufnehmend).
     * <p/>
     * WBCI Entitaeten werden manuell angelegt (VA, RUEM-VA, AKM-TR). <br/>
     * <p/>
     * Durch die Triggerung der AKM-TR Prozessierung muss von Hurrican eine WITA Neubestellung erstellt und an den
     * Simulator geschickt werden.
     *
     * @throws Exception
     */
    @CitrusTest
    public void sendWitaNeuAfterWbciAkmTrProcessing() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_07, WitaCdmVersion.V1);

        AcceptanceTestWorkflow workflow = workflow().get();
        workflow().select(workflow);

        // automatable AKM-TRs disablen
        disableAkmTrAutomationCandidates();

        AKUser user = workflow.createUser(WitaSimulatorTestUser.TAL_WITA_WBCI_CONFORM_07.getName());

        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        GeneratedTaifunData taifunData = getTaifunDataFactory().surfAndFonWithDns(1);
        AcceptanceTestDataBuilder builder = createBuilderForWbci(wbciTestDataBuilder.getWbciVorabstimmungsId(), taifunData)
                .withCancelHurricanOrdersWithSameTaifunId()
                .withUserName(WitaSimulatorTestUser.TAL_WITA_WBCI_CONFORM_07);
        CreatedData createdData = workflow.createData(builder);

        VorabstimmungsAnfrage va = wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(
                builder,
                WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_ORN,
                de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG);
        va.getWbciGeschaeftsfall().setAutomatable(Boolean.TRUE);
        va.getWbciGeschaeftsfall().setUserId(user.getId());
        va.getWbciGeschaeftsfall().setAuftragId(createdData.auftragDaten.getAuftragId());
        va.getWbciGeschaeftsfall().setBillingOrderNoOrig(createdData.auftragDaten.getAuftragNoOrig());
        va.setRequestStatus(WbciRequestStatus.AKM_TR_VERSENDET);
        wbciDao.store(va);
        LOGGER.info(String.format("WBCI VA-Id: %s", va.getVorabstimmungsId()));

        UebernahmeRessourceMeldung akmTr = wbciTestDataBuilder.getAkmTr();
        akmTr.setLeitungen(null);
        akmTr.setUebernahme(false);
        wbciDao.store(akmTr);
        wbciDao.flushSession();

        workflow().doWithWorkflow(wf -> wf.startWbciAkmTrAutoProcessing(user));

        workflow().doWithWorkflow(wf -> {
            WitaCBVorgang createdCbv = wf.getCbVorgangCreatedForVaId(va.getVorabstimmungsId());
            assertNotNull(createdCbv,
                    String.format("Es konnte kein CBVorgang zur VA-Id %s ermittelt werden!", va.getVorabstimmungsId()));
        });

        atlas().receiveOrderWithRequestedCustomerDate();
        atlas().sendNotificationWithNewVariables("QEB");

        // WITA TV und ABM pruefen
        workflow().doWithWorkflow(wf -> {
            WitaCBVorgang createdCbv = wf.getCbVorgangCreatedForVaId(va.getVorabstimmungsId());
            wf.waitForQEB(createdCbv);
        });

        workflow().doWithWorkflow(wf -> {
            WbciGeschaeftsfall wbciGf = wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId());
            assertAutomationTaskExist(wbciGf, new AutomationTaskTestBuilder()
                    .withMeldung(akmTr)
                    .withName(AutomationTask.TaskName.WITA_SEND_NEUBESTELLUNG)
                    .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN));
        });
    }

    /**
     * Testet die automatisierte WITA Storno Prozessierung nach Erhalt einer WBCI ERLM auf eine STR-AUFH (M-net =
     * aufnehmend).
     * <p/>
     * WBCI Entitaeten werden manuell angelegt (VA, STR-AUFH und ERLM)! <br/> WITA-Workflow (Neubestellung) wird
     * getriggert bis zur ersten ABM; im Anschluss wird die STR-AUFH ERLM Prozessierung gestartet (als Ersatz fuer den
     * Scheduler-Aufruf). Es wird dann geprueft, ob die richtige WITA Storno erstellt wird.
     *
     * @throws Exception
     */
    @CitrusTest
    public void sendStornoAfterWbciStrAufhErlmProcessing() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_WITA_WBCI_CONFORM_08, WitaCdmVersion.V1);

        // automatable STR-AUFH ERLMs disablen
        disableStrAufhErlmAutomationCandidates();

        WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        AcceptanceTestDataBuilder builder = createBuilderForWbci(wbciTestDataBuilder.getWbciVorabstimmungsId())
                .withUserName(WitaSimulatorTestUser.TAL_WITA_WBCI_CONFORM_08);

        VorabstimmungsAnfrage va = wbciTestDataBuilder.createWbciVorabstimmungForAccpetanceTestBuilder(builder, WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG);
        LOGGER.info(String.format("WBCI VA-Id: %s", va.getVorabstimmungsId()));

        AcceptanceTestWorkflow workflow = workflow().get();
        workflow().select(workflow).send(workflow.createData(builder), CBVorgang.TYP_NEU);

        atlas().receiveOrderWithRequestedCustomerDate();
        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);

        workflow().waitForQEB();

        atlas().sendNotification("ABM");

        workflow().waitForABM();
        workflow().doWithWorkflow(wf -> {
            WitaCBVorgang cbVorgang = wf.getCbVorgang();
            assertFalse(cbVorgang.isKlaerfall());
            LOGGER.info(String.format("WITA ext. Auftragsnummer: %s", cbVorgang.getCarrierRefNr()));
        });

        // Storno-Request generieren und speichern
        StornoAufhebungAufAnfrage storno = new StornoAufhebungAufAnfrageTestBuilder<>()
                .withVorabstimmungsIdRef(va.getVorabstimmungsId())
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        storno.setWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        storno.setRequestStatus(WbciRequestStatus.STORNO_ERLM_EMPFANGEN);

        workflow().doWithWorkflow(wf -> wbciDao.store(storno));

        // WBCI GF als automatisierbar flaggen
        va.getWbciGeschaeftsfall().setAutomatable(Boolean.TRUE);
        workflow().doWithWorkflow(wf -> wbciDao.store(va));

        // ERLM zu STR-AUFH generieren und speichern
        ErledigtmeldungStornoAuf erlm = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .withVorabstimmungsIdRef(va.getVorabstimmungsId())
                .buildValidForStorno(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        erlm.setAbsender(CarrierCode.DTAG);
        erlm.setWbciGeschaeftsfall(va.getWbciGeschaeftsfall());
        erlm.setAenderungsIdRef(storno.getAenderungsId());

        workflow().doWithWorkflow(wf -> {
            wbciDao.store(erlm);
            wbciDao.flushSession();
        });

        workflow().doWithWorkflow(AcceptanceTestWorkflow::startWbciStrAufhErlmAutoProcessing);

        atlas().receiveNotification("STORNO");
        atlas().sendNotification("ERLM");

        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> assertFalse(wf.getCbVorgang().isKlaerfall()));

        workflow().doWithWorkflow(wf -> {
            WbciGeschaeftsfall wbciGf = wbciDao.findWbciGeschaeftsfall(va.getVorabstimmungsId());
            assertAutomationTaskExist(wbciGf, new AutomationTaskTestBuilder()
                    .withMeldung(erlm)
                    .withName(AutomationTask.TaskName.WITA_SEND_STORNO)
                    .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));
        });
    }

    private AcceptanceTestDataBuilder createBuilderPVL(String wbciVorabstimmungsId) throws FindException {
        return createBuilderForWbci(wbciVorabstimmungsId)
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, Carrier.ID_QSC)
                .withUserName(TAL_WITA_WBCI_CONFORM_02);
    }

    private AcceptanceTestDataBuilder createBuilderForWbci(String wbciVorabstimmungsId) {
        return createBuilderForWbci(wbciVorabstimmungsId, null);
    }

    private AcceptanceTestDataBuilder createBuilderForWbci(String wbciVorabstimmungsId, GeneratedTaifunData taifunData) {
        final LocalDateTime vorbageMnet = DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                DateCalculationHelper.addWorkingDays(LocalDate.now(), 21)).atStartOfDay();
        AcceptanceTestDataBuilder acceptanceTestDataBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(vorbageMnet)
                .withVorabstimmungsId(wbciVorabstimmungsId);
        if (taifunData != null) {
            acceptanceTestDataBuilder.withTaifunData(taifunData);
        }
        return acceptanceTestDataBuilder;
    }

    private AcceptanceTestDataBuilder createBuilderVbl(GeneratedTaifunData taifunData, String wbciVorabstimmungsId, WitaSimulatorTestUser simulatorTestUser) throws FindException {
        return workflow().getTestDataBuilder()
                .withTaifunData(taifunData)
                .withVorabstimmungEinzelanschluss(ProduktGruppe.DTAG_ANY, ID_DTAG)
                .withCreateAnschlussinhaberAddress(false)
                .withVorgabeMnet(getPortDate())
                .withUserName(simulatorTestUser)
                .withCBVorgangMontagehinweis("#OSL_Bereit_MM#")
                .withVorabstimmungsId(wbciVorabstimmungsId);
    }

    /**
     * Ermittelt alle automatisierbaren ERLM-TVs und setzt fuer diese das Klaerfall-Flag. Dies dient dazu, dass fuer
     * einen Acc-Test nur ein spezieller WBCI GF im entsprechenden Status vorhanden ist.
     */
    private void disableErlmTvAutomationCandidates() {
        List<ErledigtmeldungTerminverschiebung> erlmTvs = wbciDao.findAutomateableTvErlmsForWitaProcessing(null);
        markGfsAsKlaerfall(erlmTvs);
    }

    /**
     * Ermittelt alle automatisierbaren AKM-TRs und setzt fuer diese das Klaerfall-Flag. Dies dient dazu, dass fuer
     * einen Acc-Test nur ein spezieller WBCI GF im entsprechenden Status vorhanden ist.
     */
    private void disableAkmTrAutomationCandidates() {
        List<UebernahmeRessourceMeldung> akmTrs = wbciDao.findAutomatableAkmTRsForWitaProcesing(null);
        markGfsAsKlaerfall(akmTrs);
    }

    /**
     * Ermittelt alle automatisierbaren STR-AUFH ERLMs und setzt fuer diese das Klaerfall-Flag. Dies dient dazu, dass
     * fuer einen Acc-Test nur ein spezieller WBCI GF im entsprechenden Status vorhanden ist.
     */
    private void disableStrAufhErlmAutomationCandidates() {
        List<ErledigtmeldungStornoAuf> erlms = wbciDao.findAutomateableStrAufhErlmsForWitaProcessing(null);
        markGfsAsKlaerfall(erlms);
    }

    private void markGfsAsKlaerfall(List<? extends Meldung> meldungen) {
        if (CollectionUtils.isNotEmpty(meldungen)) {
            for (Meldung meldung : meldungen) {
                WbciGeschaeftsfall gf = wbciDao.findWbciGeschaeftsfall(meldung.getVorabstimmungsId());
                gf.setKlaerfall(Boolean.TRUE);

                StringBuilder comment = new StringBuilder();
                if (gf.getBemerkungen() != null) {
                    comment.append(gf.getBemerkungen());
                    comment.append(System.lineSeparator());
                }
                comment.append("Klaerfall set by Acceptance-Test");

                gf.setBemerkungen(comment.toString());
                wbciDao.store(gf);
            }
        }
    }

    private void assertAutomationTaskExist(WbciGeschaeftsfall wbciGeschaeftsfall, AutomationTask example) {
        boolean found = false;
        if (wbciGeschaeftsfall.getAutomationTasks() != null) {
            for (AutomationTask automationTask : wbciGeschaeftsfall.getAutomationTasks()) {
                if (automationTask.getName().equals(example.getName())
                        && automationTask.getMeldung() != null
                        && automationTask.getMeldung().getId().equals(example.getMeldung().getId())) {
                    found = true;
                }
            }
        }

        Assert.assertTrue(found, "No automation task with given example found. Example: " + example);
    }
}

/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2011 10:47:06
 */
package de.mnet.wita.acceptance.common;

import static de.augustakom.common.tools.matcher.RetryMatcher.*;
import static de.mnet.wita.acceptance.common.AbstractWitaAcceptanceBaseTest.*;
import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.tools.session.UserSessionHolder;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.AbbmPvMeldungsCode;
import de.mnet.wita.AbmMeldungsCode;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.bpm.AbgebendPvWorkflowService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.auftrag.SchaltungKupfer;
import de.mnet.wita.message.auftrag.StandortKollokation;
import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaSendMessageService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.WitaUsertaskService;

@SuppressWarnings("unused")
public class AcceptanceTestWorkflow {

    private static final Logger LOGGER = Logger.getLogger(AcceptanceTestWorkflow.class);
    public Long cbVorgangId;
    @Autowired
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private CarrierElTALService carrierElTalService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private AKUserService akUserService;
    @Autowired
    private WitaUsertaskService witaUsertaskService;

    @Autowired
    private Provider<AcceptanceTestDataBuilder> testDataBuilderProvider;
    @Autowired
    private AcceptanceTestWorkflowService acceptanceTestWorkflowService;
    @Autowired
    private AbgebendPvWorkflowService abgebendPvWorkflowService;
    @Autowired
    private MwfEntityService mwfEntityService;
    @Autowired
    private TalOrderWorkflowService talOrderWorkflowService;
    @Autowired
    private WitaSendMessageService witaSendMessageService;
    @Autowired
    private CommonWorkflowService commonWorkflowService;

    private int expectedNumberOfAbms;
    private boolean expectSecondTam = false;
    private boolean expectSecondAkmPv = false;
    // fuer abgebende PV Testfaelle, kann aus empfangenen Nachrichten geholt oder explizit generiert werden.
    private String pvVertragsnummer;
    private CreatedData createdData;

    public static void assertNotEmpty(String toCheck) {
        assertTrue(StringUtils.isNotBlank(toCheck));
    }

    public void reset() {
        cbVorgangId = null;
        expectedNumberOfAbms = 0;
        expectSecondTam = false;
        expectSecondAkmPv = false;
        createdData = null;
        pvVertragsnummer = null;
    }

    public AcceptanceTestWorkflow sendBereitstellung(AcceptanceTestDataBuilder builder) throws Exception {
        return send(builder, CBVorgang.TYP_NEU);
    }

    public AcceptanceTestWorkflow sendBereitstellung(CreatedData createdData) throws Exception {
        this.createdData = createdData;
        return send(CBVorgang.TYP_NEU);
    }

    public AcceptanceTestWorkflow sendBereitstellung(WitaSimulatorTestUser userName) throws Exception {
        AcceptanceTestDataBuilder builder = testDataBuilderProvider.get().withUserName(userName);
        return sendBereitstellung(builder);
    }

    /**
     * Verwendet um einen Auftrag an dem Workflow vorbeizuschicken für den Testfall: Externe Auftragsnummer gleich
     * Auftrag aus Vorbedingung
     */
    public AcceptanceTestWorkflow sendBereitstellungMitGleicherExternerAuftragsnummer() throws Exception {
        // Schlechtfall - sende Auftrag am Workflow vorbei
        talOrderWorkflowService.restartProcessInstance(getCbVorgang());
        Auftrag auftrag = mwfEntityService.getAuftragOfCbVorgang(cbVorgangId);
        auftrag.getGeschaeftsfall().getGfAnsprechpartner().getAuftragsmanagement()
                .setNachname(WitaSimulatorTestUser.TAL_BEREIT_NEU_14b.getName());
        witaSendMessageService.sendAndProcessMessage(auftrag);
        return this;
    }

    public AcceptanceTestWorkflow sendLmaeForDtagPortChange(WitaSimulatorTestUser userName, Uebertragungsverfahren uetv)
            throws Exception {
        Preconditions.checkNotNull(createdData);
        Preconditions.checkNotNull(uetv);
        LocalDateTime vorgabeMnet = DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay();

        AKUser user = new AKUserBuilder().init().withRandomLoginName().withName(userName.getName())
                .withDepartmentId(AKDepartment.DEP_AM).setPersist(false).build();
        akUserService.save(user);

        Carrierbestellung cb = carrierService.findCB(createdData.carrierbestellung.getId());

        CBVorgang cbVorgang = witaTalOrderService.changeUebertragungsverfahren(cb, createdData.dtagPort,
                DateConverterUtils.asDate(vorgabeMnet), uetv, user);

        cbVorgangId = cbVorgang.getId();
        expectedNumberOfAbms = 0;
        assertGeschaeftsfallTyp(GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG);
        return this;
    }

    public AcceptanceTestWorkflow sendLmae(WitaSimulatorTestUser userName, Uebertragungsverfahren uetv,
            ProduktBezeichner expectedProduktBezeichner) throws Exception {
        return sendLmae(userName, uetv, DateCalculationHelper.addWorkingDays(LocalDate.now(), 21).atStartOfDay(),
                expectedProduktBezeichner);
    }

    public AcceptanceTestWorkflow sendLmae(WitaSimulatorTestUser userName, Uebertragungsverfahren uetv,
            LocalDateTime vorgabeMnet, ProduktBezeichner expectedProduktBezeichner) throws Exception {
        Preconditions.checkNotNull(createdData);
        Preconditions.checkNotNull(uetv);
        AcceptanceTestDataBuilder builder = testDataBuilderProvider.get().withUserName(userName)
                .withVorgabeMnet(vorgabeMnet).withCarrierbestellungAuftragId4TalNa(createdData.auftrag.getAuftragId())
                .withUetv(uetv);

        // 4-Draht-Handling
        if (createdData.vierDraht) {
            builder.withVierDraht("02", "02");
        }

        AcceptanceTestWorkflow workflow = send(builder, CBVorgang.TYP_PORTWECHSEL);
        workflow.assertGeschaeftsfallTyp(GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG);
        workflow.assertProduktBezeichnerEquals(expectedProduktBezeichner);
        return workflow;
    }

    public AcceptanceTestWorkflow sendLmae(AcceptanceTestDataBuilder builder,
            ProduktBezeichner expectedProduktBezeichner) throws Exception {
        AcceptanceTestWorkflow workflow = send(builder, CBVorgang.TYP_PORTWECHSEL);
        workflow.assertGeschaeftsfallTyp(GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG);
        workflow.assertProduktBezeichnerEquals(expectedProduktBezeichner);
        return workflow;
    }

    public AcceptanceTestWorkflow sendLae(AcceptanceTestDataBuilder builder, ProduktBezeichner expectedProduktBezeichner)
            throws Exception {
        AcceptanceTestWorkflow workflow = send(builder, CBVorgang.TYP_PORTWECHSEL);
        workflow.assertGeschaeftsfallTyp(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG);
        workflow.assertProduktBezeichnerEquals(expectedProduktBezeichner);
        return workflow;
    }

    public AcceptanceTestWorkflow sendKueKd(AcceptanceTestDataBuilder builder) throws Exception {
        return send(builder, CBVorgang.TYP_KUENDIGUNG);
    }

    public AcceptanceTestWorkflow sendPv(AcceptanceTestDataBuilder builder) throws Exception {
        final LocalDate vorgabeMnet =
                DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(builder.getVorgabeMnet().toLocalDate());
        builder.withVorgabeMnet(vorgabeMnet.atStartOfDay());
        return send(builder, CBVorgang.TYP_ANBIETERWECHSEL);
    }

    /**
     * Annahme: QSC ist auf WITA
     */
    public AcceptanceTestWorkflow sendPv(WitaSimulatorTestUser userName) throws Exception {
        final LocalDate vorbageMnet = DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                DateCalculationHelper.addWorkingDays(LocalDate.now(), 21));
        AcceptanceTestDataBuilder builder = testDataBuilderProvider.get()
                .withVorgabeMnet(vorbageMnet.atStartOfDay())
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, Carrier.ID_QSC).withUserName(userName);
        return sendPv(builder);
    }

    public AcceptanceTestWorkflow sendRexMk(AcceptanceTestDataBuilder builder) throws Exception {
        return send(builder, CBVorgang.TYP_REX_MK);
    }

    public AcceptanceTestWorkflow sendSerPow(AcceptanceTestDataBuilder builder,
            ProduktBezeichner expectedProduktBezeichner) throws Exception {
        AcceptanceTestWorkflow workflow = send(builder, CBVorgang.TYP_PORTWECHSEL);
        workflow.assertGeschaeftsfallTyp(GeschaeftsfallTyp.PORTWECHSEL);
        workflow.assertProduktBezeichnerEquals(expectedProduktBezeichner);
        return workflow;
    }

    public AcceptanceTestWorkflow sendRuemPv() throws Exception {
        String vertragsNummer = getPvVertragsnummer();
        assertNotEmpty(vertragsNummer);

        ProcessInstance pi = abgebendPvWorkflowService.retrieveRunningProcessInstance(vertragsNummer);
        assertNotNull(pi);

        LOGGER.info("Send RUEM-PV for vertragsNummer " + vertragsNummer + "; BusinessKey " + pi.getBusinessKey());
        witaTalOrderService.sendPositiveRuemPv(pi.getBusinessKey(), RuemPvAntwortCode.OK, null, null);
        return this;
    }

    public AcceptanceTestWorkflow sendNegativeRuemPv(RuemPvAntwortCode antwortCode, String antwortText)
            throws Exception {
        String vertragsNummer = getPvVertragsnummer();
        assertNotEmpty(vertragsNummer);

        ProcessInstance pi = abgebendPvWorkflowService.retrieveRunningProcessInstance(vertragsNummer);
        assertNotNull(pi);

        LOGGER.info("Send negative RUEM-PV for vertragsNummer " + vertragsNummer + "; BusinessKey "
                + pi.getBusinessKey());
        witaTalOrderService.sendNegativeRuemPv(pi.getBusinessKey(), antwortCode, antwortText, null);

        return this;
    }

    public AcceptanceTestWorkflow send(AcceptanceTestDataBuilder builder, Long cbVorgangTyp) throws Exception {
        createData(builder);
        return send(cbVorgangTyp);
    }

    public AcceptanceTestWorkflow send(Long cbVorgangTyp) throws Exception {
        CBVorgang cbVorgang = acceptanceTestWorkflowService.createCBVorgang(createdData, cbVorgangTyp);

        this.cbVorgangId = cbVorgang.getId();
        this.expectedNumberOfAbms = 0;
        return this;
    }

    public CreatedData createData(AcceptanceTestDataBuilder builder) throws Exception {
        Preconditions.checkNotNull(carrierElTalService);

        createdData = builder.build(getSessionId());
        assertKundenwunschtermin();
        return createdData;
    }

    public AcceptanceTestWorkflow sendTv() throws Exception {
        Preconditions.checkNotNull(cbVorgangId);
        return sendTv(DateCalculationHelper.addWorkingDays(DateConverterUtils.asLocalDate(getCbVorgang().getVorgabeMnet()), 14));
    }

    public AcceptanceTestWorkflow sendTv(LocalDate newKwt) throws Exception {
        Preconditions.checkNotNull(newKwt);
        LOGGER.info("Send TerminVerschiebung for cbVorgangId " + cbVorgangId);

        final LocalDate date = DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(newKwt);
        witaTalOrderService.doTerminverschiebung(cbVorgangId, date, getCreatedData().user, true, null, TV_60_TAGE);
        return this;
    }

    /**
     * Notwendig fur den Fall wo {@link TerminVerschiebung} nach {@link ErledigtMeldung} geschickt werden muss; direkt
     * am Workflow vorbei.
     */
    public AcceptanceTestWorkflow sendTvDirectlyAndNotViaWorkflow() throws Exception {
        LOGGER.info("Send TerminVerschiebung directly (not via Workflow) for cbVorgangId " + cbVorgangId);

        // Schlechtfall - sende TV am Workflow vorbei
        talOrderWorkflowService.restartProcessInstance(getCbVorgang());
        List<TerminVerschiebung> tvs = mwfEntityService.getTerminVerschiebungenOfCbVorgang(cbVorgangId);
        TerminVerschiebung tv = tvs.get(0);
        tv.setTermin(DateCalculationHelper.addWorkingDays(tv.getTermin(), 14));
        witaSendMessageService.sendAndProcessMessage(tv);

        return this;
    }

    public AcceptanceTestWorkflow sendStorno() throws Exception {
        LOGGER.info("Send storno for cbVorgangId " + cbVorgangId + " ");

        witaTalOrderService.doStorno(cbVorgangId, getCreatedData().user);
        return this;
    }

    /**
     * Notwendig fur den Fall wo {@link Storno} nach {@link ErledigtMeldung} geschickt werden muss; direkt am Workflow
     * vorbei.
     */
    public AcceptanceTestWorkflow sendStornoDirectlyAndNotViaWorkflow() throws Exception {
        LOGGER.info("Send Storno directly (not via Workflow) for cbVorgangId " + cbVorgangId);

        // Schlechtfall - sende Storno am Workflow vorbei
        talOrderWorkflowService.restartProcessInstance(getCbVorgang());
        Storno storno = new Storno(mwfEntityService.getAuftragOfCbVorgang(cbVorgangId));
        witaSendMessageService.sendAndProcessMessage(storno);
        return this;
    }

    public AcceptanceTestWorkflow sendErlmK() throws Exception {
        LOGGER.info("Send Erlm-K for cbVorgangId " + cbVorgangId + " ");
        witaTalOrderService.sendErlmk(cbVorgangId, getCreatedData().user);
        return this;
    }

    public AcceptanceTestWorkflow sendVbl(AcceptanceTestDataBuilder builder) throws Exception {
        final LocalDate vorgabeMnet =
                DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(builder.getVorgabeMnet().toLocalDate());
        builder.withVorgabeMnet(vorgabeMnet.atStartOfDay());
        return send(builder, CBVorgang.TYP_ANBIETERWECHSEL);
    }

    public AcceptanceTestWorkflow waitForQEB() throws Exception {
        acceptanceTestWorkflowService.waitForQEB(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForQEB(WitaCBVorgang cbVorgang) throws Exception {
        acceptanceTestWorkflowService.waitForQEB(cbVorgang);
        return this;
    }

    public AcceptanceTestWorkflow waitForVZM() throws Exception {
        acceptanceTestWorkflowService.waitForVZM(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForABBM() throws Exception {
        acceptanceTestWorkflowService.waitForABBM(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForABM(AbmMeldungsCode... abmMeldungsCodes) throws Exception {
        return waitForABM(null, null, abmMeldungsCodes);
    }

    public AcceptanceTestWorkflow waitForTAM() throws Exception {
        if (expectSecondTam) {
            acceptanceTestWorkflowService.waitForSecondTAM(getCbVorgang());
        }
        else {
            acceptanceTestWorkflowService.waitForTAM(getCbVorgang());
            expectSecondTam = true;
        }
        return this;
    }

    public AcceptanceTestWorkflow waitForSecondTAM() throws Exception {
        acceptanceTestWorkflowService.waitForSecondTAM(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForMTAM() throws Exception {
        acceptanceTestWorkflowService.waitForMTAM(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForABM(LocalDateTime changeDate, DateCheck checkType, AbmMeldungsCode... meldungsCodes)
            throws Exception {
        expectedNumberOfAbms++;
        LOGGER.info("wait for ABM #" + expectedNumberOfAbms);
        acceptanceTestWorkflowService.waitForABM(getCbVorgang(), changeDate, checkType, expectedNumberOfAbms,
                meldungsCodes);
        return this;
    }

    public AcceptanceTestWorkflow waitForABBM(AbbmMeldungsCode meldungscode) throws Exception {
        acceptanceTestWorkflowService.waitForABBM(getCbVorgang(), meldungscode);
        return this;
    }

    public AcceptanceTestWorkflow waitForNonClosingABBM(AbbmMeldungsCode meldungscode) throws Exception {
        acceptanceTestWorkflowService.waitForNonClosingABBM(getCbVorgang(), meldungscode);
        return this;
    }

    public AcceptanceTestWorkflow waitForERLM() throws Exception {
        acceptanceTestWorkflowService.waitForERLM(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForERLMWorkflowClosed() throws Exception {
        acceptanceTestWorkflowService.waitForERLMWorkflowClosed(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForERLMInWorkflowError() throws Exception {
        acceptanceTestWorkflowService.waitForERLM(getCbVorgang(), true);
        return this;
    }

    public AcceptanceTestWorkflow waitForENTM() throws Exception {
        acceptanceTestWorkflowService.waitForENTM(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow wait(int millis) throws Exception {
        LOGGER.info("wait for " + millis + "ms");
        Thread.sleep(millis);
        return this;
    }

    public AcceptanceTestWorkflow waitForENTMInWorkflowError() throws Exception {
        acceptanceTestWorkflowService.waitForENTM(getCbVorgang(), true);
        return this;
    }

    public AcceptanceTestWorkflow waitForENTMWorkflowClosed() throws Exception {
        acceptanceTestWorkflowService.waitForENTMWorkflowClosed(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForAkmPv() throws Exception {
        if (expectSecondAkmPv) {
            acceptanceTestWorkflowService.waitForAkmPv(getCbVorgang(), 2);
        }
        else {
            acceptanceTestWorkflowService.waitForAkmPv(getCbVorgang());
            expectSecondAkmPv = true;
        }
        return this;

    }

    public AcceptanceTestWorkflow waitForSecondAkmPv() throws Exception {
        acceptanceTestWorkflowService.waitForAkmPv(getCbVorgang(), 2);
        return this;
    }

    public AcceptanceTestWorkflow waitForAutomaticRuemPv() throws Exception {
        acceptanceTestWorkflowService.waitForAutomaticRuemPv(getAkmPv());
        return this;
    }

    public AcceptanceTestWorkflow waitForAbmPv(int expectedNumberOfAbms) throws Exception {
        acceptanceTestWorkflowService.waitForAbmPv(getAkmPv(), expectedNumberOfAbms);
        return this;
    }

    public AcceptanceTestWorkflow waitForAbmPv() throws Exception {
        acceptanceTestWorkflowService.waitForAbmPv(getAkmPv());
        return this;
    }

    public AcceptanceTestWorkflow waitForVZMPV() throws Exception {
        acceptanceTestWorkflowService.waitForVZMPV(getAkmPv());
        return this;
    }

    public AcceptanceTestWorkflow waitForSecondAbmPv() throws Exception {
        acceptanceTestWorkflowService.waitForAbmPv(getAkmPv(), 2);
        return this;
    }

    public AcceptanceTestWorkflow waitForErlmPv() throws Exception {
        acceptanceTestWorkflowService.waitForErlmPv(getAkmPv());
        return this;
    }

    public AcceptanceTestWorkflow waitForErlmPvInWorkflowError() throws Exception {
        acceptanceTestWorkflowService.waitForErlmPv(getAkmPv(), true);
        return this;
    }

    public AcceptanceTestWorkflow waitForEntmPv() throws Exception {
        acceptanceTestWorkflowService.waitForEntmPv(getAkmPv());
        return this;
    }

    public AcceptanceTestWorkflow waitForEntmPvInWorkflowError() throws Exception {
        acceptanceTestWorkflowService.waitForEntmPv(getAkmPv(), true);
        return this;
    }

    public AcceptanceTestWorkflow waitForAbbmPv(AbbmPvMeldungsCode meldungscode) throws Exception {
        acceptanceTestWorkflowService.waitForAbbmPv(getAkmPv(), meldungscode);
        return this;
    }

    public AcceptanceTestWorkflow waitForIOArchiveEntry() throws Exception {
        acceptanceTestWorkflowService.waitForIOArchiveEntry(getCbVorgang());
        return this;
    }

    public AcceptanceTestWorkflow waitForIOArchiveEntry(final String extOrderNo, final GeschaeftsfallTyp requestGf,
            final MeldungsType requestTyp) throws Exception {
        return waitForIOArchiveEntry(extOrderNo, requestGf, requestTyp, null);
    }

    public AcceptanceTestWorkflow waitForIOArchiveEntry(final String extOrderNo, final GeschaeftsfallTyp requestGf,
            final MeldungsType requestTyp, final String requestMeldungscode)
            throws Exception {
        acceptanceTestWorkflowService.waitForIOArchiveEntry(extOrderNo, requestGf, requestTyp.toString(),
                requestMeldungscode);
        return this;
    }

    public AcceptanceTestWorkflow closeCbVorgang() throws Exception {
        witaTalOrderService.closeCBVorgang(cbVorgangId, getSessionId());
        return this;
    }

    public WitaCBVorgang getCbVorgang() throws FindException {
        Preconditions.checkNotNull(cbVorgangId);
        return (WitaCBVorgang) carrierElTalService.findCBVorgang(cbVorgangId);
    }

    public WitaCBVorgang getCbVorgangCreatedForVaId(String vaId) {
        return acceptanceTestWorkflowService.findAndWaitForCreatedCbVorgang(vaId);
    }

    public List<WitaCBVorgang> getCbVorgaenge4Klammerung() throws Exception {
        return witaTalOrderService.findCBVorgaenge4Klammer(getCbVorgang().getAuftragsKlammer(), getAuftrag().getId());
    }

    public Auftrag getAuftrag() {
        return mwfEntityService.getAuftragOfCbVorgang(cbVorgangId);
    }

    public List<Storno> getStornos() {
        return mwfEntityService.getStornosOfCbVorgang(cbVorgangId);
    }

    public List<TerminVerschiebung> getTerminVerschiebungen() {
        return mwfEntityService.getTerminVerschiebungenOfCbVorgang(cbVorgangId);
    }

    public Long getCbVorgangId() {
        return cbVorgangId;
    }

    private Long getSessionId() {
        return UserSessionHolder.sessionId;
    }

    public CreatedData getCreatedData() {
        return createdData;
    }

    protected String getWorkflowState() throws FindException {
        return commonWorkflowService.getWorkflowState(getCbVorgang().getCarrierRefNr());
    }


    private void assertKundenwunschtermin() {
        LocalDate today = LocalDate.now();
        LocalDate kwt = DateConverterUtils.asLocalDate(createdData.carrierbestellung.getVorgabedatum());
        assertTrue(DateCalculationHelper.addWorkingDays(today, 5).isBefore(kwt) && kwt.isBefore(DateCalculationHelper.asWorkingDay(today.plusDays(62))),
                "Kundenwunschtermin für KFT muss folgende Eigenschaft haben: heute+5Tage < KWT <= heute+60Tage");
    }

    public void assertWorkflowState(String state) throws FindException {
        assertEquals(getWorkflowState(), state);
    }

    public void assertStandortKundeSet() {
        StandortKunde standortKunde = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getStandortKunde();
        assertNotNull(standortKunde);

        Kundenname kundenname = standortKunde.getKundenname();
        assertNotNull(kundenname);
        assertNotNull(kundenname.getAnrede());
        if (kundenname instanceof Personenname) {
            assertNotEmpty(((Personenname) kundenname).getNachname());
        }
        else {
            assertNotEmpty(((Firmenname) kundenname).getErsterTeil());
        }
        assertNotEmpty(standortKunde.getStrassenname());
        assertNotEmpty(standortKunde.getHausnummer());
        assertNotEmpty(standortKunde.getLand());
        assertNotEmpty(standortKunde.getLageTAEDose());
        // Fehlt: Ortsteil, Gebäudeteil, Hausnummerzusatz
    }

    public void assertStandortKundeIsPerson() {
        StandortKunde standortKunde = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getStandortKunde();
        assertTrue(standortKunde.getKundenname() instanceof Personenname);
    }

    public void assertStandortKundeIsFirma() {
        StandortKunde standortKunde = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getStandortKunde();
        assertTrue(standortKunde.getKundenname() instanceof Firmenname);
    }

    public void assertStandortKollokationSet() {
        StandortKollokation standortKollokation = getAuftrag().getGeschaeftsfall().getAuftragsPosition()
                .getGeschaeftsfallProdukt().getStandortKollokation();
        assertNotNull(standortKollokation);
        assertNotNull(standortKollokation.getAsb());
        assertNotNull(standortKollokation.getOnkz());
    }

    public void assertStandortKundeOrtsteilSet() {
        StandortKunde standortKunde = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getStandortKunde();
        assertNotEmpty(standortKunde.getOrtsteil());
    }

    public void assertStandortKundeLageTaeSet() {
        StandortKunde standortKunde = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getStandortKunde();
        assertNotEmpty(standortKunde.getLageTAEDose());
    }

    public void assertStandortKundeLageTaeEquals(String expected) {
        StandortKunde standortKunde = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getStandortKunde();
        assertThat(standortKunde.getLageTAEDose(), equalTo(expected));
    }

    public void assertCountOfRequestsWithSameAuftragsKlammer(int expectedCount) {
        try {
            WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
            example.setAuftragsKlammer(getAuftrag().getAuftragsKenner().getAuftragsKlammer());
            List<WitaCBVorgang> cbVorgaenge = carrierElTalService.findCBVorgaengeByExample(example);
            assertThat(cbVorgaenge, hasSize(2));
        }
        catch (Exception e) {
            fail("Error validating AuftragsKlammer count!", e);
        }
    }

    public void assertSchaltangabenSet() {
        assertNotNull(getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getSchaltangaben());
    }

    public void assertUebertragungsverfahrenSet(Uebertragungsverfahren expected) {
        assertSchaltangabenSet();
        List<SchaltungKupfer> schaltungKupferList = getAuftrag().getGeschaeftsfall().getAuftragsPosition()
                .getGeschaeftsfallProdukt().getSchaltangaben().getSchaltungKupfer();
        assertNotNull(schaltungKupferList);
        SchaltungKupfer schaltungKupfer = schaltungKupferList.get(0);

        if (expected == null) {
            assertNotNull(schaltungKupfer.getUebertragungsverfahren());
        }
        else {
            assertEquals(schaltungKupfer.getUebertragungsverfahren(), expected);
        }
    }

    public void assertUebertragungsverfahrenSet() {
        assertUebertragungsverfahrenSet(null);
    }

    public void assertDtagPortChangeUebertragungsverfahrenSet(Uebertragungsverfahren newUetv,
            Uebertragungsverfahren oldUetv) {
        assertSchaltangabenSet();
        Auftragsposition auftragsPosition = getAuftrag().getGeschaeftsfall().getAuftragsPosition();

        // Auftragsposition haelt altes Uebertragungsverfahren
        List<SchaltungKupfer> schaltungKupferList = auftragsPosition.getGeschaeftsfallProdukt().getSchaltangaben()
                .getSchaltungKupfer();
        assertEquals(Iterables.getOnlyElement(schaltungKupferList).getUebertragungsverfahren().toString(),
                oldUetv.toString());

        // Subposition haelt neues Uebertragungsverfahren
        schaltungKupferList = auftragsPosition.getPosition().getGeschaeftsfallProdukt().getSchaltangaben()
                .getSchaltungKupfer();
        assertEquals(Iterables.getOnlyElement(schaltungKupferList).getUebertragungsverfahren().toString(),
                newUetv.toString());

    }

    public void assertBestandssucheSet() {
        BestandsSuche bestandsSuche = getBestandsSuche();
        assertNotNull(bestandsSuche);
    }

    public void assertBestandssucheEinzelanschlussSet() {
        BestandsSuche bestandsSuche = getBestandsSuche();
        assertNotNull(bestandsSuche);
        assertNotNull(bestandsSuche.getRufnummer());
        assertNull(bestandsSuche.getAnlagenAbfrageStelle());
        assertNull(bestandsSuche.getAnlagenDurchwahl());
        assertNull(bestandsSuche.getAnlagenOnkz());
    }

    public void assertBestandssucheAnlagenanschlussSet() {
        BestandsSuche bestandsSuche = getBestandsSuche();
        assertNotNull(bestandsSuche);
        assertNull(bestandsSuche.getOnkz());
        assertNull(bestandsSuche.getRufnummer());
        assertNotNull(bestandsSuche.getAnlagenAbfrageStelle());
        assertNotNull(bestandsSuche.getAnlagenDurchwahl());
        assertNotNull(bestandsSuche.getAnlagenOnkz());
    }

    private Montageleistung getMontageleistung() {
        return getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt().getMontageleistung();
    }

    public void assertMontageleistungHinweis(String expected) {
        Montageleistung montageleistung = getMontageleistung();
        assertEquals(montageleistung.getMontagehinweis(), expected);
    }

    public void assertMontageleistungSet() {
        assertNotNull(getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getMontageleistung());
    }

    public void assertAnlageSet(int count) {
        assertAnlageSet(getAuftrag(), count);
    }

    public void assertAnlageSetForOnlyStorno(int count) {
        assertAnlageSet(Iterables.getOnlyElement(mwfEntityService.getStornosOfCbVorgang(cbVorgangId)), count);
    }

    public void assertAnlageSetForOnlyTv(int count) {
        assertAnlageSet(Iterables.getOnlyElement(mwfEntityService.getTerminVerschiebungenOfCbVorgang(cbVorgangId)),
                count);
    }

    private void assertAnlageSet(MnetWitaRequest request, Anlage anlageToTest) {
        List<Anlage> anlagen = request.getGeschaeftsfall().getAnlagen();
        assertNotNull(anlagen, "Anlage(n) erwartet.");
        for (Anlage anlage : anlagen) {
            if (anlage.equals(anlageToTest)) {
                return;
            }
        }
        fail("Anlage mit ID " + anlageToTest.getId() + " erwartet.");
    }

    private void assertAnlageSet(MnetWitaRequest request, int count) {
        List<Anlage> anlagen = request.getGeschaeftsfall().getAnlagen();
        assertNotNull(anlagen, "Anlage(n) erwartet.");
        assertThat(count + " Anlage(n) erwartet.", anlagen, hasSize(count));
    }

    public void assertBktoFakturaSet() {
        assertNotEmpty(getAuftrag().getGeschaeftsfall().getBktoFatkura());
    }

    public void assertVormieterSet() {
        Vormieter vormieter = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getVormieter();
        assertNotNull(vormieter);
        assertNotNull(vormieter.getOnkz());
        assertNotEmpty(getAuftrag().getGeschaeftsfall().getBktoFatkura());
    }

    public void assertStateAndCbVorgang(WorkflowTaskName expectedWorkflowTaskName, Long expectedStatus,
            AenderungsKennzeichen expectedAenderungskennzeichen, UserTask.UserTaskStatus expectedUserTaskStatus) throws FindException {
        WitaCBVorgang cbVorgang = getCbVorgang();
        String carrierRefNr = (cbVorgang != null) ? cbVorgang.getCarrierRefNr() : "not defined";
        String detail = String.format("Workflow business key: %s", carrierRefNr);
        assertEquals(getWorkflowState(), expectedWorkflowTaskName.id, detail);
        assertEquals(cbVorgang.getStatus(), expectedStatus);
        assertEquals(cbVorgang.getAenderungsKennzeichen(), expectedAenderungskennzeichen);
        assertEquals(cbVorgang.getTamUserTask().getStatus(), expectedUserTaskStatus);
    }

    /*
    * Abfrage des Status hat bei testTvOnHoldAndStornoOnIt manchmal nicht richtig funktioniert; der Workflow-Status war
    * zum Zeitpunkt der Abfrage noch nicht umgeschrieben.
    * Durch den retry count mit kurzem sleep sollte dies nicht mehr auftreten.
    */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "BAD_PRACTICE", justification = "Exception at Thread.sleep can be ignored!")
    public void assertStateAndCbVorgangWithRetry(WorkflowTaskName expectedWorkflowTaskName, Long expectedStatus,
            AenderungsKennzeichen expectedAenderungskennzeichen, UserTask.UserTaskStatus expectedUserTaskStatus,
            int retryTimes) throws FindException {

        int retryCounter = -1;
        do {
            try {
                assertStateAndCbVorgang(expectedWorkflowTaskName, expectedStatus, expectedAenderungskennzeichen, expectedUserTaskStatus);
                break;   // stop processing because assertion passed
            }
            catch (AssertionError assertionError) {
                LOGGER.info(String.format("assertion for Workflow state failed! Expected: <%s> but was <%s>  -  retry", expectedWorkflowTaskName, getWorkflowState()));
                retryCounter++;
                try { Thread.sleep(100); } catch (Exception e) { /* do nothing */ }
                if (retryCounter >= retryTimes) {
                    throw assertionError;
                }
            }
        }
        while (retryCounter < retryTimes);
    }


    public void assertProduktBezeichnerEquals(ProduktBezeichner produktBezeichner) {
        assertThat(getAuftrag().getGeschaeftsfall().getAuftragsPosition().getProduktBezeichner(),
                equalTo(produktBezeichner));
    }

    public void assertAuftragsKennerSet() {
        assertNotNull(getAuftrag().getAuftragsKenner());
    }

    public void assertProjektKennerSet() {
        assertNotNull(getAuftrag().getProjekt().getProjektKenner());
    }

    public BestandsSuche getBestandsSuche() {
        BestandsSuche bestandsSuche = getAuftrag().getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt()
                .getBestandsSuche();
        return bestandsSuche;
    }

    public void assertRufnummernPortierungEinzelanschlussSet(int anzahlDns) {
        RufnummernPortierung rufnummernPortierung = getAuftrag().getGeschaeftsfall().getAuftragsPosition()
                .getGeschaeftsfallProdukt().getRufnummernPortierung();

        assertNotNull(rufnummernPortierung);
        assertTrue(rufnummernPortierung instanceof RufnummernPortierungEinzelanschluss);

        RufnummernPortierungEinzelanschluss rufnummernPortierungEinzelanschluss = (RufnummernPortierungEinzelanschluss) rufnummernPortierung;
        assertThat(rufnummernPortierungEinzelanschluss.getRufnummern(), hasSize(anzahlDns));
    }

    public void assertCbVorgangReturnOk() throws FindException {
        assertTrue(getCbVorgang().getReturnOk());
    }

    public void assertRufnummernPortierungAnlagenanschlussSet(int anzahlBlocks) {
        RufnummernPortierung rufnummernPortierung = getAuftrag().getGeschaeftsfall().getAuftragsPosition()
                .getGeschaeftsfallProdukt().getRufnummernPortierung();

        assertNotNull(rufnummernPortierung);
        assertTrue(rufnummernPortierung instanceof RufnummernPortierungAnlagenanschluss);

        RufnummernPortierungAnlagenanschluss anlagenanschluss = (RufnummernPortierungAnlagenanschluss) rufnummernPortierung;
        assertThat(anlagenanschluss.getRufnummernBloecke(), hasSize(anzahlBlocks));
    }

    public void assertRufnummernPortierungNotSet() {
        RufnummernPortierung rufnummernPortierung = getAuftrag().getGeschaeftsfall().getAuftragsPosition()
                .getGeschaeftsfallProdukt().getRufnummernPortierung();
        assertNull(rufnummernPortierung);
    }

    public void assertBestellerSet() {
        assertNotNull(getAuftrag().getBesteller());
    }

    public void assertAnsprechpartnerTechnikSet() {
        assertNotNull(getAuftrag().getGeschaeftsfall().getGfAnsprechpartner().getAnsprechpartner());
    }

    public void assertGeschaeftsfallTyp(GeschaeftsfallTyp geschaeftsfallTyp) {
        try {
            assertEquals(getCbVorgang().getWitaGeschaeftsfallTyp(), geschaeftsfallTyp);
        }
        catch (FindException e) {
            assertTrue(false, "Error loading CBVorgang: " + e.getMessage());
        }
    }

    public void assertInterimsProjektkenner() {
        assertEquals(getAuftrag().getProjekt().getProjektKenner(),
                WitaCBVorgang.PK_FOR_ANBIETERWECHSEL_CARRIER_WITHOUT_WITA);
    }

    /**
     * checks if the assigned CBVorgang is marked as Klaerfall *
     */
    public void assertKlaerfall(Boolean isKlaerfall, String regexForKlaerfallBemerkung) {
        try {
            WitaCBVorgang cbVorgang = getCbVorgang();
            assertEquals(cbVorgang.isKlaerfall(), isKlaerfall);
            if (regexForKlaerfallBemerkung != null) {
                assertTrue(cbVorgang.getKlaerfallBemerkung().matches(regexForKlaerfallBemerkung),
                        String.format("Expected regex for Klaerfallbemerkung '%s' doesn't match the current " +
                                "Klaerfallbemerkung '%s'", regexForKlaerfallBemerkung, cbVorgang.getKlaerfallBemerkung()));
            }
            else {
                assertNull(cbVorgang.getKlaerfallBemerkung());
            }
        }
        catch (FindException e) {
            assertTrue(false, "Error loading CBVorgang: " + e.getMessage());
        }
    }


    public void assertAbmValuesSetOnCbVorgang(String lbz, String ll, String aqs) throws Exception {
        CBVorgang cbVorgang = getCbVorgang();
        assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertTrue(cbVorgang.getReturnOk());
        assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
        assertThat("Real return date must be set", cbVorgang.getReturnRealDate(), notNullValue());
        assertThat("Leitungsabschnitt is required", cbVorgang.getReturnLBZ(), equalTo(lbz));
        assertThat("Leitungslaenge is required", cbVorgang.getReturnLL(), equalTo(ll));
        assertThat("AQS is required", cbVorgang.getReturnAQS(), equalTo(aqs));
    }

    public void assertAbbmValuesSetOnCbVorgang(String bemerkung) throws Exception {
        CBVorgang cbVorgang = getCbVorgang();
        assertEquals(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED);
        assertFalse(cbVorgang.getReturnOk());
        assertTrue(cbVorgang.getReturnBemerkung().contains(bemerkung));
        assertThat("Date must be set", cbVorgang.getAnsweredAt(), notNullValue());
    }

    public void assertBestandsSucheOnkz(String onkz) {
        assertEquals(getBestandsSuche().getOnkz(), onkz);
    }

    public void assertBestandsSucheRufnummer(String rufnummer) {
        assertEquals(getBestandsSuche().getRufnummer(), rufnummer);
    }

    private String getPvVertragsnummer() throws FindException {
        if (pvVertragsnummer == null) {
            pvVertragsnummer = getAkmPv().getVertragsNummer();
        }
        return pvVertragsnummer;
    }

    public AnkuendigungsMeldungPv getAkmPv() throws FindException {
        // Look for AKM-PV after CBVorgang
        AnkuendigungsMeldungPv akmPv = mwfEntityService.getLastAkmPv(getCbVorgang().getReturnVTRNR());
        if (akmPv == null) {
            throw new FindException("Could not find last AKM-PV");
        }
        return akmPv;
    }

    public VerzoegerungsMeldungPv getVzmPv() throws FindException {
        final CBVorgang cbVorgang = getCbVorgang();
        Function<Void, Boolean> checkFunction = i-> doGetVzmPv(cbVorgang) != null;
        assertThat(overTime(checkFunction), eventuallyTrue());
        VerzoegerungsMeldungPv vzmPv = doGetVzmPv(cbVorgang);
        if (vzmPv == null) {
            String msg = String.format("Kann die letzte VZM-PV mit der Vertragsnummer '%s' nicht finden",
                    getCbVorgang().getReturnVTRNR());
            throw new FindException(msg);
        }

        return vzmPv;
    }

    private VerzoegerungsMeldungPv doGetVzmPv(CBVorgang cbVorgang) {
        return mwfEntityService.getLastVzmPv(cbVorgang.getReturnVTRNR());
    }

    public void closeAkmPvUserTask() throws FindException, StoreException {
        AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTask(getAkmPv().getExterneAuftragsnummer());
        witaUsertaskService.closeUserTask(userTask);
    }

    public AuftragsBestaetigungsMeldungPv getAbmPv() throws FindException {
        // Look for ABM-PV
        AuftragsBestaetigungsMeldungPv abmPv = mwfEntityService.getLastAbmPv(getCbVorgang().getReturnVTRNR());
        if (abmPv == null) {
            throw new FindException("Could not find last abmPv");
        }
        return abmPv;
    }

    public AbbruchMeldungPv getAbbmPv() throws FindException {
        // Look for ABBM-PV
        AbbruchMeldungPv abbmPv = mwfEntityService.getLastAbbmPv(getCbVorgang().getReturnVTRNR());
        if (abbmPv == null) {
            throw new FindException("Could not find last abbmPv");
        }
        return abbmPv;
    }

    public AuftragsBestaetigungsMeldung getAbm() throws FindException {
        // Look for ABM
        AuftragsBestaetigungsMeldung abm = mwfEntityService.getLastAbm(getCbVorgang().getCarrierRefNr());
        if (abm == null) {
            throw new FindException("Could not find last abm");
        }
        return abm;
    }

    public void startWbciErlmTvAutoProcessing() throws Exception {
        AKUser user = new AKUserBuilder().init()
                .withRandomLoginName()
                .withName("citrus")
                .withDepartmentId(AKDepartment.DEP_AM).setPersist(false).build();
        akUserService.save(user);

        acceptanceTestWorkflowService.startWbciErlmTvAutoProcessing(user);
    }

    public void startWbciStrAufhErlmAutoProcessing() throws Exception {
        AKUser user = new AKUserBuilder().init()
                .withRandomLoginName()
                .withName("citrus")
                .withDepartmentId(AKDepartment.DEP_AM).setPersist(false).build();
        akUserService.save(user);

        acceptanceTestWorkflowService.startWbciStrAufhErlmAutoProcessing(user);
    }

    public AKUser createUser(String nachname) throws Exception {
        AKUser user = new AKUserBuilder().init()
                .withRandomLoginName()
                .withName(nachname)
                .withFirstName("citrus")
                .withDepartmentId(AKDepartment.DEP_AM).setPersist(false).build();
        akUserService.save(user);
        return user;
    }

    public void startWbciAkmTrAutoProcessing(AKUser user) throws Exception {
        acceptanceTestWorkflowService.startWbciAkmTrAutoProcessing(user);
    }

    public enum DateCheck {
        CHECK_EXACT_DATE,
        CHECK_MONTH
    }

}

/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2011 11:09:14
 */
package de.mnet.wita.acceptance.common;

import static de.augustakom.common.tools.matcher.RetryMatcher.*;
import static de.mnet.wita.acceptance.common.AbstractWitaAcceptanceBaseTest.*;
import static de.mnet.wita.message.MeldungsType.*;
import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.IsNot;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.service.WbciAutomationService;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.AbbmPvMeldungsCode;
import de.mnet.wita.AbmMeldungsCode;
import de.mnet.wita.IOArchiveProperties.IOType;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow.DateCheck;
import de.mnet.wita.acceptance.common.function.GetPvWorkflowStateFunction;
import de.mnet.wita.acceptance.common.function.GetTalOrderWorkflowStateFunction;
import de.mnet.wita.acceptance.common.function.WaitForIoArchiveEntryFunction;
import de.mnet.wita.acceptance.common.function.WaitForMwfEntityFunction;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.dao.IoArchiveDao;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.EntgeltMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaSendMessageService;
import de.mnet.wita.service.WitaTalOrderService;

@SuppressWarnings({ "SpringJavaAutowiredMembersInspection", "Guava" })
public class AcceptanceTestWorkflowService implements ApplicationContextAware {

    private static final Logger LOGGER = Logger.getLogger(AcceptanceTestWorkflowService.class);
    @Qualifier("txCBVorgangDAO")
    @Autowired
    protected CBVorgangDAO cbVorgangDAO;
    @Autowired
    protected WitaSendMessageService witaSendMessageService;
    @Autowired
    private CommonWorkflowService commonWorkflowService;
    @Autowired
    private WitaTalOrderService talOrderServiceRemote;
    @Qualifier("txIoArchiveDao")
    @Autowired
    private IoArchiveDao ioArchiveDao;
    @Autowired
    private MwfEntityService mwfEntityService;
    @Autowired
    private WbciAutomationService wbciAutomationServiceRemote;

    /**
     * Spring application context
     */
    private ApplicationContext applicationContext;

    public String getNextVertragsnummer() {
        return cbVorgangDAO.getNextCarrierRefNr();
    }

    public void waitForABBM(WitaCBVorgang cbVorgang) throws Exception {
        waitForABBM(cbVorgang, null);
    }

    public void waitForABBM(WitaCBVorgang cbVorgang, final AbbmMeldungsCode meldungscode) throws Exception {
        waitForNonClosingABBM(cbVorgang, meldungscode);
        // Workflow should be closed after abbm
        assertWorkflowClosed(cbVorgang);
    }

    void waitForNonClosingABBM(WitaCBVorgang cbVorgang, final AbbmMeldungsCode meldungscode) throws Exception {
        AbbruchMeldung abbm = new AbbruchMeldung();
        abbm.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<AbbruchMeldung>(abbm, applicationContext) {

            @Override
            protected Boolean validateMessage(AbbruchMeldung message) {
                assertThat(message, notNullValue());
                if (meldungscode != null) {
                    assertFalse(message.getMeldungsPositionen().isEmpty());
                    MeldungsPosition meldungsPosition = Iterables.getOnlyElement(message.getMeldungsPositionen());
                    assertThat(meldungsPosition.getMeldungsCode(), equalTo(meldungscode.meldungsCode));
                }
                return true;
            }
        }, ABBM, cbVorgang.getCarrierRefNr(), cbVorgang.getWitaGeschaeftsfallTyp());
    }

    void waitForAbbmPv(AnkuendigungsMeldungPv akmPv, final AbbmPvMeldungsCode meldungscode) throws Exception {
        Preconditions.checkNotNull(akmPv.getExterneAuftragsnummer());

        final AbbruchMeldungPv abbmPv = new AbbruchMeldungPv();
        abbmPv.setExterneAuftragsnummer(akmPv.getExterneAuftragsnummer());

        assertMeldungReceived(new WaitForMwfEntityFunction<AbbruchMeldungPv>(abbmPv, applicationContext) {

            @Override
            protected Boolean validateMessage(AbbruchMeldungPv message) {
                assertFalse(message.getMeldungsPositionen().isEmpty());
                MeldungsPosition meldungsPosition = Iterables.getOnlyElement(message.getMeldungsPositionen());
                assertThat(meldungsPosition.getMeldungsCode(), equalTo(meldungscode.meldungsCode));
                return true;
            }
        }, ABBM_PV, akmPv.getExterneAuftragsnummer(), akmPv.getGeschaeftsfallTyp());

        // Workflow should be closed after abbm-pv
        assertWorkflowClosed(akmPv);
    }

    public AcceptanceTestWorkflowService waitForABM(WitaCBVorgang cbVorgang) throws Exception {
        waitForABM(cbVorgang, null, null, 1);
        return this;
    }

    public void waitForABM(WitaCBVorgang cbVorgang, final LocalDateTime expectedDate, final DateCheck checkType,
            int expectedNumber, final AbmMeldungsCode... meldungscodes) throws Exception {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldung();
        abm.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<AuftragsBestaetigungsMeldung>(abm, expectedNumber, applicationContext) {
            @Override
            protected Boolean validateMessage(AuftragsBestaetigungsMeldung message) {
                assertAbmValid(message, expectedDate, checkType, meldungscodes);
                return true;
            }
        }, ABM, cbVorgang.getCarrierRefNr(), cbVorgang.getWitaGeschaeftsfallTyp());

        assertWorkflowState(cbVorgang, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    void waitForAbmPv(AnkuendigungsMeldungPv akmPv) throws Exception {
        waitForAbmPv(akmPv, 1);
    }

    void waitForAbmPv(final AnkuendigungsMeldungPv akmPv, final int no) throws Exception {
        LOGGER.info("Waiting for " + MeldungsType.ABM_PV + " number " + no);
        Function<Void, Collection<? extends AuftragsBestaetigungsMeldungPv>> findAbmPvs = input -> mwfEntityService.findMwfEntitiesByProperty(
                AuftragsBestaetigungsMeldungPv.class, Meldung.VERTRAGS_NUMMER_FIELD, akmPv.getVertragsNummer());
        assertThat("No (second) ABM_PV received for VertragsNr " + akmPv.getVertragsNummer(), overTime(findAbmPvs),
                eventually(Matchers.hasSize(no)));
        LOGGER.info("Received " + MeldungsType.ABM_PV);

        mwfEntityService.getLastAbmPv(akmPv.getVertragsNummer());
        assertWorkflowState(akmPv, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    void waitForVZMPV(final AnkuendigungsMeldungPv akmPv) throws Exception {
        waitForVZMPV(akmPv, 1);
    }

    private void waitForVZMPV(final AnkuendigungsMeldungPv akmPv, int no) throws Exception {
        LOGGER.info("wait for " + MeldungsType.VZM_PV);
        VerzoegerungsMeldungPv lastVzmPv = mwfEntityService.getLastVzmPv(akmPv.getVertragsNummer());
        if (lastVzmPv != null) {
            LOGGER.info("last VZM-PV = " + lastVzmPv.getBusinessKey());
        }
        assertWorkflowState(akmPv, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    void waitForAkmPv(final WitaCBVorgang cbVorgang) throws Exception {
        waitForAkmPv(cbVorgang, 1);
    }

    void waitForAkmPv(final WitaCBVorgang cbVorgang, final int no) throws Exception {
        LOGGER.info("Waiting for " + MeldungsType.AKM_PV + " number " + no);
        Function<Void, Collection<? extends AnkuendigungsMeldungPv>> findAkmPvs =
                input -> mwfEntityService.findMwfEntitiesByProperty(
                        AnkuendigungsMeldungPv.class, Meldung.VERTRAGS_NUMMER_FIELD, cbVorgang.getReturnVTRNR());
        assertThat("No (second) AKM_PV received for refId " + cbVorgang.getReturnVTRNR(), overTime(findAkmPvs),
                eventually(Matchers.hasSize(no)));
        LOGGER.info("Received " + MeldungsType.AKM_PV);

        AnkuendigungsMeldungPv akmPv = mwfEntityService.getLastAkmPv(cbVorgang.getReturnVTRNR());
        assertWorkflowState(akmPv, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    void waitForAutomaticRuemPv(AnkuendigungsMeldungPv akmPv) throws Exception {
        Preconditions.checkNotNull(akmPv.getVertragsNummer());

        assertMeldungReceived(new WaitForIoArchiveEntryFunction(akmPv.getVertragsNummer(), RUEM_PV, IOType.OUT, applicationContext),
                RUEM_PV, akmPv.getVertragsNummer(), akmPv.getGeschaeftsfallTyp());
        assertWorkflowAlive(akmPv);
    }

    void waitForEntmPv(AnkuendigungsMeldungPv akmPv) throws Exception {
        waitForEntmPv(akmPv, false);
    }

    void waitForEntmPv(AnkuendigungsMeldungPv akmPv, boolean waitForError) throws Exception {
        Preconditions.checkNotNull(akmPv.getExterneAuftragsnummer());

        EntgeltMeldungPv entmPv = new EntgeltMeldungPv();
        entmPv.setExterneAuftragsnummer(akmPv.getExterneAuftragsnummer());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(entmPv, applicationContext), MeldungsType.ENTM_PV,
                akmPv.getExterneAuftragsnummer(), akmPv.getGeschaeftsfallTyp());

        if (!waitForError) {
            assertWorkflowClosed(akmPv);
        }
        else {
            assertWorkflowState(akmPv, equalTo(WorkflowTaskName.WORKFLOW_ERROR.id));
        }
    }

    public void waitForENTM(WitaCBVorgang cbVorgang) throws Exception {
        waitForENTM(cbVorgang, false);
    }

    public void waitForENTM(WitaCBVorgang cbVorgang, boolean waitForError) throws Exception {
        EntgeltMeldung entm = new EntgeltMeldung();
        entm.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(entm, applicationContext), ENTM, cbVorgang.getCarrierRefNr(),
                cbVorgang.getWitaGeschaeftsfallTyp());

        if (!waitForError) {
            assertWorkflowClosed(cbVorgang);
        }
        else {
            assertWorkflowState(cbVorgang, equalTo(WorkflowTaskName.WORKFLOW_ERROR.id));
        }
    }

    void waitForENTMWorkflowClosed(WitaCBVorgang cbVorgang) throws Exception {
        EntgeltMeldung entm = new EntgeltMeldung();
        entm.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(entm, applicationContext), ENTM, cbVorgang.getCarrierRefNr(),
                cbVorgang.getWitaGeschaeftsfallTyp());
        assertWorkflowClosed(cbVorgang);
    }

    void waitForErlmPv(AnkuendigungsMeldungPv akmPv) throws Exception {
        waitForErlmPv(akmPv, false);
    }

    void waitForErlmPv(AnkuendigungsMeldungPv akmPv, boolean waitForError) throws Exception {
        Preconditions.checkNotNull(akmPv.getExterneAuftragsnummer());

        ErledigtMeldungPv erlmPv = new ErledigtMeldungPv();
        erlmPv.setExterneAuftragsnummer(akmPv.getExterneAuftragsnummer());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(erlmPv, applicationContext), ERLM_PV,
                akmPv.getExterneAuftragsnummer(), akmPv.getGeschaeftsfallTyp());

        if (!waitForError) {
            assertWorkflowState(akmPv, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
        }
        else {
            assertWorkflowState(akmPv, equalTo(WorkflowTaskName.WORKFLOW_ERROR.id));
        }
    }

    public AcceptanceTestWorkflowService waitForERLM(WitaCBVorgang cbVorgang) throws Exception {
        waitForERLM(cbVorgang, false);
        return this;
    }

    void waitForERLMWorkflowClosed(WitaCBVorgang cbVorgang) throws Exception {
        ErledigtMeldung erlm = new ErledigtMeldung();
        erlm.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(erlm, applicationContext), ERLM, cbVorgang.getCarrierRefNr(),
                cbVorgang.getWitaGeschaeftsfallTyp());
        assertWorkflowClosed(cbVorgang);
    }

    public void waitForERLM(WitaCBVorgang cbVorgang, boolean waitForError) throws Exception {
        ErledigtMeldung erlm = new ErledigtMeldung();
        erlm.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(erlm, applicationContext), ERLM, cbVorgang.getCarrierRefNr(),
                cbVorgang.getWitaGeschaeftsfallTyp());

        if (!waitForError) {
            assertWorkflowState(cbVorgang, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
        }
        else {
            assertWorkflowState(cbVorgang, equalTo(WorkflowTaskName.WORKFLOW_ERROR.id));
        }
    }

    public AcceptanceTestWorkflowService waitForQEB(WitaCBVorgang cbVorgang) throws Exception {
        QualifizierteEingangsBestaetigung qeb = new QualifizierteEingangsBestaetigung();
        qeb.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(qeb, applicationContext), QEB,
                cbVorgang.getCarrierRefNr(), cbVorgang.getWitaGeschaeftsfallTyp());

        assertWorkflowState(cbVorgang, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
        return this;
    }

    void waitForTAM(WitaCBVorgang cbVorgang) throws Exception {
        TerminAnforderungsMeldung tam = new TerminAnforderungsMeldung();
        tam.setExterneAuftragsnummer(cbVorgang.getCarrierRefNr());

        assertMeldungReceived(new WaitForMwfEntityFunction<>(tam, applicationContext), TAM,
                cbVorgang.getCarrierRefNr(), cbVorgang.getWitaGeschaeftsfallTyp());
        assertWorkflowAlive(cbVorgang);
    }

    void waitForSecondTAM(final WitaCBVorgang cbVorgang) throws Exception {
        LOGGER.info(String.format("Waiting for %s for Geschaeftsfall %s", MeldungsType.TAM, cbVorgang.getWitaGeschaeftsfallTyp()));
        Function<Void, Collection<? extends TerminAnforderungsMeldung>> findTams = input -> mwfEntityService.findMwfEntitiesByProperty(
                TerminAnforderungsMeldung.class, Meldung.VERTRAGS_NUMMER_FIELD, cbVorgang.getReturnVTRNR());
        assertThat("No (second) TAM received for refId " + cbVorgang.getReturnVTRNR(), overTime(findTams),
                eventually(Matchers.hasSize(2)));
        LOGGER.info("Received " + MeldungsType.TAM);
        assertWorkflowState(cbVorgang, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    void waitForMTAM(final WitaCBVorgang cbVorgang) throws Exception {
        LOGGER.info(String.format("Waiting for M%s for Geschaeftsfall %s", MeldungsType.TAM, cbVorgang.getWitaGeschaeftsfallTyp()));
        Function<Void, Collection<? extends TerminAnforderungsMeldung>> findTams = input -> {
            TerminAnforderungsMeldung terminAnforderungsMeldung = new TerminAnforderungsMeldung();
            terminAnforderungsMeldung.setVertragsNummer(cbVorgang.getReturnVTRNR());
            terminAnforderungsMeldung.setMahnTam(true);
            return mwfEntityService.findMwfEntitiesByExample(terminAnforderungsMeldung);
        };
        assertThat("No MahnTAM received for refId " + cbVorgang.getReturnVTRNR(), overTime(findTams),
                eventually(Matchers.hasSize(1)));
        LOGGER.info("Received M" + MeldungsType.TAM);
        assertWorkflowAlive(cbVorgang);
    }

    void waitForVZM(WitaCBVorgang cbVorgang) throws Exception {
        assertMeldungReceived(new WaitForIoArchiveEntryFunction(cbVorgang, VZM, applicationContext), VZM, cbVorgang.getCarrierRefNr(),
                cbVorgang.getWitaGeschaeftsfallTyp());
        assertWorkflowState(cbVorgang, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    private void assertMeldungReceived(Function<Void, Boolean> function, MeldungsType meldungsTyp, String refId,
            GeschaeftsfallTyp geschaeftsfallTyp)
            throws Exception {
        LOGGER.info(String.format("Waiting for '%s' for Geschaeftsfall '%s' and refId '%s'", meldungsTyp, geschaeftsfallTyp, refId));
        assertThat("No " + meldungsTyp + " received for RefId " + refId, overTime(function), eventually(equalTo(true)));
        LOGGER.info(String.format("Received %s for Geschaeftsfall %s", meldungsTyp, geschaeftsfallTyp));
    }

    private void assertWorkflowAlive(WitaCBVorgang cbVorgang) throws Exception {
        assertTrue(commonWorkflowService.isProcessInstanceAlive(cbVorgang.getBusinessKey()),
                "Workflow not alive! RefId: " + cbVorgang.getCarrierRefNr() + " workflow state is: " + commonWorkflowService.getWorkflowState(cbVorgang.getBusinessKey()));

        assertWorkflowState(cbVorgang, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    private void assertWorkflowState(WitaCBVorgang cbVorgang, Matcher<String> state) {
        GetTalOrderWorkflowStateFunction checkFunction = new GetTalOrderWorkflowStateFunction(cbVorgang, applicationContext);
        assertThat("Workflow im falschen Zustand fuer RefNr: " + cbVorgang.getCarrierRefNr(), overTime(checkFunction),
                eventually(state));
    }

    private void assertWorkflowAlive(AnkuendigungsMeldungPv akmPv) throws Exception {
        assertTrue(commonWorkflowService.isProcessInstanceAlive(akmPv.getBusinessKey()), "Workflow not alive! RefId: "
                + akmPv.getExterneAuftragsnummer());

        assertWorkflowState(akmPv, new IsNot<>(equalTo(WorkflowTaskName.WORKFLOW_ERROR.id)));
    }

    private void assertWorkflowState(AnkuendigungsMeldungPv akmPv, Matcher<String> state) {
        GetPvWorkflowStateFunction checkFunction = new GetPvWorkflowStateFunction(akmPv, applicationContext);
        assertThat("Workflow im falschen Zustand fuer RefNr: " + akmPv.getExterneAuftragsnummer(),
                overTime(checkFunction), eventually(state));
    }

    private void assertWorkflowState(VerzoegerungsMeldungPv vzmPv, Matcher<String> state) {
        GetPvWorkflowStateFunction checkFunction = new GetPvWorkflowStateFunction(vzmPv, applicationContext);
        assertThat("Workflow im falschen Zustand fuer RefNr: " + vzmPv.getExterneAuftragsnummer(),
                overTime(checkFunction), eventually(state));
    }

    private void assertWorkflowClosed(WitaCBVorgang cbVorgang) throws Exception {
        assertTrue(commonWorkflowService.isProcessInstanceFinished(cbVorgang.getBusinessKey()),
                "Workflow not closed! RefId: " + cbVorgang.getCarrierRefNr());
    }

    private void assertWorkflowClosed(AnkuendigungsMeldungPv akmPv) throws Exception {
        assertTrue(commonWorkflowService.isProcessInstanceFinished(akmPv.getBusinessKey()),
                "Workflow not closed! RefId: " + akmPv.getExterneAuftragsnummer());
    }

    private void assertAbmValid(AuftragsBestaetigungsMeldung achieved, final LocalDateTime expectedDate,
            final DateCheck checkType, final AbmMeldungsCode... meldungscodes) {
        if ((meldungscodes != null) && (meldungscodes.length > 0)) {
            assertFalse(achieved.getMeldungsPositionen().isEmpty());

            Set<String> achievedSet = Sets.newHashSet();
            for (MeldungsPosition meldungsPosition : achieved.getMeldungsPositionen()) {
                achievedSet.add(meldungsPosition.getMeldungsCode());
            }
            Set<String> expectedSet = Sets.newHashSet();
            for (AbmMeldungsCode code : meldungscodes) {
                expectedSet.add(code.meldungsCode);
            }

            assertTrue(Sets.symmetricDifference(achievedSet, expectedSet).isEmpty(),
                    "ABM Meldungscodes unerwartet: Expected=" + expectedSet + ",achieved=" + achievedSet);

        }

        if (expectedDate != null) {
            LocalDateTime achievedDate = achieved.getVerbindlicherLiefertermin().atStartOfDay();
            switch (checkType) {
                case CHECK_EXACT_DATE:
                    assertTrue(achievedDate.toLocalDate().equals(expectedDate.toLocalDate()),
                            "ABM VLT unerwartet: Expected=" + expectedDate + ", achieved=" + achievedDate);
                    break;
                case CHECK_MONTH:
                    assertEquals(achievedDate.getMonth(), expectedDate.getMonth(),
                            "Realisierungsmonat von ABM unerwartet: Expected=" + expectedDate.getMonth()
                                    + ", achieved=" + achievedDate.getMonth()
                    );
                    break;
            }
        }
    }

    public AcceptanceTestWorkflowService sendStorno(CBVorgang cbVorgang, AKUser user) throws Exception {
        talOrderServiceRemote.doStorno(cbVorgang.getId(), user);
        return this;
    }

    void waitForIOArchiveEntry(final CBVorgang result) throws Exception {
        LOGGER.info("Search for IO archive entry with RefId " + result.getCarrierRefNr());
        Function<Void, Collection<IoArchive>> findIoArchives = input -> {
            // IOArchiveService abfragen, ob Message archiviert wurde
            return ioArchiveDao.findIoArchivesForExtOrderNo(result.getCarrierRefNr());
        };
        assertThat("No IO archive entry for RefId " + result.getCarrierRefNr() + " found!", overTime(findIoArchives),
                eventually(not(IsEmptyCollection.empty())));
    }

    void waitForIOArchiveEntry(final String extOrderNo, final GeschaeftsfallTyp requestGf,
            final String requestTyp, final String requestMeldungscode)
            throws Exception {
        LOGGER.info("Waiting for IOArchive " + extOrderNo + " / " + requestGf.getDtagMeldungGeschaeftsfall() + " / "
                + requestTyp);
        Function<Void, Collection<IoArchive>> findIoArchiveWithMeldungscodes = input -> {

            // IOArchiveService abfragen, ob Message archiviert wurde
            List<IoArchive> archive = ioArchiveDao.findIoArchivesForExtOrderNo(extOrderNo);
            CollectionUtils.filter(archive, object -> {
                IoArchive archive1 = (IoArchive) object;
                if ((requestGf.name().equals(archive1.getRequestGeschaeftsfall()))
                        && StringUtils.equals(archive1.getRequestMeldungstyp(), requestTyp)) {

                    if (requestMeldungscode != null) {
                        if (!StringUtils.equals(requestMeldungscode, archive1.getRequestMeldungscode())) {
                            return false;
                        }
                    }

                    return true;
                }
                return false;
            });
            return archive;
        };
        assertThat("No IO archive entry for RefId " + extOrderNo + " found!", overTime(findIoArchiveWithMeldungscodes),
                eventually(not(IsEmptyCollection.empty())));

        LOGGER.info("IOArchive received");
    }

    WitaCBVorgang createCBVorgang(CreatedData createdData, Long cbVorgangTyp) throws Exception {
        LOGGER.info("Send wita order message");
        Auftrag auftrag = createdData.auftrag;
        Carrierbestellung carrierbestellung = createdData.carrierbestellung;
        AKUser user = createdData.user;
        boolean isVierDraht = !createdData.cbVorgangSubOrders.isEmpty();

        final Date createdDataVorgameMnet = DateConverterUtils.asDate(createdData.vorgabeMnet);
        if (createdData.previousUetv != null) {
            Equipment equipment = createdData.dtagPort;
            Uebertragungsverfahren newUetv = equipment.getUetv();
            equipment.setUetv(createdData.previousUetv);

            return talOrderServiceRemote.changeUebertragungsverfahren(carrierbestellung, equipment,
                    createdDataVorgameMnet, newUetv, user);
        }

        HashMap<Long, Set<Pair<ArchiveDocumentDto, String>>> archiveDocuments = Maps.newHashMap();
        archiveDocuments.put(auftrag.getAuftragId(), createdData.archiveDocuments);
        // @formatter:off
        CbVorgangData cbvData = new CbVorgangData()
                .withCbId(CBVorgang.TYP_REX_MK.equals(cbVorgangTyp) ? null : carrierbestellung.getId())
                .addAuftragId(auftrag.getId(), carrierbestellung.getAuftragId4TalNA())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(createdDataVorgameMnet)
                .withCbVorgangTyp(cbVorgangTyp)
                .withSubOrders(createdData.cbVorgangSubOrders, isVierDraht)
                .withMontagehinweis(createdData.montagehinweis)
                .withTerminReservierungsId(createdData.terminReservierungsId)
                .withAnbieterwechselTkg46(createdData.anbieterwechselTKG46)
                .withUser(user)
                .withArchiveDocuments(archiveDocuments)
                .withProjektKenner(createdData.projektKenner)
                .withVorabstimmungsId(createdData.vorabstimmungsId)
                .withPreviousUetv(createdData.previousUetv)
                .withRufnummerIds(createdData.rufnummern.stream().map(Rufnummer::getDnNoOrig).collect(toSet()));
        // @formatter:on

        List<CBVorgang> result = talOrderServiceRemote.createCBVorgang(cbvData);
        assertNotNull(result, "CBVorgang for WITA not created!");

        WitaCBVorgang firstCbVorgang = (WitaCBVorgang) result.get(0);
        if (!CBVorgang.TYP_REX_MK.equals(cbVorgangTyp)) { // Bei REX-MK gibt es keine Carrierbestellung
            assertEquals(firstCbVorgang.getCbId(), carrierbestellung.getId(), "CB-Id not as expected!");
        }
        return firstCbVorgang;
    }

    public WitaCBVorgang closeCbVorgang(WitaCBVorgang cbVorgang) throws Exception {
        return talOrderServiceRemote.closeCBVorgang(cbVorgang.getId(),
                AbstractWitaAcceptanceBaseTest.hurricanContextStarter.getSessionId());
    }

    void startWbciErlmTvAutoProcessing(AKUser user) {
        wbciAutomationServiceRemote.processAutomatableErlmTvs(user);
    }

    void startWbciStrAufhErlmAutoProcessing(AKUser user) {
        wbciAutomationServiceRemote.processAutomatableStrAufhErlms(user);
    }

    void startWbciAkmTrAutoProcessing(AKUser user) {
        wbciAutomationServiceRemote.processAutomatableAkmTrs(user);
    }

    WitaCBVorgang findAndWaitForCreatedCbVorgang(final String vaId) {
        LOGGER.info("Waiting for a WITA request");

        Function<Void, Collection<? extends WitaCBVorgang>> findWitaRequests = input -> {
            List<WitaCBVorgang> result = null;
            try {
                result = talOrderServiceRemote.findCBVorgaengeByVorabstimmungsId(vaId);
            }
            catch (FindException e) {
                LOGGER.warn(e.getMessage());
            }
            return result;
        };

        assertThat("No WITA CBVorgang found for VaId " + vaId, overTime(findWitaRequests),
                eventually(Matchers.hasSize(1)));

        WitaCBVorgang cbv = findWitaRequests.apply(null).iterator().next();
        LOGGER.info("Received WITA Request " + cbv);
        return cbv;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

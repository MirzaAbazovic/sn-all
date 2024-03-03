/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import javax.annotation.*;
import javax.validation.*;
import com.google.common.base.Preconditions;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.bpm.variables.WitaActivitiVariableUtils;
import de.mnet.wita.exceptions.AuftragNotFoundException;
import de.mnet.wita.exceptions.MessageOutOfOrderException;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaCheckConditionService;
import de.mnet.wita.service.WitaTalOrderService;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * Activiti-Task, der WITA Messages entgegen nimmt und abhaengig vom Typ (z.B. ABM, ABBM) verarbeitet.
 */
public class ProcessMessageTask extends AbstractProcessingWitaTask {

    private static final Logger LOG = Logger.getLogger(ProcessMessageTask.class);
    static final String DELIVERY_DATE_DIFFERS_FROM_REQUESTED_DATE =
            "Der bestätigte Liefertermin '%s' für die Neubestellung weicht vom Kundenwunschtermin '%s' ab.";
    static final String HVT_TO_KVZ_ABBM_AUF_NEU =
            "Die Neubestellung im HVt-nach-KVz wurde mit einer ABBM beantwortet.";

    @Resource(name = "de.mnet.wita.service.impl.WitaDataService")
    private WitaDataService witaDataService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private WitaTalOrderService witaTalOrderService;
    @Autowired
    private MwfEntityService mwfEntityService;
    @Autowired
    private WitaCheckConditionService witaCheckConditionService;
    @Autowired
    private AKUserService userService;

    @Override
    protected void processMessage(DelegateExecution execution) throws Exception {
        MeldungsType messageTyp = WitaActivitiVariableUtils.extractMeldungsType(execution);
        WitaCBVorgang cbVorgang = getCbVorgang(execution);
        LOG.info("Received " + messageTyp.toString() + " for CBVorgang with Id = " + cbVorgang.getId());
        try {
            switch (messageTyp) {
                case STORNO:
                case TV:
                    throw new WitaBpmException(messageTyp.name() + " cannot be processed within "
                            + this.getClass().getSimpleName());
                default:
                    processMeldung(execution, cbVorgang, messageTyp);
                    return;
            }
        }
        catch (ValidationException e) {
            throw new WitaDataAggregationException(
                    "Bei der Validierung der WITA Daten wurden fehlerhafte Daten entdeckt: " + e.getMessage(), e);
        }
    }

    private void processMeldung(DelegateExecution execution, WitaCBVorgang cbVorgang, MeldungsType meldungsType)
            throws StoreException {

        if (meldungsType == MeldungsType.ERLM_K) {
            // workflow variable correct; use next edge to send ERLM-K in sendErlmkTask
            return;
        }

        Long meldungsId = WitaActivitiVariableUtils.extractMeldungsId(execution);
        LOG.info("Received Meldung with meldungsId = " + meldungsId);
        Meldung<?> meldung = mwfEntityDao.findById(meldungsId, meldungsType.getMeldungClass());

        Preconditions.checkArgument(meldungsType == meldung.getMeldungsTyp(),
                "MeldungsTyp aus WF passt nicht zu MeldungsTyp aus MwfEntity");

        switch (meldungsType) {
            case ERLM:
                if (((meldung.getAenderungsKennzeichen() == STANDARD) && mwfEntityService.checkMeldungReceived(
                        cbVorgang.getCarrierRefNr(), AuftragsBestaetigungsMeldung.class))
                        || ((meldung.getAenderungsKennzeichen() == STORNO) && checkStornoSent(cbVorgang.getId()))) {
                    processMeldung(meldung, execution, cbVorgang);
                }
                else {
                    workflowTaskService.setWorkflowToError(execution,
                            getWrongMeldungErrorMessage(execution, meldungsType, "ABM, ABBM or VZM."));
                }
                break;
            case ENTM:
                throw new MessageOutOfOrderException(getWrongMeldungErrorMessage(execution, meldungsType,
                        "ABM, ABBM, ERLM, TAM OR VZM"));
            default:
                processMeldung(meldung, execution, cbVorgang);
        }
    }

    private boolean checkStornoSent(Long cbVorgangId) {
        try {
            mwfEntityDao.getStornosOfCbVorgang(cbVorgangId);
            return true;
        }
        catch (AuftragNotFoundException e) {
            return false;
        }
    }

    private String getWrongMeldungErrorMessage(DelegateExecution execution, MeldungsType meldungsType,
            String expectedMeldungstypen) {
        return "Wrong MeldungsTyp: " + meldungsType + " for externe Auftragsnummer: "
                + execution.getProcessBusinessKey() + ". Expected MeldungsType: " + expectedMeldungstypen + ".";
    }

    private void processMeldung(Meldung<?> meldung, DelegateExecution execution, WitaCBVorgang cbVorgang)
            throws StoreException {
        if (meldung instanceof AuftragsBestaetigungsMeldung) {
            preprocessAbm(execution, cbVorgang, (AuftragsBestaetigungsMeldung) meldung);
        }
        else if (meldung instanceof AbbruchMeldung) {
            preprocessAbbm(cbVorgang, (AbbruchMeldung) meldung);
        }
        if (!workflowTaskService.validateMwfInput(meldung, execution)) {
            return;
        }
        mwfCbVorgangConverterService.write(cbVorgang, meldung);
    }

    void preprocessAbbm(WitaCBVorgang cbVorgang, AbbruchMeldung abbm) throws StoreException {
        if (cbVorgang.isHvtToKvz() && GeschaeftsfallTyp.BEREITSTELLUNG.equals(cbVorgang.getWitaGeschaeftsfallTyp())) {
            witaTalOrderService.markWitaCBVorgangAsKlaerfall(cbVorgang.getId(), HVT_TO_KVZ_ABBM_AUF_NEU);
        }
        else if (GeschaeftsfallTyp.KUENDIGUNG_KUNDE.equals(cbVorgang.getWitaGeschaeftsfallTyp())) {
            witaTalOrderService.checkHvtKueAndCancelKvzBereitstellung(cbVorgang.getId());
        }
    }

    void preprocessAbm(DelegateExecution execution, WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm) throws StoreException {
        determineLeitungForAbm(execution, cbVorgang, abm);
        witaCheckConditionService.checkConditionsForAbm(cbVorgang, abm);

        //TODO  move to witaCheckConditionService
        checkAndAdaptEarliestSendDateForBereitstellungAuftrag(cbVorgang, abm);
        //TODO  move to witaCheckConditionService
        checkVerbindlicherLieferterminHvtToKvzWechsel(cbVorgang, abm);
    }

    /**
     * Überprüft ob der bestätigte Liefertermin aus der ABM dem Kundwenwunschtermin aus der Neubestellung entspricht.
     * Wenn das nicht der Fall sein sollte, wird der Vorgang als Klaerfall markiert und eine entsprechende Bemerkung
     * hinzugefügt. Die Prüfung wird nur für 'HVt nach KVz'-Fälle durchgeführt.
     */
    void checkVerbindlicherLieferterminHvtToKvzWechsel(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm)
            throws StoreException {
        if (GeschaeftsfallTyp.BEREITSTELLUNG.equals(cbVorgang.getWitaGeschaeftsfallTyp()) && cbVorgang.isHvtToKvz()) {
            try {
                final CBVorgang kuendigung = carrierElTalService.findCBVorgang(cbVorgang.getCbVorgangRefId());
                final LocalDate kueKwt = DateConverterUtils.asLocalDate(kuendigung.getReturnRealDate());
                if (!abm.getVerbindlicherLiefertermin().isEqual(kueKwt)) {
                    // TODO we need some kind of System-User here since the storno is processed automatically. Imho it is
                    // not correct to use the last user here, but at the moment this is the only possible workaround since
                    // the user is a mandatory field within the CbVorgang
                    AKUser user = userService.findById(cbVorgang.getUserId());
                    witaTalOrderService.doTerminverschiebung(kuendigung.getId(), abm.getVerbindlicherLiefertermin(),
                            user, true, null, TamUserTask.TamBearbeitungsStatus.TV_60_TAGE);
                }
            }
            catch (Exception e) {
                throw new StoreException(
                        String.format("Während der Prozessierung der CBVorgang-Bereitstellung '%s' ist ein " +
                                "unerwarteter Fehler aufgetreten!%nGrund: %s)", cbVorgang.getExmId(), e.getMessage()), e);
            }
        }
    }

    /**
     * Checks to see if the {@code abm} is for a {@link de.mnet.wita.message.GeschaeftsfallTyp#KUENDIGUNG_KUNDE}. If it
     * is then the corresponding Bereitstellung Auftrag is located, adapted and scheduled for sending.
     *
     * @param cbVorgang
     * @param abm
     */
    void checkAndAdaptEarliestSendDateForBereitstellungAuftrag(WitaCBVorgang cbVorgang, AuftragsBestaetigungsMeldung abm) throws StoreException {
        if (GeschaeftsfallTyp.KUENDIGUNG_KUNDE.equals(cbVorgang.getWitaGeschaeftsfallTyp())) {
            LocalDateTime earliestSendDate = DateConverterUtils.asLocalDateTime(abm.getVersandZeitstempel()).truncatedTo(ChronoUnit.DAYS);
            Date confirmedTermin = Date.from(abm.getVerbindlicherLiefertermin().atStartOfDay(ZoneId.systemDefault()).toInstant());
            witaTalOrderService.checkAndAdaptHvtToKvzBereitstellung(cbVorgang.getId(), earliestSendDate, confirmedTermin);
        }
    }

    private void determineLeitungForAbm(DelegateExecution execution, WitaCBVorgang cbVorgang,
            AuftragsBestaetigungsMeldung abm) {
        if ((abm.getLeitung() == null)
                && ((abm.getGeschaeftsfallTyp() == GeschaeftsfallTyp.LEISTUNGS_AENDERUNG)
                || (abm.getGeschaeftsfallTyp() == GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG) || (abm
                .getGeschaeftsfallTyp() == GeschaeftsfallTyp.PORTWECHSEL))) {

            // Wenn keine neue Leitung (z.B. bei AEN-LMAE, da sich diese nicht ändert) mitgesendet wird, dann versuchen
            // die Daten von der alten Carrierbestellung zu übernehmen
            try {
                Carrierbestellung cb = carrierService.findCB(cbVorgang.getCbId());
                Carrierbestellung referencingCb = witaDataService.getReferencingCarrierbestellung(cbVorgang, cb);

                Leitung leitung = new Leitung();
                leitung.setLeitungsBezeichnung(new LeitungsBezeichnung(referencingCb.getLbz(), null));
                leitung.setLeitungsAbschnitte(LeitungsAbschnitt.valueOf(referencingCb.getLl(), referencingCb.getAqs()));
                leitung.setMaxBruttoBitrate(referencingCb.getMaxBruttoBitrate());
                abm.setLeitung(leitung);
                mwfEntityService.store(abm);
            }
            catch (Exception e) {
                workflowTaskService.setWorkflowToError(execution,
                        "Could not determine Leitung for ABM due to " + e.getMessage(), e);
            }
        }
    }

}

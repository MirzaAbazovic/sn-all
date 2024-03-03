/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm.impl;

import static de.mnet.wita.bpm.WitaTaskVariables.*;

import java.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.bpm.AbgebendPvWorkflowService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.IncomingPvMeldung;
import de.mnet.wita.message.meldung.Meldung;

@CcTxRequired
public class AbgebendPvWorkflowServiceImpl implements AbgebendPvWorkflowService {

    private static final Logger LOGGER = Logger.getLogger(AbgebendPvWorkflowServiceImpl.class);

    /**
     * public for testing
     */
    @Autowired
    public CBVorgangDAO cbVorgangDAO;
    /**
     * public for testing
     */
    @Autowired
    public MwfEntityDao mwfEntityDao;
    @Autowired
    CommonWorkflowService commonWorkflowService;

    @Override
    public ProcessInstance retrieveRunningProcessInstance(String vertragsNummer) {
        LOGGER.info(String.format("Retrieving running process instance for Vertragsnummer '%s'", vertragsNummer));

        Preconditions.checkArgument(StringUtils.isNotBlank(vertragsNummer));
        ProcessInstance runningPi = null;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<Meldung<?>> meldungen = (List) mwfEntityDao.findByProperty(Meldung.class, Meldung.VERTRAGS_NUMMER_FIELD,
                vertragsNummer);

        LOGGER.info(String.format("Found '%s' Meldungen for Vertragsnummer '%s'", meldungen.size(), vertragsNummer));

        for (Meldung<?> meldung : meldungen) {
            String businessKey = meldung.getExterneAuftragsnummer();
            if (meldung instanceof IncomingPvMeldung && StringUtils.isNotBlank(businessKey)) {
                ProcessInstance pi = commonWorkflowService.retrieveProcessInstanceIfExists(businessKey);
                if ((pi != null) && !commonWorkflowService.isProcessInstanceFinished(pi)) {
                    if (runningPi == null) {
                        runningPi = pi;
                    }
                    else if (!StringUtils.equals(runningPi.getId(), pi.getId())) {
                        throw new WitaBpmException(String.format("Mehrere laufende Prozessinstanzen gefunden fuer Vertragsnummer '%s'", vertragsNummer));
                    }
                }
            }
        }

        if (runningPi != null) {
            LOGGER.info(String.format("Found running process instance '%s' for Vertragsnummer '%s'", runningPi.getId(), vertragsNummer));
        }
        else {
            LOGGER.info(String.format("No running process instance found for Vertragsnummer '%s'", vertragsNummer));
        }

        return runningPi;
    }

    @Override
    public void sendRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText) {
        Preconditions.checkNotNull(antwortCode, "Der RUEM-PV AntwortCode wurde nicht angegeben!");

        Map<String, Object> taskParams = Maps.newHashMap();

        Map<String, Object> defaultParams = ImmutableMap.<String, Object>of(WITA_MESSAGE_TYPE.id,
                MeldungsType.RUEM_PV.name(), RUEM_PV_ANTWORTCODE.id, antwortCode);
        taskParams.putAll(defaultParams);
        if (!RuemPvAntwortCode.OK.equals(antwortCode) && StringUtils.isNotBlank(antwortText)) {
            taskParams.put(RUEM_PV_ANTWORTTEXT.id, antwortText);
        }

        commonWorkflowService.handleOutgoingWitaMessage(businessKey, taskParams);
    }

    @Override
    public void handleWitaMessage(IncomingPvMeldung meldung) {
        ProcessInstance pi = retrieveRunningProcessInstance(meldung.getVertragsNummer());
        if (pi == null) {
            newProcessInstance(meldung);
        }
        else {
            handleWitaMessage(meldung, pi);
        }
    }

    /**
     * Erstellt eine neue Activiti ProcessInstance fuer eine {@link IncomingPvMeldung}.
     *
     * @param meldung fuer die eine neue Workflow-Instanz erstellt werden soll
     */
    private void newProcessInstance(IncomingPvMeldung meldung) {
        Preconditions.checkNotNull(meldung);
        Preconditions.checkArgument(meldung.getBusinessKey() == null);

        String businessKey = cbVorgangDAO.getNextCarrierRefNr();
        meldung.setExterneAuftragsnummer(businessKey);
        mwfEntityDao.store(meldung);

        Preconditions.checkNotNull(meldung.getId());

        Map<String, Object> taskParams = Maps.newHashMap();
        if (meldung instanceof AnkuendigungsMeldungPv) { // AKM_PV_ID is used for finding AKM-PV when sending RUEM-PV
            taskParams.put(WitaTaskVariables.AKM_PV_ID.id, meldung.getId());
        }
        taskParams.put(WitaTaskVariables.WITA_MESSAGE_TYPE.id, meldung.getMeldungsTyp().name());
        taskParams.put(WitaTaskVariables.WITA_IN_MWF_ID.id, meldung.getId());

        commonWorkflowService.newProcessInstance(Workflow.ABGEBEND_PV, businessKey, taskParams);
    }

    private void handleWitaMessage(IncomingPvMeldung meldung, ProcessInstance pi) {
        Preconditions.checkNotNull(meldung);
        Preconditions.checkNotNull(pi);

        meldung.setExterneAuftragsnummer(pi.getBusinessKey());
        mwfEntityDao.store(meldung);

        Preconditions.checkNotNull(meldung.getId());

        Map<String, Object> taskParams = Maps.newHashMap();
        if (meldung instanceof AnkuendigungsMeldungPv) {
            /*
                /mehrfache AKM-PV zu einer Vertragsnr. --> AKM-PV erhaelt bereits vorhandene externe Auftragsnummer;
                ProcessInstance-Variable wird auf neue AKM-PV umgeschrieben; Workflow bleibt 'alive'
            */
            LOGGER.warn("AKM-PV zu bestehender Prozess-Instanz bekommen. Vertragsnummer: "
                    + meldung.getVertragsNummer() + ", BusinessKey: " + pi.getBusinessKey());
            taskParams.put(WitaTaskVariables.AKM_PV_ID.id, meldung.getId());
        }

        taskParams.put(WITA_IN_MWF_ID.id, meldung.getId());
        // WITA_MESSAGE_TYPE muss gesetzt werden, damit 'conditionExpression' in bpmn20.xml ausgewertet werden kann
        taskParams.put(WITA_MESSAGE_TYPE.id, meldung.getMeldungsTyp().name());

        commonWorkflowService.handleWitaMessage(pi, taskParams);
    }
}

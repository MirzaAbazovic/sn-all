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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.TalOrderMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;

@CcTxRequired
@Service
public class TalOrderWorkflowServiceImpl implements TalOrderWorkflowService {

    private static final Logger LOGGER = Logger.getLogger(TalOrderWorkflowServiceImpl.class);

    @Autowired
    MwfEntityDao mwfEntityDao;
    @Autowired
    CommonWorkflowService commonWorkflowService;
    @Autowired
    WitaConfigService witaConfigService;

    @Autowired
    CBVorgangDAO cbVorgangDao;

    @Override
    public ProcessInstance newProcessInstance(WitaCBVorgang witaCbVorgang) {
        return newProcessInstance(witaCbVorgang, null);
    }

    @Override
    public void restartProcessInstance(WitaCBVorgang witaCbVorgang) {
        commonWorkflowService.deleteHistoricProcessInstance(witaCbVorgang.getBusinessKey());
        Map<String, Object> taskParams = ImmutableMap.<String, Object>of(WitaTaskVariables.RESTART.id, true);
        newProcessInstance(witaCbVorgang, taskParams);
    }

    private ProcessInstance newProcessInstance(WitaCBVorgang witaCbVorgang, Map<String, Object> params) {
        Map<String, Object> defaultParams = ImmutableMap.<String, Object>of(WitaTaskVariables.CB_VORGANG_ID.id,
                witaCbVorgang.getId());

        Map<String, Object> taskParams = Maps.newHashMap();
        taskParams.putAll(defaultParams);
        if (params != null) {
            taskParams.putAll(params);
        }
        return commonWorkflowService.newProcessInstance(Workflow.TAL_ORDER, witaCbVorgang.getBusinessKey(), taskParams);

    }

    @Override
    public <T extends TalOrderMeldung> void handleWitaMessage(T message) {
        LOGGER.info("handleWitaMessage for " + message);
        if ((message == null) || (message.getMeldungsTyp() == null)) {
            throw new WitaBpmException("WITA message is null or has no meldungsTyp!");
        }

        LOGGER.info("persist WitaInMessage: " + message);
        mwfEntityDao.store(message);

        // WITA_IN_MESSAGE_TYPE muss gesetzt werden, damit 'conditionExpression' in bpmn20.xml ausgewertet werden kann
        Map<String, Object> defaultParams = ImmutableMap.<String, Object>of(WITA_MESSAGE_TYPE.id, message
                .getMeldungsTyp().name());
        Map<String, Object> taskParams = Maps.newHashMap();
        taskParams.putAll(defaultParams);

        if (message instanceof MwfEntity) {
            MwfEntity mwfEntity = (MwfEntity) message;
            if (mwfEntity.getId() != null) {
                taskParams.put(WITA_IN_MWF_ID.id, mwfEntity.getId());
            }
        }
        if (message instanceof Meldung<?>) {
            Meldung<?> meldung = (Meldung<?>) message;
            taskParams.put(WitaTaskVariables.WITA_MESSAGE_AENDERUNGSKENNZEICHEN.id, meldung.getAenderungsKennzeichen()
                    .name());
            taskParams.put(WitaTaskVariables.WITA_MESSAGE_GESCHAEFTSFALL.id, meldung.getGeschaeftsfallTyp()
                    .getDtagMeldungGeschaeftsfall());
        }

        String businessKey = message.getExterneAuftragsnummer();
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstanceIfExists(businessKey);
        if (pi != null) {
            commonWorkflowService.handleWitaMessage(pi, taskParams);
        }
        else {
            handleMissingProcesssInstance(message);
        }
    }

    /**
     * Sollte eine ABBM oder VZM fuer einen bereits stornierten Vorgang eingehen, sollen diese einfach aufgenommen
     * werden (keine weitere Verarbeitung), auch wenn Sie fuer den Vorgang nicht mehr relevant sind.
     *
     * @throws WitaBpmException, wenn es sich nicht um genau den den beschriebenen Fall handelt, denn dann soll das
     *                           Standard-Verhalten stattfinden -> Workflow Error
     */
    private <T extends TalOrderMeldung> void handleMissingProcesssInstance(T message) {
        if (!shouldIgnoreMissingProcessInstance(message)) {
            throw new WitaBpmException("Konnte keinen aktiven WITA-Workflow mit dem Business-Key: "
                    + message.getExterneAuftragsnummer()
                    + " finden. Wahrscheinlich wurde der Workflow bereits abgeschlossen oder abgebrochen.");
        }
    }

    private <T extends TalOrderMeldung> boolean shouldIgnoreMissingProcessInstance(T message) {
        boolean isProcessInstanceFinished = ((message instanceof VerzoegerungsMeldung) || (message instanceof AbbruchMeldung))
                && commonWorkflowService.isProcessInstanceFinished(message.getExterneAuftragsnummer());

        if (!isProcessInstanceFinished) {
            return false;
        }

        CBVorgang cbVorgang = cbVorgangDao.findCBVorgangByCarrierRefNr(message.getExterneAuftragsnummer());
        return cbVorgang.hasAnswer();
    }

    @Override
    public void sendTvOrStornoRequest(MnetWitaRequest request) {
        Preconditions.checkNotNull(request, "request must be set.");
        Preconditions.checkNotNull(request.getId(), "request must have id (must be persisted) that it can be sent.");
        Preconditions.checkArgument((request instanceof TerminVerschiebung || request instanceof Storno),
                "request have to be a TV or Storno request.");

        // Den Aufruf von isSendAllowed bitte drin lassen obwohl es innerhalb der Route auch noch einaml aufgerufen wird.
        // Wenn man diese Pruefung nicht machen wuerde, wuerde der Workflow in den naechsten Status gesetzt (aus Sicht des
        // Workflows waere die Nachricht dann rausgeschickt). Bei einer Stornierung der TV- bzw. Storno-Request muesste
        // dann der Workflow explizit zurueckgerollt werden.
        if (witaConfigService.isSendAllowed(request)) {
            // @formatter:off
            Map<String, Object> params = ImmutableMap.<String, Object> of(
                    WITA_MESSAGE_TYPE.id, request.getMeldungsTyp().name(),
                    WITA_OUT_MWF_ID.id, request.getId());
            // @formatter:on
            commonWorkflowService.handleOutgoingWitaMessage(request.getExterneAuftragsnummer(), params);
        }
        else {
            LOGGER.info("MnetWitaRequest will not be sent now: " + request);
        }
    }

    @Override
    public WitaCBVorgang sendErlmk(WitaCBVorgang witaCbVorgang) {
        commonWorkflowService.handleOutgoingWitaMessage(witaCbVorgang.getBusinessKey(),
                ImmutableMap.<String, Object>of(WITA_MESSAGE_TYPE.id, MeldungsType.ERLM_K.name()));
        return witaCbVorgang;
    }
}

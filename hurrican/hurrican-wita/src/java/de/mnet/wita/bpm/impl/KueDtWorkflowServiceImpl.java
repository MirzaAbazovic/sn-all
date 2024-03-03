/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm.impl;

import java.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.KueDtWorkflowService;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.MessageOutOfOrderException;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.IncomingTalOrderMeldung;

@CcTxRequired
public class KueDtWorkflowServiceImpl implements KueDtWorkflowService {

    @Autowired
    CommonWorkflowService commonWorkflowService;
    @Autowired
    MwfEntityDao mwfEntityDao;

    @Override
    public ProcessInstance newProcessInstance(ErledigtMeldung erlm) {
        Preconditions.checkNotNull(erlm);
        Preconditions.checkNotNull(erlm.getBusinessKey());

        mwfEntityDao.store(erlm);
        Preconditions.checkNotNull(erlm.getId(), "Id must be set after storing erlm");

        ProcessInstance pi = checkExistingProcessInstance(erlm);
        if (pi != null) {
            return pi;
        }

        // @formatter:off
        Map<String, Object> defaultParams = ImmutableMap.<String, Object> of(
                WitaTaskVariables.WITA_MESSAGE_TYPE.id, MeldungsType.ERLM.name(),
                WitaTaskVariables.WITA_IN_MWF_ID.id, erlm.getId());
        // @formatter:on

        return commonWorkflowService.newProcessInstance(Workflow.KUE_DT, erlm.getBusinessKey(), defaultParams);
    }

    private ProcessInstance checkExistingProcessInstance(ErledigtMeldung erlm) {
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstanceIfExists(erlm.getExterneAuftragsnummer());
        if (pi == null) {
            HistoricProcessInstance hpi = commonWorkflowService.retrieveHistoricProcessInstanceIfExists(erlm
                    .getExterneAuftragsnummer());
            if (hpi != null) {
                throw new WitaBpmException("Zur externen Auftragsnummer " + erlm.getExterneAuftragsnummer()
                        + " der ERLM existiert bereits ein geschlossener Workflow.");
            }
        }
        else {
            commonWorkflowService
                    .setErrorState(erlm.getExterneAuftragsnummer(),
                            "Zweite ERLM zu offenem KueDt-Workflow empfangen. Bitte überprüfen und Workflow-Status korrigieren.");
        }
        return pi;
    }

    @Override
    public <T extends IncomingTalOrderMeldung> void handleWitaMessage(T message) {
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstanceIfExists(message.getExterneAuftragsnummer());
        if (pi == null) {
            throw new MessageOutOfOrderException(
                    "Die Meldung der KUE-DT kann nicht verarbeitet werden, da noch kein Workflow dazu geöffnet wurde. Meldung: "
                            + message
            );
        }

        mwfEntityDao.store(message);

        Map<String, Object> defaultParams = ImmutableMap.<String, Object>of(WitaTaskVariables.WITA_MESSAGE_TYPE.id,
                message.getMeldungsTyp().name());
        commonWorkflowService.handleWitaMessage(message.getExterneAuftragsnummer(), defaultParams);
    }
}

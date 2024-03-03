/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2011 15:42:13
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.bpm.WitaTaskVariables.*;

import java.util.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.variables.WitaActivitiVariableUtils;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.service.WitaUsertaskService;

/**
 * Task verarbeitet eine AKM-PV, indem er die zugehoerige Carrierbestellung sucht und einen neuen Hurrican-Usertask
 * anlegt.
 */
public class ProcessAkmPvTask extends AbstractProcessingWitaTask {

    private static final Logger LOGGER = Logger.getLogger(ProcessAkmPvTask.class);

    @Autowired
    private WitaUsertaskService witaUserTaskService;

    @Override
    protected void processMessage(DelegateExecution execution) throws Exception {
        MeldungsType meldungsTyp = WitaActivitiVariableUtils.extractMeldungsType(execution);
        if (meldungsTyp == MeldungsType.AKM_PV) {
            Long meldungsId = WitaActivitiVariableUtils.extractMeldungsId(execution);
            LOGGER.info(String.format("Received Meldung with meldungsId = %s", meldungsId));
            AnkuendigungsMeldungPv akmPv = mwfEntityDao.findById(meldungsId, AnkuendigungsMeldungPv.class);
            if (!workflowTaskService.validateMwfInput(akmPv, execution)) {
                LOGGER.warn(String.format("Fehler beim Validieren der AnkuendigungsMeldungPv mit der id: %s", akmPv.getId()));
                return;
            }

            Map<WitaTaskVariables, Object> witaTaskVariablesObjectMap = witaUserTaskService.getAutomaticAnswerForAkmPv(akmPv);
            if (CollectionUtils.isEmpty(witaTaskVariablesObjectMap)) {
                witaUserTaskService.createAkmPvUserTask(akmPv);
            }
            else {
                execution.setVariable(WITA_MESSAGE_TYPE.id, MeldungsType.RUEM_PV.name());
                for (WitaTaskVariables witaTaskVariables : witaTaskVariablesObjectMap.keySet()) {
                    execution.setVariable(witaTaskVariables.id, witaTaskVariablesObjectMap.get(witaTaskVariables));
                }
            }
        }
        else {
            workflowTaskService.setWorkflowToError(execution,
                    String.format("Wrong MeldungsTyp: %s; Expected MeldungsType: AKM-PV.", meldungsTyp));
        }
    }

}

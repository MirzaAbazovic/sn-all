/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.bpm.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.bpm.variables.WitaActivitiVariableUtils;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.service.WitaUsertaskService;

/**
 * Activiti-Task, der eine ERLM fuer den Geschaeftsfall KUE-DT behandelt.
 */
public class ProcessKueDtTask extends AbstractWitaTask {

    private static final Logger LOGGER = Logger.getLogger(ProcessKueDtTask.class);

    @Autowired
    private WitaUsertaskService witaUserTaskService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        MeldungsType meldungsTyp = WitaActivitiVariableUtils.extractMeldungsType(execution);

        if (meldungsTyp == MeldungsType.ERLM) {
            Long meldungsId = WitaActivitiVariableUtils.extractMeldungsId(execution);
            LOGGER.info("Received Meldung with meldungsId = " + meldungsId);
            ErledigtMeldung erlm = mwfEntityDao.findById(meldungsId, ErledigtMeldung.class);
            if (!workflowTaskService.validateMwfInput(erlm, execution)) {
                LOGGER.warn("Fehler beim Validieren ser ErledigtMeldung mit der id: " + erlm.getId());
                return;
            }
            // gescheiterte Persistierung sorgt fuer Deadletterqueueeintrag
            witaUserTaskService.createKueDtUserTask(erlm);
        }
        else {
            workflowTaskService.setWorkflowToError(execution, "Wrong MeldungsTyp: " + meldungsTyp + "; Expected MeldungsType: " + MeldungsType.ERLM);
        }
    }
}

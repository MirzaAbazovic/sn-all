/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.message.MeldungsType.*;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.bpm.variables.WitaActivitiVariableUtils;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.service.WitaUsertaskService;

/**
 * Activiti-Task, der eine zweite AKM-PV (bevor eine RUEM-PV geschickt wurde) entgegen nimmt. <br> Die zweite AKM-PV
 * aktualisiert dabei den UserTask, der durch die erste AKM-PV erstellt wurde.
 */
public class ProcessSecondAkmPvMessageTask extends AbstractProcessingWitaTask {

    private static final Logger LOGGER = Logger.getLogger(ProcessSecondAkmPvMessageTask.class);

    @Autowired
    private WitaUsertaskService witaUserTaskService;

    @Override
    protected void processMessage(DelegateExecution execution) throws Exception {
        Long meldungsId = WitaActivitiVariableUtils.extractMeldungsId(execution);
        MeldungsType messageTyp = WitaActivitiVariableUtils.extractMeldungsType(execution);

        LOGGER.info("Received Meldung with meldungsId = " + meldungsId);
        try {
            if (AKM_PV.equals(messageTyp)) {
                // bestehenden UserTask finden und Daten aktualisieren
                AnkuendigungsMeldungPv akmPv = mwfEntityDao.findById(meldungsId, AnkuendigungsMeldungPv.class);
                if (!workflowTaskService.validateMwfInput(akmPv, execution)) {
                    LOGGER.warn("Fehler beim Validieren der AnkuendigungsMeldungPv mit der id: " + akmPv.getId());
                    return;
                }
                witaUserTaskService.updateAkmPvUserTask(akmPv);
            }
            else {
                workflowTaskService.setWorkflowToError(execution, "Wrong MeldungsTyp: " + messageTyp
                        + "; Expected MeldungsType: AKM-PV.");
            }
        }
        catch (Exception e) {
            workflowTaskService.setWorkflowToError(execution, e.getMessage(), e);
        }
    }
}

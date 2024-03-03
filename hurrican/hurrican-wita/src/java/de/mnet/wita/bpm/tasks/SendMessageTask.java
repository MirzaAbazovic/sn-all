/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.bpm.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.Logger;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.bpm.variables.WitaActivitiVariableUtils;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;

/**
 * Activiti-Task, der WITA Messages entgegen nimmt und abhaengig vom Typ (z.B. ABM, ABBM) verarbeitet.
 */
public class SendMessageTask extends AbstractSendingWitaTask {

    private static final Logger LOG = Logger.getLogger(SendMessageTask.class);

    @Override
    protected void send(DelegateExecution execution, WitaCdmVersion witaCdmVersion) throws Exception {
        MeldungsType messageType = WitaActivitiVariableUtils.extractMeldungsType(execution);
        Long requestId = WitaActivitiVariableUtils.extractRequestId(execution);

        Class<? extends MnetWitaRequest> clazz;
        switch (messageType) {
            case STORNO:
                clazz = Storno.class;
                break;
            case TV:
                clazz = TerminVerschiebung.class;
                break;
            default:
                throw new WitaBpmException(messageType.name() + " cannot be processed within "
                        + this.getClass().getSimpleName());
        }

        try {
            MnetWitaRequest request = mwfEntityDao.findById(requestId, clazz);
            request.setCdmVersion(witaCdmVersion);
            LOG.info(String.format("Trigger sending of %s with MwfEntity-Id = %s.", messageType.toString(), requestId));
            workflowTaskService.sendToWita(request);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            workflowTaskService.setWorkflowToError(execution,
                    String.format("Senden von %s fehlgeschlagen. Grund: %s", messageType.toString(), e.getMessage()));
        }
    }
}

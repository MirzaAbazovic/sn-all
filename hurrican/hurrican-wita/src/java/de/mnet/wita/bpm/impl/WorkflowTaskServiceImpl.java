/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm.impl;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.WorkflowTaskService;
import de.mnet.wita.bpm.WorkflowTaskValidationService;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.service.WitaSendMessageService;

/**
 * Abstraction-Layer fuer die Activiti Workflows im Context einer TAL-Bestellung.
 */
@CcTxRequired
public class WorkflowTaskServiceImpl implements WorkflowTaskService {

    public static final int MAX_VARIABLE_STRING_LENGTH = 2000;
    private static final Logger LOGGER = Logger.getLogger(WorkflowTaskService.class);
    @Autowired
    private WorkflowTaskValidationService workflowTaskValidationService;

    @Autowired
    private WitaSendMessageService witaSendMessageService;

    @Autowired
    private MwfEntityDao mwfEntityDao;

    @Override
    public <T extends WitaMessage> void sendToWita(T request) throws WitaUserException {
        try {
            mwfEntityDao.store(request);
            witaSendMessageService.sendAndProcessMessage(request);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaUserException("Fehler beim Senden der WITA Nachricht: " + e.getMessage(), e);
            // TODO Validierung und Persistierung aller Requests muss vorher passieren!!!
            // => WitaUserException rausnehmen, da dann nicht mehr relevant für User
        }
    }

    /**
     * Check bean validation constraints for the incoming Meldung. in case of an error set the error flag and pass over
     * the validation messages.
     */
    @Override
    public boolean validateMwfInput(Meldung<?> meldung, DelegateExecution execution) {
        if (meldung != null) {
            String errorMsg = workflowTaskValidationService.validateMwfInput(meldung);
            if (errorMsg != null) {
                setWorkflowToError(execution, errorMsg);
                return false;
            }
        }
        else {
            setWorkflowToError(execution, "Keine Meldung zum Validieren.");
            return false;
        }
        return true;
    }

    @Override
    public void setWorkflowToError(DelegateExecution execution, String errorMessage) {
        execution.setVariable(WitaTaskVariables.WORKFLOW_ERROR.id, true);
        execution.setVariable(WitaTaskVariables.WORKFLOW_ERROR_MESSAGE.id,
                StringUtils.substring(errorMessage, 0, MAX_VARIABLE_STRING_LENGTH));

        // Variablen muessen auf null gesetzt werden, um eindeutig Error-Kante zu nehmen. Falls der Type spaeter noch
        // benoetigt
        // wird, muss die Bedingung workflowError == false explizit in die *.bpm20.xml mit aufgenommen werden
        execution.setVariable(WitaTaskVariables.WITA_MESSAGE_TYPE.id, null);
        execution.setVariable(WitaTaskVariables.WITA_MESSAGE_AENDERUNGSKENNZEICHEN.id, null);
        execution.setVariable(WitaTaskVariables.WITA_MESSAGE_GESCHAEFTSFALL.id, null);
    }

    @Override
    public void setWorkflowToError(DelegateExecution execution, String errorMessage, Exception e) {
        LOGGER.info("Set workflow to error", e);
        setWorkflowToError(execution, errorMessage);
    }

}

/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.bpm.tasks;

import static de.augustakom.hurrican.model.cc.tal.CBVorgang.*;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.model.WitaCBVorgang;

/**
 * Activiti-Task, der eingehende WITA Messages entgegennimmt und nur eine TEQ akzeptiert.
 * This activity task is deprecated since TEQs sent by WITA are no longer received by Hurrican. The task however can
 * only be removed when all activiti process instances, that were created when TEQs were sent by WITA, have been
 * completed.
 */
@Deprecated
public class ProcessTeqTask extends AbstractProcessingWitaTask {

    @Override
    protected void processMessage(DelegateExecution execution) throws Exception {
        WitaCBVorgang cbVorgang = getCbVorgangOrNull(execution);
        if ((cbVorgang != null) && (cbVorgang.getStatus() < STATUS_TRANSFERRED)) {
            cbVorgang.setStatus(STATUS_TRANSFERRED);
            carrierElTalService.saveCBVorgang(cbVorgang);
        }
    }
}

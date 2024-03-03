/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 13:34:44
 */
package de.mnet.wita.bpm.gateways;

import static de.mnet.wita.bpm.variables.WitaActivitiVariableUtils.*;
import static de.mnet.wita.message.MeldungsType.*;

import java.time.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;

/**
 * Gateway zur Auslagerung der komplexen Bedingungen aus den ProcessTam-Definitionen
 */
public class ProcessTamGateway extends AbstractGateway {

    @Autowired
    MwfEntityDao mwfEntityDao;

    public boolean processSecondTAM(DelegateExecution execution) {
        return extractMeldungsType(execution) == TAM && !workflowError(execution);
    }

    public boolean processMessage(DelegateExecution execution) {
        MeldungsType meldungsType = extractMeldungsType(execution);
        return (VZM == meldungsType || isAbmWithFutureVlt(execution) || ERLM == meldungsType || ABBM == meldungsType)
                && !workflowError(execution);
    }

    boolean isAbmWithFutureVlt(DelegateExecution execution) {
        if (ABM != extractMeldungsType(execution)) {
            return false;
        }
        Long meldungsId = extractMeldungsId(execution);
        AuftragsBestaetigungsMeldung abm = mwfEntityDao.findById(meldungsId, AuftragsBestaetigungsMeldung.class);
        if (abm == null || abm.getVerbindlicherLiefertermin().isBefore(LocalDate.now())) {
            return false;
        }
        return true;
    }
}

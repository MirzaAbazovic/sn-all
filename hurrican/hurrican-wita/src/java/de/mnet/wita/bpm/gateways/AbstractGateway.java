/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 13:35:49
 */
package de.mnet.wita.bpm.gateways;

import static de.mnet.wita.bpm.variables.WitaActivitiVariableUtils.*;
import static de.mnet.wita.message.MeldungsType.*;
import static de.mnet.wita.message.MeldungsType.STORNO;
import static de.mnet.wita.message.meldung.position.AenderungsKennzeichen.*;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.variables.ActivitiVariableUtils;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;

public class AbstractGateway {

    public boolean sendErlmk(DelegateExecution execution) {
        return ERLM_K == extractMeldungsType(execution) && !workflowError(execution);
    }

    public boolean sendMessage(DelegateExecution execution) {
        return isStornoOrTv(execution) && !workflowError(execution);
    }

    public boolean workflowError(DelegateExecution execution) {
        return ActivitiVariableUtils.extractVariableSilent(execution, WitaTaskVariables.WORKFLOW_ERROR, Boolean.class,
                false);
    }

    protected boolean isAbbmForStandard(MeldungsType meldungsType, AenderungsKennzeichen aenderungsKennzeichen) {
        return meldungsType == MeldungsType.ABBM && aenderungsKennzeichen == STANDARD;
    }

    protected boolean isVzm(DelegateExecution execution) {
        return extractMeldungsType(execution) == VZM;
    }

    protected boolean isStornoOrTv(DelegateExecution execution) {
        MeldungsType meldungsType = extractMeldungsType(execution);
        return (STORNO == meldungsType || TV == meldungsType);
    }

    protected GeschaeftsfallTyp extractMessageGeschaeftsfall(DelegateExecution execution) {
        String messageGeschaeftsfallString = ActivitiVariableUtils.extractVariableSilent(execution,
                WitaTaskVariables.WITA_MESSAGE_GESCHAEFTSFALL, String.class, null);
        return messageGeschaeftsfallString == null ? null : GeschaeftsfallTyp
                .buildFromMeldung(messageGeschaeftsfallString);
    }

    protected AenderungsKennzeichen extractAenderungsKennzeichen(DelegateExecution execution) {
        String aenderungsKennzeichen = ActivitiVariableUtils.extractVariableSilent(execution,
                WitaTaskVariables.WITA_MESSAGE_AENDERUNGSKENNZEICHEN, String.class, null);
        return aenderungsKennzeichen == null ? null : AenderungsKennzeichen.valueOf(aenderungsKennzeichen);
    }
}

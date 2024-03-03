/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2012 17:24:37
 */
package de.mnet.wita.bpm.variables;

import static de.mnet.wita.bpm.WitaTaskVariables.*;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.MeldungsType;

public class WitaActivitiVariableUtils extends ActivitiVariableUtils {

    public static Long extractRequestId(DelegateExecution execution) {
        Long requestId = extractVariable(execution, WITA_OUT_MWF_ID, Long.class);
        if (requestId == null) {
            throw new WitaBaseException("Keine Wita-Out-Request-Id gefunden");
        }
        return requestId;
    }

    public static Long extractMeldungsId(DelegateExecution execution) {
        Long meldungsId = extractVariable(execution, WITA_IN_MWF_ID, Long.class);
        if (meldungsId == null) {
            throw new WitaBaseException("Keine WitaInMeldung erhalten");
        }
        return meldungsId;
    }

    public static MeldungsType extractMeldungsType(DelegateExecution execution) {
        // TODO warum schlägt TalOrderWorkflowServiceImplActivitiErrorTest fehl, wenn hier nicht Silent geschaut wird
        String meldungsTypVariable = extractVariableSilent(execution, WITA_MESSAGE_TYPE, String.class, null);
        if (meldungsTypVariable == null) {
            return null;
        }

        MeldungsType meldungsTyp = MeldungsType.valueOf(meldungsTypVariable);
        if (meldungsTyp == null) {
            throw new WitaBaseException(String.format("Meldungstyp %s wird nicht unterstuetzt", meldungsTypVariable));
        }
        return meldungsTyp;
    }
}

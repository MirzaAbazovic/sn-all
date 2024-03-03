/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.bpm.WitaTaskVariables.*;
import static de.mnet.wita.message.MeldungsType.*;

import com.google.common.collect.ImmutableList;
import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.MeldungsType;

/**
 * Activiti-Task, der eine ENTM (Entgeldmeldung) entgegennimmt.
 */
public class ProcessENTMMessageTask extends AbstractProcessingWitaTask {

    @Override
    protected void processMessage(DelegateExecution execution) throws Exception {
        String meldungsTypVariable = (String) execution.getVariable(WITA_MESSAGE_TYPE.id);
        if (meldungsTypVariable == null) {
            throw new WitaBaseException("Kein Meldungstyp vorhanden");
        }

        MeldungsType meldungsTyp = MeldungsType.valueOf((String) execution.getVariable(WITA_MESSAGE_TYPE.id));
        if (meldungsTyp == null) {
            throw new WitaBaseException(String.format("Meldungstyp %s wird nicht unterstuetzt", meldungsTypVariable));
        }
        if (!ImmutableList.of(ENTM, ERLM, VZM).contains(meldungsTyp)) {
            workflowTaskService.setWorkflowToError(execution,
                    "Die Meldung (Typ: "
                            + meldungsTyp
                            + ")kann nicht ausgeführt werden, da auf eine ENTM Meldung gewartet wird.\n"
                            +
                            "Terminverschiebungen oder Storno (und weitere Aktionen) sind zu diesem Zeitpunkt nicht mehr erlaubt."
            );
        }
    }

}

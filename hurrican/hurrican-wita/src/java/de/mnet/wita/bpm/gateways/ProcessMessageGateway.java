/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.12.2011 13:35:22
 */
package de.mnet.wita.bpm.gateways;

import static de.mnet.wita.bpm.variables.WitaActivitiVariableUtils.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.MeldungsType.*;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;

public class ProcessMessageGateway extends AbstractGateway {

    public boolean processTAM(DelegateExecution execution) {
        return MeldungsType.TAM == extractMeldungsType(execution) && !workflowError(execution);
    }

    public boolean end(DelegateExecution execution) {
        GeschaeftsfallTyp messageGeschaeftsfall = extractMessageGeschaeftsfall(execution);
        MeldungsType meldungsType = extractMeldungsType(execution);
        AenderungsKennzeichen aenderungsKennzeichen = extractAenderungsKennzeichen(execution);
        return (isErlmOnRexMk(messageGeschaeftsfall, meldungsType, aenderungsKennzeichen) ||
                isAbbmForStandard(meldungsType, aenderungsKennzeichen)) && !workflowError(execution);
    }

    public boolean waitForENTMMessage(DelegateExecution execution) {
        GeschaeftsfallTyp messageGeschaeftsfall = extractMessageGeschaeftsfall(execution);
        MeldungsType meldungsType = extractMeldungsType(execution);
        AenderungsKennzeichen aenderungsKennzeichen = extractAenderungsKennzeichen(execution);
        return meldungsType == MeldungsType.ERLM
                && !isErlmOnRexMk(messageGeschaeftsfall, meldungsType, aenderungsKennzeichen)
                && !workflowError(execution);
    }

    private boolean isErlmOnRexMk(GeschaeftsfallTyp messageGeschaeftsfall, MeldungsType meldungsType,
            AenderungsKennzeichen aenderungsKennzeichen) {
        return messageGeschaeftsfall == RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG && meldungsType == ERLM
                && aenderungsKennzeichen != AenderungsKennzeichen.STORNO;
    }
}

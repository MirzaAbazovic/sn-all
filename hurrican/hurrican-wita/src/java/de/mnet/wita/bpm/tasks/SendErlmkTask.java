/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2011 14:47:34
 */
package de.mnet.wita.bpm.tasks;

import org.activiti.engine.delegate.DelegateExecution;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;

public class SendErlmkTask extends AbstractProcessingSendWitaTask {

    @Override
    public void processMessage(DelegateExecution execution, WitaCdmVersion witaCdmVersion) throws Exception {
        verifyMeldungsType(execution, MeldungsType.ERLM_K);

        WitaCBVorgang cbVorgang = getCbVorgang(execution);
        if (AenderungsKennzeichen.STORNO.equals(cbVorgang.getAenderungsKennzeichen())) {
            throw new WitaUserException(
                    "Für einen Auftrag, der gerade storniert wird darf keine ErledigtMeldungKunde verschickt werden.");
        }

        Auftrag auftrag = mwfEntityDao.getAuftragOfCbVorgang(cbVorgang.getId());
        carrierElTalService.saveCBVorgang(cbVorgang);
        ErledigtMeldungKunde request = new ErledigtMeldungKunde(cbVorgang.getReturnVTRNR(), auftrag);
        request.setCdmVersion(witaCdmVersion);
        workflowTaskService.sendToWita(request);
    }

}

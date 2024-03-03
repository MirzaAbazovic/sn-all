/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2011 15:21:31
 */
package de.mnet.wita.bpm.converter.usertask;

import static de.mnet.wita.model.AkmPvUserTask.AkmPvStatus.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.service.WitaUsertaskService;

public class AbmPvUserTaskConverter {

    @Autowired
    private WitaUsertaskService witaUsertaskService;

    public void write(AuftragsBestaetigungsMeldungPv abmPv) {
        AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTask(abmPv.getExterneAuftragsnummer());
        if (userTask == null) {
            userTask = witaUsertaskService.createAkmPvUserTask(abmPv);
        }
        else if (AkmPvUserTask.AkmPvStatus.ABM_PV_EMPFANGEN.equals(userTask.getAkmPvStatus())) {
            // UserTask markieren, dass eine weitere ABM-PV empfangen wurde
            userTask.setTerminverschiebung(Boolean.TRUE);
        }
        
        userTask.changeAkmPvStatus(ABM_PV_EMPFANGEN);
        userTask.setKuendigungsDatum(abmPv.getAufnehmenderProvider().getUebernahmeDatumVerbindlich());
        userTask.setAntwortFrist(null);
        userTask.setWiedervorlageAm((Date)null);
        witaUsertaskService.storeUserTask(userTask);
    }
}

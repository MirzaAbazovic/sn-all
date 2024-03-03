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

import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.service.WitaUsertaskService;

public class AbbmPvUserTaskConverter {

    @Autowired
    private WitaUsertaskService witaUsertaskService;

    public void write(AbbruchMeldungPv abbmPv) {
        AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTask(abbmPv.getExterneAuftragsnummer());
        if (userTask == null) {
            userTask = witaUsertaskService.createAkmPvUserTask(abbmPv);
        }
        userTask.changeAkmPvStatus(ABBM_PV_EMPFANGEN);
        userTask.setAntwortFrist(null);
        userTask.setWiedervorlageAm((Date)null);
        witaUsertaskService.storeUserTask(userTask);
    }
}

/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2011 12:48:57
 */
package de.mnet.wita.bpm.converter.usertask;

import static de.mnet.wita.model.AkmPvUserTask.AkmPvStatus.*;

import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.service.WitaUsertaskService;

public class RuemPvUserTaskConverter {
    @Autowired
    private WitaUsertaskService witaUsertaskService;

    public void write(RueckMeldungPv ruemPv) {
        AkmPvUserTask userTask = witaUsertaskService.findAkmPvUserTask(ruemPv.getExterneAuftragsnummer());
        if (userTask != null) { // no usertask if automatic answer for Akm-Pv
            userTask.changeAkmPvStatus(RUEM_PV_GESENDET);
            witaUsertaskService.storeUserTask(userTask);
        }
    }
}

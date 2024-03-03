/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.hurrican.acceptance.behavior;

import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Performs create command data request with hardware equipment, leiste and 1-n stifte.
 *
 */
public class SendCommandStiftData_Behavior extends AbstractTestBehavior {

    private final String hwEqn;
    private final String leiste;
    private final String[] stifte;

    /**
     * Constructor initializing fields.
     * @param hwEqn
     * @param leiste
     * @param stifte
     */
    public SendCommandStiftData_Behavior(String hwEqn, String leiste, String ... stifte) {
        this.hwEqn = hwEqn;
        this.leiste = leiste;
        this.stifte = stifte;
    }

    @Override
    public void apply() {
        if (hwEqn !=  null) {
            variable(VariableNames.HW_EQN, hwEqn);
        }

        variable(VariableNames.LEISTE, leiste);

        for (int i = 0; i < stifte.length; i++) {
            variable(VariableNames.STIFT + (i + 1), stifte[i]);
        }

        resourceInventory().sendCommandStiftDataRequest("commandStiftDataRequest");
        resourceInventory().receiveCommandStiftDataResponse("commandStiftDataResponse");

        for (int i = 0; i < stifte.length; i++) {
            hurrican().verifyEquipment(null, leiste, stifte[i]);
        }
    }
}

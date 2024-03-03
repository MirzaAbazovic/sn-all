/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.hurrican.acceptance.behavior;

import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class SendUpdateResource_Port_Behavior extends AbstractTestBehavior {

    private final String hwEqn;
    private final String leiste;
    private final String stift;

    /**
     * Constructor initializing fields.
     * @param hwEqn
     * @param leiste
     * @param stift
     */
    public SendUpdateResource_Port_Behavior(String hwEqn, String leiste, String stift) {
        this.hwEqn = hwEqn;
        this.leiste = leiste;
        this.stift = stift;
    }

    @Override
    public void apply() {
        variable(VariableNames.HW_EQN, hwEqn);
        variable(VariableNames.LEISTE, leiste);
        variable(VariableNames.STIFT, stift);

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_Port");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyDpoPortCreated();
        hurrican().verifyEquipment(hwEqn, leiste, stift);
    }
}

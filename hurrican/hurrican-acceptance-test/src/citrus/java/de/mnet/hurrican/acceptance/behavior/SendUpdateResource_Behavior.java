/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.14
 */
package de.mnet.hurrican.acceptance.behavior;

/**
 * Simply calls update resource as Command resource inventory on Hurrican server. Receives proper acknowledgement from Hurrican
 * and checks resource creation in database.
 *
 *
 */
public class SendUpdateResource_Behavior extends AbstractTestBehavior {
    private final String hwRackType;

    /**
     * Initialize behavior with hardware rack type.
     * @param hwRackType
     */
    public SendUpdateResource_Behavior(String hwRackType) {
        this.hwRackType = hwRackType;
    }

    @Override
    public void apply() {
        resourceInventory().sendUpdateResourceRequest("updateResourceRequest");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyHwRackCreated(hwRackType);
    }
}

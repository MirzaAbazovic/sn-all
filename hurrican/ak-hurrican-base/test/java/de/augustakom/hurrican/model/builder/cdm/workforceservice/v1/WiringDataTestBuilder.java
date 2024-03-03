/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class WiringDataTestBuilder extends WiringDataBuilder {

    public WiringDataTestBuilder() {
        withDeviceName("MDU-416291");
        withModuleType("MA5652G_VDSL2");
        withPhysicType("FTTB_VDSL2");
        withHWEQN("0-1-1");
        withPanelPin1("LMN-405723 02");
        withDistributor("distributor");
        withLayer2Protocol("layer2protocol");
        withONTID("ontid");
        withPanelPin2("panelpin2");
        withCrossing(new CrossingTestBuilder().build());
    }
}

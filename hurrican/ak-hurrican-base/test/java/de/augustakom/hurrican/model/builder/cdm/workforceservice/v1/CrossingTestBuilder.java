/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015 08:51
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class CrossingTestBuilder extends CrossingBuilder {

    public CrossingTestBuilder()   {
        withDistributor("testdistributor");
        withHwEqn("0030-00-01-00");
        withPanelPin1("LMN-405723 03");
        withPanelPin2("LMN-405723 04");
        withPhysikType("ADSL2+");
    }
}

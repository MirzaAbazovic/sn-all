/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 12:39:30
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;


/**
 * EntityBuilder for HWSubrackTyp objects
 *
 *
 */
@SuppressWarnings("unused")
public class HWSubrackTypBuilder extends AbstractCCIDModelBuilder<HWSubrackTypBuilder, HWSubrackTyp> {
    private String name = "ALTS-X";
    private String description = "UnitTest/IntegrationTest Subrack Typ";
    private String rackTyp = "DSLAM";
    private Integer bgCount = 16;
    private Integer portCount = 48;
    private String hwTypeName = "XDSL-MUC";

    public HWSubrackTypBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HWSubrackTypBuilder withHwTypeName(String hwTypeName) {
        this.hwTypeName = hwTypeName;
        return this;
    }

    public HWSubrackTypBuilder withRackTyp(String rackTyp) {
        this.rackTyp = rackTyp;
        return this;
    }
}

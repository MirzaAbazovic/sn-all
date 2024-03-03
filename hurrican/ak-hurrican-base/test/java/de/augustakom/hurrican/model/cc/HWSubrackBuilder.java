/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2009 10:57:28
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.UseBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;


/**
 * EntityBuilder for HWBaugruppe objects
 *
 *
 */
@SuppressWarnings("unused")
public class HWSubrackBuilder extends AbstractCCIDModelBuilder<HWSubrackBuilder, HWSubrack> {
    private HWSubrackTypBuilder subrackTypBuilder;
    @UseBuilder(HWDslamBuilder.class)
    private HWRackBuilder<?, ?> rackBuilder;
    private String modNumber = "1-1";


    public HWRackBuilder<?, ?> getRackBuilder() {
        return rackBuilder;
    }

    public HWSubrackTypBuilder getSubrackTypBuilder() {
        return subrackTypBuilder;
    }


    public HWSubrackBuilder withSubrackTypBuilder(HWSubrackTypBuilder subrackTypBuilder) {
        this.subrackTypBuilder = subrackTypBuilder;
        return this;
    }

    public HWSubrackBuilder withRackBuilder(HWRackBuilder<?, ?> rackBuilder) {
        this.rackBuilder = rackBuilder;
        return this;
    }

    public HWSubrackBuilder withModNumber(String modNumber) {
        this.modNumber = modNumber;
        return this;
    }
}

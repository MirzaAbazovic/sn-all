/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 18:38:02
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;


/**
 *
 */
public class HWOntBuilder extends HWOltChildBuilder<HWOntBuilder, HWOnt> {

    private String ontType = HWOnt.ONT_TYPE_O123T;

    @Override
    protected void initialize() {
        super.withRackTyp(HWRack.RACK_TYPE_ONT);
    }

    public HWOntBuilder withOntType(String ontType) {
        this.ontType = ontType;
        return this;
    }

}



/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 18:38:02
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWRack;


/**
 *
 */
@SuppressWarnings("unused")
public class HWMduBuilder extends HWOltChildBuilder<HWMduBuilder, HWMdu> {

    private Boolean catvOnline = null;
    private String mduType = HWMdu.MDU_TYPE_MA5652G;

    @Override
    protected void initialize() {
        super.withRackTyp(HWRack.RACK_TYPE_MDU);
    }

}



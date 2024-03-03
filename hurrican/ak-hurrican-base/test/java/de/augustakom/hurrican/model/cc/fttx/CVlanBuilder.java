/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.04.2012 15:45:06
 */
package de.augustakom.hurrican.model.cc.fttx;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;
import de.augustakom.hurrican.model.cc.fttx.CVlan.CVlanProtocoll;

/**
 *
 */
@SuppressWarnings("unused")
public class CVlanBuilder extends AbstractCCIDModelBuilder<CVlanBuilder, CVlan> {

    private CvlanServiceTyp typ = CvlanServiceTyp.HSI;
    private Integer value = 7;
    private Integer pbitLimit;
    private CVlanProtocoll protocoll = CVlanProtocoll.PPPoE;

    public CVlanBuilder withTyp(CvlanServiceTyp typ) {
        this.typ = typ;
        return this;
    }

    public CVlanBuilder withValue(Integer value) {
        this.value = value;
        return this;
    }

    public CVlanBuilder withPbitLimit(Integer pbitLimit) {
        this.pbitLimit = pbitLimit;
        return this;
    }

    public CVlanBuilder withProtocoll(CVlanProtocoll protocoll) {
        this.protocoll = protocoll;
        return this;
    }

}



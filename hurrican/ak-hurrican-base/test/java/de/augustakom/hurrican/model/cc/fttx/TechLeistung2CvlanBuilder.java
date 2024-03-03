/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 12:12:28
 */
package de.augustakom.hurrican.model.cc.fttx;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;

@SuppressWarnings("unused")
public class TechLeistung2CvlanBuilder extends EntityBuilder<TechLeistung2CvlanBuilder, TechLeistung2Cvlan> {

    private TechLeistungBuilder techLeistungBuilder;
    private CvlanServiceTyp cvlanTyp = CvlanServiceTyp.HSI;
    private Boolean removeLogic = Boolean.FALSE;

    public TechLeistung2CvlanBuilder withTechLeistungBuilder(TechLeistungBuilder techLeistungBuilder) {
        this.techLeistungBuilder = techLeistungBuilder;
        return this;
    }

    public TechLeistung2CvlanBuilder withCvlanServiceType(CvlanServiceTyp cvlanTyp) {
        this.cvlanTyp = cvlanTyp;
        return this;
    }

    public TechLeistung2CvlanBuilder withRemoveLogic(Boolean removeLogic) {
        this.removeLogic = removeLogic;
        return this;
    }
}



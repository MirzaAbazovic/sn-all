/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 11:27:52
 */
package de.augustakom.hurrican.model.cc.fttx;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.EntityBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;

@SuppressWarnings("unused")
public class Produkt2CvlanBuilder extends EntityBuilder<Produkt2CvlanBuilder, Produkt2Cvlan> {

    private ProduktBuilder produktBuilder;
    private CvlanServiceTyp cvlanTyp = CvlanServiceTyp.HSI;
    private Long techLocationTypeRefId;
    @DontCreateBuilder
    private HVTTechnikBuilder hvtTechnikBuilder;
    private Boolean isMandatory = Boolean.TRUE;

    @Override
    protected void beforeBuild() {
        if (produktBuilder == null) {
            produktBuilder = new ProduktBuilder();
        }
    }

    public Produkt2CvlanBuilder withProduktBuilder(ProduktBuilder produktBuilder) {
        this.produktBuilder = produktBuilder;
        return this;
    }

    public Produkt2CvlanBuilder withIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
        return this;
    }

    public Produkt2CvlanBuilder withCvlanServiceType(CvlanServiceTyp cvlanTyp) {
        this.cvlanTyp = cvlanTyp;
        return this;
    }

    public Produkt2CvlanBuilder withTechLocationTypeRefId(Long techLocationTypeRefId) {
        this.techLocationTypeRefId = techLocationTypeRefId;
        return this;
    }

    public Produkt2CvlanBuilder withHvtTechnikBuilder(HVTTechnikBuilder hvtTechnikBuilder) {
        this.hvtTechnikBuilder = hvtTechnikBuilder;
        return this;
    }

}

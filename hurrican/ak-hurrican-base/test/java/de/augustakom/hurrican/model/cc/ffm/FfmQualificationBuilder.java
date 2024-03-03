/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;

/**
 * because of FfmQualification does not know that it has a foreign key to FfmProductMapping set persist always to false
 * that FfmQualification is only saved by using saving FfmProductMapping.
 */
@SuppressWarnings("unused")
public class FfmQualificationBuilder extends AbstractCCIDModelBuilder<FfmQualificationBuilder, FfmQualification> {

    private String qualification = randomString(100);
    private Integer additionalDuration = 0;

    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        super.setPersist(false);
    }

    @Override
    public boolean getPersist() {
        return false;
    }

    public FfmQualificationBuilder withAdditionalDuration(int duration) {
        this.additionalDuration = duration;
        return this;
    }

    public FfmQualificationBuilder withQualification(String qualification) {
        this.qualification = qualification;
        return this;
    }
}

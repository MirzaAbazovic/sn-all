/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 07.12.2016

 */

package de.augustakom.hurrican.model.cc;

@SuppressWarnings("unused") //AbstractCCIDModelBuilder uses reflection-based access, so "unused can be ignored"
public class ProfileParameterBuilder extends AbstractCCIDModelBuilder<ProfileParameterBuilder, ProfileParameter> {

    private HWBaugruppenTypBuilder baugruppenTypBuilder;
    private String parameterName = randomString(4);
    private String parameterValue = randomString(4);
    private Boolean isDefault = Boolean.FALSE;

    public ProfileParameterBuilder withHWBaugruppenTypBuilder(final HWBaugruppenTypBuilder baugruppenTypBuilder) {
        this.baugruppenTypBuilder = baugruppenTypBuilder;
        return this;
    }

    public ProfileParameterBuilder withIsDefault(final boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

}

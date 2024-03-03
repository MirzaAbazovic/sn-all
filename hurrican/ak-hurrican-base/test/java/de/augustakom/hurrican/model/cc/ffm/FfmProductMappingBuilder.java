/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;

/**
 * because of FfmQualification does not know that it has a foreign key to FfmProductMapping set persist always to false
 * that a FfmQualification is always saved with their corresponding FfmProductMapping.
 */
@SuppressWarnings("unused")
public class FfmProductMappingBuilder extends AbstractCCIDModelBuilder<FfmProductMappingBuilder, FfmProductMapping> {

    private Long produktId;
    private FfmTyp baFfmTyp;
    private Long standortTypRefId;
    private String ffmActivityType = randomString(100);
    private Integer ffmPlannedDuration = Integer.valueOf(45);
    private FfmAggregationStrategy aggregationStrategy = FfmAggregationStrategy.HEADER_ONLY_WITH_TIMESLOT;

    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        super.setPersist(false);
    }

    public boolean getPersist() {
        return false;
    }

    public FfmProductMappingBuilder withAggregationStrategy(FfmAggregationStrategy aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
        return this;
    }

    public FfmProductMappingBuilder withProduktId(Long produktId) {
        this.produktId = produktId;
        return this;
    }

    public FfmProductMappingBuilder withBaFfmTyp(FfmTyp baFfmTyp) {
        this.baFfmTyp = baFfmTyp;
        return this;
    }

    public FfmProductMappingBuilder withStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
        return this;
    }

    public FfmProductMappingBuilder withFfmActivityType(String ffmActivityType) {
        this.ffmActivityType = ffmActivityType;
        return this;
    }

    public FfmProductMappingBuilder withFfmPlannedDuration(Integer ffmPlannedDuration) {
        this.ffmPlannedDuration = ffmPlannedDuration;
        return this;
    }
}

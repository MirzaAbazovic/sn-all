/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.GebaeudeteilType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class GebaeudeteilTypeBuilder implements LineOrderNotificationTypeBuilder<GebaeudeteilType> {

    private String gebaeudeteilName;
    private String gebaeudeteilZusatz;

    @Override
    public GebaeudeteilType build() {
        GebaeudeteilType gebaeudeteilType = new GebaeudeteilType();
        gebaeudeteilType.setGebaeudeteilName(gebaeudeteilName);
        gebaeudeteilType.setGebaeudeteilZusatz(gebaeudeteilZusatz);
        return gebaeudeteilType;
    }

    public GebaeudeteilTypeBuilder withGebaeudeteilName(String gebaeudeteilName) {
        this.gebaeudeteilName = gebaeudeteilName;
        return this;
    }

    public GebaeudeteilTypeBuilder withGebaeudeteilZusatz(String gebaeudeteilZusatz) {
        this.gebaeudeteilZusatz = gebaeudeteilZusatz;
        return this;
    }

}

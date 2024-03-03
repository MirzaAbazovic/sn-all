/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 * Created by glinkjo on 06.02.2015.
 */
public class ElectricMeterTestBuilder extends ElectricMeterBuilder {

    public ElectricMeterTestBuilder() {
        withEmId("99");
        withProvisioning(1.4D);
        withTermination(9285.7D);
    }

}

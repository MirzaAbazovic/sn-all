/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import java.math.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;

@SuppressWarnings("unused")
public class FfmFeedbackMaterialBuilder extends AbstractCCIDModelBuilder<FfmFeedbackMaterialBuilder, FfmFeedbackMaterial> {

    private String workforceOrderId = "HUR_"+ randomString(96);
    private String materialId = randomString(100);
    private String serialNumber = randomString(100);
    private String summary = randomString(100);
    private String description = randomString(255);
    private BigDecimal quantity = new BigDecimal(1);
    private Boolean processed = Boolean.FALSE;

    public FfmFeedbackMaterialBuilder withWorkforceOrderId(String workforceOrderId) {
        this.workforceOrderId = workforceOrderId;
        return this;
    }

}

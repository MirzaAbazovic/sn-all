/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.FfmHelper;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 * Created by glinkjo on 06.02.2015.
 */
public class ElectricMeterBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Housing.ElectricMeter> {

    private String emId;
    protected Double provisioning;
    protected Double termination;

    @Override
    public OrderTechnicalParams.Housing.ElectricMeter build() {
        OrderTechnicalParams.Housing.ElectricMeter electricMeter = new OrderTechnicalParams.Housing.ElectricMeter();
        electricMeter.setEmId(this.emId);
        electricMeter.setProvisioning(FfmHelper.convertDouble(this.provisioning));
        electricMeter.setTermination(FfmHelper.convertDouble(this.termination));
        return electricMeter;
    }

    public ElectricMeterBuilder withEmId(String emId) {
        this.emId = emId;
        return this;
    }

    public ElectricMeterBuilder withProvisioning(Double provisioning) {
        this.provisioning = provisioning;
        return this;
    }

    public ElectricMeterBuilder withTermination(Double termination) {
        this.termination = termination;
        return this;
    }

}

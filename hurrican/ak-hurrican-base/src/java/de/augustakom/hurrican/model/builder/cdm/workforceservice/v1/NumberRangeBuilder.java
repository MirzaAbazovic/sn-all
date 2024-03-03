/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class NumberRangeBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.DialNumber.NumberRange> {

    protected String from;
    protected String to;
    protected String central;

    @Override
    public OrderTechnicalParams.DialNumber.NumberRange build() {
        OrderTechnicalParams.DialNumber.NumberRange numberRange = new OrderTechnicalParams.DialNumber.NumberRange();
        numberRange.setCentral(this.central);
        numberRange.setFrom(this.from);
        numberRange.setTo(this.to);
        return numberRange;
    }

    public NumberRangeBuilder withCentral(String central) {
        this.central = central;
        return this;
    }

    public NumberRangeBuilder withFrom(String from) {
        this.from = from;
        return this;
    }

    public NumberRangeBuilder withTo(String to) {
        this.to = to;
        return this;
    }
}
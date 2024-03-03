/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015 14:19
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class DialNumberPlanBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.DialNumber.DialNumberPlan>  {

    private String onkz;
    private String dnBase;
    private String start;
    private String end;
    private boolean zentrale;

    @Override
    public OrderTechnicalParams.DialNumber.DialNumberPlan build() {
        final OrderTechnicalParams.DialNumber.DialNumberPlan rufnummernplan =
                new OrderTechnicalParams.DialNumber.DialNumberPlan();

        rufnummernplan.setNumber(this.dnBase);
        rufnummernplan.setAreaDialingCode(this.onkz);
        final OrderTechnicalParams.DialNumber.DialNumberPlan.NumberRange numberRange =
                new OrderTechnicalParams.DialNumber.DialNumberPlan.NumberRange();

        numberRange.setFrom(this.start);
        numberRange.setTo(this.end);
        numberRange.setCentral(String.valueOf(this.zentrale));

        rufnummernplan.setNumberRange(numberRange);
        return rufnummernplan;
    }

    public DialNumberPlanBuilder withOnkz(final String onkz)    {
        this.onkz = onkz;
        return this;
    }

    public DialNumberPlanBuilder withDnBase(final String dnBase)    {
        this.dnBase = dnBase;
        return this;
    }

    public DialNumberPlanBuilder withStart(final String start)    {
        this.start = start;
        return this;
    }

    public DialNumberPlanBuilder withEnd(final String end)    {
        this.end = end;
        return this;
    }

    public DialNumberPlanBuilder withZentrale(final boolean zentrale)    {
        this.zentrale = zentrale;
        return this;
    }
}

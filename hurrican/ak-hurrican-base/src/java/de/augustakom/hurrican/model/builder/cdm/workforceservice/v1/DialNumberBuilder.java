/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;


import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class DialNumberBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.DialNumber> {

    protected String areaDialingCode;
    protected String number;
    protected OrderTechnicalParams.DialNumber.NumberRange numberRange;
    protected Boolean main;
    protected String portMode;
    protected LocalDate validFrom;
    protected LocalDate validTo;
    protected OrderTechnicalParams.DialNumber.VoIPLogin voipLogin;
    protected List<OrderTechnicalParams.DialNumber.DialNumberPlan> dialNumberPlans = Lists.newArrayList();

    @Override
    public OrderTechnicalParams.DialNumber build() {
        OrderTechnicalParams.DialNumber dialNumber = new OrderTechnicalParams.DialNumber();
        dialNumber.setAreaDialingCode(this.areaDialingCode);
        dialNumber.setNumber(this.number);
        dialNumber.setNumberRange(this.numberRange);
        dialNumber.setMain(this.main);
        dialNumber.setPortMode(this.portMode);
        dialNumber.setValidFrom(this.validFrom);
        dialNumber.setValidTo(this.validTo);
        dialNumber.setVoIPLogin(this.voipLogin);
        dialNumber.getDialNumberPlan().addAll(this.dialNumberPlans);
        return dialNumber;
    }

    public DialNumberBuilder withAreaDialingCode(String areaDialingCode) {
        this.areaDialingCode = areaDialingCode;
        return this;
    }

    public DialNumberBuilder withNumber(String number) {
        this.number = number;
        return this;
    }

    public DialNumberBuilder withNumberRange(OrderTechnicalParams.DialNumber.NumberRange numberRange) {
        this.numberRange = numberRange;
        return this;
    }

    public DialNumberBuilder withMain(Boolean main) {
        this.main = main;
        return this;
    }

    public DialNumberBuilder withPortMode(String portMode) {
        this.portMode = portMode;
        return this;
    }

    public DialNumberBuilder withValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    public DialNumberBuilder withValidTo(LocalDate validTo) {
        this.validTo = validTo;
        return this;
    }

    public DialNumberBuilder withVoIPLogin(final OrderTechnicalParams.DialNumber.VoIPLogin voipLogin)   {
        this.voipLogin = voipLogin;
        return this;
    }

    public DialNumberBuilder addRufnummerplans(final Collection<OrderTechnicalParams.DialNumber.DialNumberPlan> dialNumberPlans)  {
        this.dialNumberPlans.addAll(dialNumberPlans);
        return this;
    }

}

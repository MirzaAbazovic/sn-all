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
public class PortingBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Common.Porting> {

    private String portingDate;
    private String window;
    private String lastCarrier;

    @Override
    public OrderTechnicalParams.Common.Porting build() {
        OrderTechnicalParams.Common.Porting porting = new OrderTechnicalParams.Common.Porting();
        porting.setPortingDate(this.portingDate);
        porting.setWindow(this.window);
        porting.setLastCarrier(this.lastCarrier);
        return porting;
    }

    public PortingBuilder withPortingDate(String portingDate) {
        this.portingDate = portingDate;
        return this;
    }

    public PortingBuilder withWindow(String window) {
        this.window = window;
        return this;
    }

    public PortingBuilder withLastCarrier(String lastCarrier) {
        this.lastCarrier = lastCarrier;
        return this;
    }
}
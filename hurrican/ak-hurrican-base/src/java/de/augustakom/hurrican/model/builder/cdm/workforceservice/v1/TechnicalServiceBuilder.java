/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 * Created by GlinkJo on 05.02.2015.
 */
public class TechnicalServiceBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.TechnicalService> {

    private String type;
    private String name;

    @Override
    public OrderTechnicalParams.TechnicalService build() {
        OrderTechnicalParams.TechnicalService technischeLeistung = new OrderTechnicalParams.TechnicalService();
        technischeLeistung.setTsType(this.type);
        technischeLeistung.setTsName(this.name);
        return technischeLeistung;
    }

    public TechnicalServiceBuilder withType(String typ) {
        this.type = typ;
        return this;
    }

    public TechnicalServiceBuilder withName(String tsName) {
        this.name = tsName;
        return this;
    }

}

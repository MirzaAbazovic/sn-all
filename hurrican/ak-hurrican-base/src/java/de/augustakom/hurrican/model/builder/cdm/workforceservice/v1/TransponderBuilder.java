/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2015
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 * Created by glinkjo on 06.02.2015.
 */
public class TransponderBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Housing.Transponder> {

    private Long id;
    private String firstName;
    private String lastName;
    private String group;

    @Override
    public OrderTechnicalParams.Housing.Transponder build() {
        OrderTechnicalParams.Housing.Transponder transponder = new OrderTechnicalParams.Housing.Transponder();
        transponder.setId(id != null ? id.toString() : null);
        transponder.setGroup(group);
        transponder.setFirstName(firstName);
        transponder.setLastName(lastName);
        return transponder;
    }

    public TransponderBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TransponderBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public TransponderBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public TransponderBuilder withGroup(String group) {
        this.group = group;
        return this;
    }

}

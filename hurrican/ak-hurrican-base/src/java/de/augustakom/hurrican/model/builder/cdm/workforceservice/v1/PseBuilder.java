/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 29.11.2016

 */

package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import java.util.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**

 */
public class PseBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.PSE> {

    private String manufacturer;
    private String model;
    private String version;


    @Override
    public OrderTechnicalParams.PSE build() {
        final OrderTechnicalParams.PSE pse = createPse();
        validateAllFieldsAreSet(pse);
        return pse;
    }

    private OrderTechnicalParams.PSE createPse() {
        final OrderTechnicalParams.PSE pse = new OrderTechnicalParams.PSE();
        pse.setManufacturer(this.manufacturer);
        pse.setModel(this.model);
        pse.setVersion(this.version);
        return pse;
    }

    private void validateAllFieldsAreSet(OrderTechnicalParams.PSE pse) {
        final List<String> validationErrors = Lists.newLinkedList();
        if (StringUtils.isBlank(pse.getManufacturer())) {
            validationErrors.add("Hersteller der PSE ist nicht gesetzt");
        }
        if (StringUtils.isBlank(pse.getModel())) {
            validationErrors.add("Modell der PSE ist nicht gesetzt");
        }
        if (StringUtils.isBlank(pse.getVersion())) {
            validationErrors.add("Version der PSE ist nicht gesetzt");
        }
        final Optional<String> validationMsg = validationErrors.stream().reduce((s1, s2) -> s1.join("\n", s2));
        validationMsg.ifPresent((msg) -> {
            throw new IllegalStateException(msg + ToStringBuilder.reflectionToString(pse));
        });
    }

    public PseBuilder withManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public PseBuilder withModel(final String model) {
        this.model = model;
        return this;
    }

    public PseBuilder withVersion(final String version) {
        this.version = version;
        return this;
    }

}

/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2011 14:53:05
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.common.service.iface.IServiceObject;

/**
 * Builder fuer {@link EndgeraetPort}.
 *
 *
 * @since Release 10
 */
public class EndgeraetPortBuilder extends AbstractCCIDModelBuilder<EndgeraetPortBuilder, EndgeraetPort> implements
        IServiceObject {

    @ReferencedEntityId(value = "endgeraetTyp")
    @DontCreateBuilder
    private EGTypeBuilder endgeraetTypBuilder;
    private Integer number;
    private String name;

    public EndgeraetPortBuilder withNumber(Integer number) {
        this.number = number;
        return this;
    }

    public EndgeraetPortBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EndgeraetPortBuilder withEndgeraetTypeBuilder(EGTypeBuilder builder) {
        this.endgeraetTypBuilder = builder;
        return this;
    }

} // end

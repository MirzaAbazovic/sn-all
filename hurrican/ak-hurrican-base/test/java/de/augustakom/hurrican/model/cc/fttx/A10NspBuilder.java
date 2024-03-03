/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 09:29:10
 */
package de.augustakom.hurrican.model.cc.fttx;

import de.augustakom.hurrican.model.cc.AbstractCCIDModelBuilder;

/**
 *
 */
public class A10NspBuilder extends AbstractCCIDModelBuilder<A10NspBuilder, A10Nsp> {
    protected Integer nummer = randomInt(1, Integer.MAX_VALUE);
    protected String name = randomString(10);
    ;

    public A10NspBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public A10NspBuilder withNummer(Integer nummer) {
        this.nummer = nummer;
        return this;
    }
}



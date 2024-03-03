/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import de.augustakom.common.model.EntityBuilder;

public class IaLevel5Builder extends EntityBuilder<IaLevel5Builder, IaLevel5> {

    private String name = randomString(50);
    private String sapId = randomString(50);

    public IaLevel5Builder withName(final String name) {
        this.name = name;
        return this;
    }

}

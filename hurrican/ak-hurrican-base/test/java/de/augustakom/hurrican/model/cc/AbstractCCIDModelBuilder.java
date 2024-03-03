/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2009 15:53:42
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;

public abstract class AbstractCCIDModelBuilder<BUILDER extends AbstractCCIDModelBuilder<BUILDER, ENTITY>, ENTITY extends AbstractCCIDModel>
        extends EntityBuilder<BUILDER, ENTITY> {

    protected Long id = null;

    public BUILDER withRandomId() {
        id = getLongId();
        @SuppressWarnings("unchecked")
        BUILDER actualClass = (BUILDER) this;
        return actualClass;
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public BUILDER withId(Long id) {
        this.id = id;
        return (BUILDER) this;
    }

}

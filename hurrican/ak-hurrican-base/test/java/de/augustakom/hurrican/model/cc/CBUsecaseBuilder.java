/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.2009 14:37:15
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.cc.tal.CBUsecase;

@SuppressWarnings("unused")
public class CBUsecaseBuilder extends AbstractCCIDModelBuilder<CBUsecaseBuilder, CBUsecase> {

    private ReferenceBuilder referenceBuilder = null;
    private Long exmTbvId = getLongId();
    private Boolean active = true;

    @Override
    protected void initialize() {
        id = getLongId();
    }

    public CBUsecaseBuilder withReferenceBuilder(ReferenceBuilder referenceBuilder) {
        this.referenceBuilder = referenceBuilder;
        return this;
    }

    public CBUsecaseBuilder withExmTbvId(Long exmTbvId) {
        this.exmTbvId = exmTbvId;
        return this;
    }
}

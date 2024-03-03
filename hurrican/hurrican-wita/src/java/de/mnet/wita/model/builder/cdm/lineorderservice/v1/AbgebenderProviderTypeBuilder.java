/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AbgebenderProviderType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BoolDecisionType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AbgebenderProviderTypeBuilder implements LineOrderTypeBuilder<AbgebenderProviderType>{

    private String providername;
    private BoolDecisionType zustimmungProviderwechsel;
    private String antwortcode;
    private String antworttext;

    public AbgebenderProviderType build() {
        return enrich(new AbgebenderProviderType());
    }

    protected <APT extends AbgebenderProviderType> APT enrich(APT abgebenderProviderType) {
        abgebenderProviderType.setAntwortcode(antwortcode);
        abgebenderProviderType.setAntworttext(antworttext);
        abgebenderProviderType.setProvidername(providername);
        abgebenderProviderType.setZustimmungProviderwechsel(zustimmungProviderwechsel);
        return abgebenderProviderType;
    }

    public AbgebenderProviderTypeBuilder withProvidername(String value) {
        this.providername = value;
        return this;
    }

    public AbgebenderProviderTypeBuilder withZustimmungProviderwechsel(BoolDecisionType value) {
        this.zustimmungProviderwechsel = value;
        return this;
    }

    public AbgebenderProviderTypeBuilder withAntwortcode(String value) {
        this.antwortcode = value;
        return this;
    }

    public AbgebenderProviderTypeBuilder withAntworttext(String value) {
        this.antworttext = value;
        return this;
    }

}

/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BoolDecisionType;

/**
 *
 */
public class AbgebenderProviderTypeTestBuilder extends AbgebenderProviderTypeBuilder {

    public AbgebenderProviderTypeTestBuilder() {
        withProvidername("provider");
        withZustimmungProviderwechsel(BoolDecisionType.J);
        withAntwortcode("123");
        withAntworttext("Antworttext");
    }
}

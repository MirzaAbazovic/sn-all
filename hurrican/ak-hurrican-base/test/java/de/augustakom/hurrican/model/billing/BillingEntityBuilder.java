/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2009 13:30:02
 */
package de.augustakom.hurrican.model.billing;

import de.augustakom.common.model.EntityBuilder;


/**
 * Abstrakte Builder Klasse fuer Billing-Modelle. Stellt sicher, dass die Objekte nicht persistiert werden!
 *
 *
 */
public abstract class BillingEntityBuilder
        <BUILDER extends EntityBuilder<BUILDER, ENTITY>,
                ENTITY> extends EntityBuilder<BUILDER, ENTITY> {

    public BillingEntityBuilder() {
        setPersist(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BUILDER setPersist(boolean persist) {
        if (persist) {
            throw new UnsupportedOperationException("Persisting in TAIFUN Database not supported");
        }
        super.setPersist(false);
        return (BUILDER) this;
    }


}



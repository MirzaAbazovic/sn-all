/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2012 10:53:57
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.query;

import de.augustakom.common.model.EntityBuilder;


@SuppressWarnings("unused")
public class CPSQueryEntryDataBuilder extends EntityBuilder<CPSQueryEntryDataBuilder, CPSQueryEntryData> {

    private String maxAttBrDn;
    private String maxAttBrUp;

    public CPSQueryEntryDataBuilder() {
        setPersist(false);
    }

    public CPSQueryEntryDataBuilder withMaxAttBrDn(String maxAttBrDn) {
        this.maxAttBrDn = maxAttBrDn;
        return this;
    }

    public CPSQueryEntryDataBuilder withMaxAttBrUp(String maxAttBrUp) {
        this.maxAttBrUp = maxAttBrUp;
        return this;
    }

    @Override
    public CPSQueryEntryDataBuilder setPersist(boolean persist) {
        if (persist) {
            throw new UnsupportedOperationException("Persisting a CPS query result is not supported!");
        }
        super.setPersist(false);
        return this;
    }

}



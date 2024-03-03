/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;

/**
 *
 */
public abstract class AbstractRufnummernblockTypeBuilder<TBT extends RufnummernblockType> extends
        V1AbstractBasicBuilder<TBT> {

    public AbstractRufnummernblockTypeBuilder<TBT> withRnrBlockVon(String rnrBlockVon) {
        objectType.setRnrBlockVon(rnrBlockVon);
        return this;
    }

    public AbstractRufnummernblockTypeBuilder<TBT> withRnrBlockBis(String rnrBlockBis) {
        objectType.setRnrBlockBis(rnrBlockBis);
        return this;
    }

}

/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernblockType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class RufnummernblockTypeBuilder implements LineOrderNotificationTypeBuilder<RufnummernblockType> {

    private String rnrBlockVon;
    private String rnrBlockBis;
    
    @Override
    public RufnummernblockType build() {
        RufnummernblockType rufnummernblockType = new RufnummernblockType();
        rufnummernblockType.setRnrBlockBis(rnrBlockBis);
        rufnummernblockType.setRnrBlockVon(rnrBlockVon);
        return rufnummernblockType;
    }

    public RufnummernblockTypeBuilder withRnrBlockVon(String rnrBlockVon) {
        this.rnrBlockVon = rnrBlockVon;
        return this;
    }

    public RufnummernblockTypeBuilder withRnrBlockBis(String rnrBlockBis) {
        this.rnrBlockBis = rnrBlockBis;
        return this;
    }

}

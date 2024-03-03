/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockPortierungskennerType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.builder.RufnummernblockBuilder;

/**
 *
 */
@Component
public class RufnummernblockUnmarshaller implements Function<RufnummernblockType, Rufnummernblock> {

    @Nullable
    @Override
    public Rufnummernblock apply(@Nullable RufnummernblockType input) {
        if (input != null) {
            Rufnummernblock output = new RufnummernblockBuilder()
                    .withRnrBlockBis(input.getRnrBlockBis())
                    .withRnrBlockVon(input.getRnrBlockVon())
                    .build();
            if (input instanceof RufnummernblockPortierungskennerType) {
                output.setPortierungskennungPKIabg(((RufnummernblockPortierungskennerType) input)
                        .getPortierungskennungPKIabg());
            }
            return output;
        }
        return null;
    }

}

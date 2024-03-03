/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RufnummernblockTypeBuilder;

/**
 *
 */
@Component
public class RufnummernblockMarshaller extends AbstractBaseMarshaller implements Function<Rufnummernblock, RufnummernblockType> {
    @Nullable
    @Override
    public RufnummernblockType apply(@Nullable Rufnummernblock input) {
        if (input != null) {
            return new RufnummernblockTypeBuilder()
                    .withRnrBlockVon(input.getRnrBlockVon())
                    .withRnrBlockBis(input.getRnrBlockBis())
                    .build();
        }

        return null;
    }
}

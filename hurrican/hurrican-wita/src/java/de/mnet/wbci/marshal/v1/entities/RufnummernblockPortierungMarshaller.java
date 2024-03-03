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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockPortierungskennerType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RufnummernblockPortierungskennerTypeBuilder;

/**
 *
 */
@Component
public class RufnummernblockPortierungMarshaller extends AbstractBaseMarshaller implements Function<Rufnummernblock, RufnummernblockPortierungskennerType> {
    @Nullable
    @Override
    public RufnummernblockPortierungskennerType apply(@Nullable Rufnummernblock input) {
        if (input != null) {
            return new RufnummernblockPortierungskennerTypeBuilder()
                    .withPortierungskennungPKIabg(input.getPortierungskennungPKIabg())
                    .withRnrBlockVon(input.getRnrBlockVon())
                    .withRnrBlockBis(input.getRnrBlockBis())
                    .build();
        }

        return null;
    }
}

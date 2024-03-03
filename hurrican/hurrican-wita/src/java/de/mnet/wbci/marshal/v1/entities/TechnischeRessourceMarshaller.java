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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.TechnischeRessourceTypeBuilder;

/**
 *
 */
@Component
public class TechnischeRessourceMarshaller extends AbstractBaseMarshaller implements Function<TechnischeRessource, TechnischeRessourceType> {
    @Nullable
    @Override
    public TechnischeRessourceType apply(@Nullable TechnischeRessource input) {
        if (input != null) {
            return new TechnischeRessourceTypeBuilder()
                    .withLineID(input.getLineId())
                    .withIdentifizierer(input.getIdentifizierer())
                    .withKennungTNBabg(input.getTnbKennungAbg())
                    .withVertragsnummer(input.getVertragsnummer()).build();
        }

        return null;
    }
}

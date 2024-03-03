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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.builder.TechnischeRessourceBuilder;

/**
 *
 */
@Component
public class TechnischeRessourceUnmarshaller implements Function<TechnischeRessourceType, TechnischeRessource> {
    @Nullable
    @Override
    public TechnischeRessource apply(@Nullable TechnischeRessourceType input) {
        if (input != null) {
            return new TechnischeRessourceBuilder()
                    .withIdentifizierer(input.getIdentifizierer())
                    .withLineId(input.getLineID())
                    .withTnbKennungAbg(input.getKennungTNBabg())
                    .withVertragsnummer(input.getVertragsnummer())
                    .build();
        }
        return null;
    }
}

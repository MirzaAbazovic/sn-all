/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UebernahmeLeitungType;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.builder.LeitungBuilder;

/**
 *
 */
@Component
public class LeitungUnmarshaller implements Function<UebernahmeLeitungType, Leitung> {

    @Nullable
    @Override
    public Leitung apply(@Nullable UebernahmeLeitungType input) {
        if (input != null) {
            return new LeitungBuilder()
                    .withLineId(input.getLineID())
                    .withVertragsnummer(input.getVertragsnummer())
                    .build();
        }
        return null;
    }

}

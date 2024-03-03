/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ProjektIDType;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.model.builder.ProjektBuilder;

@Component
public class ProjektUnmarshaller implements Function<ProjektIDType, Projekt> {

    @Nullable
    @Override
    public Projekt apply(@Nullable ProjektIDType input) {
        if (input == null) {
            return null;
        }

        Projekt projekt = new ProjektBuilder()
                .withProjektKenner(input.getProjektkenner())
                .withKopplungsKenner(input.getKopplungskenner())
                .build();
        return projekt;
    }

}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ProjektIDType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Projekt;

/**
 *
 */
@Component
public class ProjektMarshaller extends AbstractBaseMarshaller implements Function<Projekt, ProjektIDType> {

    @Nullable
    @Override
    public ProjektIDType apply(@Nullable Projekt input) {
        if (input == null) {
            return null;
        }
        ProjektIDType projektId = V1_OBJECT_FACTORY.createProjektIDType();
        projektId.setKopplungskenner(input.getKopplungsKenner());
        projektId.setProjektkenner(input.getProjektKenner());
        return projektId;
    }
}

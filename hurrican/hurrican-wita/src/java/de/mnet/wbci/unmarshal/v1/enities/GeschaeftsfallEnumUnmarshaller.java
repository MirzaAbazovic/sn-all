/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.model.GeschaeftsfallTyp;

/**
 *
 */
@Component
public class GeschaeftsfallEnumUnmarshaller implements Function<GeschaeftsfallEnumType, GeschaeftsfallTyp> {
    @Nullable
    @Override
    public GeschaeftsfallTyp apply(@Nullable GeschaeftsfallEnumType input) {
        if (input != null) {
            return GeschaeftsfallTyp.buildFromName(input.name());
        }
        return null;
    }
}

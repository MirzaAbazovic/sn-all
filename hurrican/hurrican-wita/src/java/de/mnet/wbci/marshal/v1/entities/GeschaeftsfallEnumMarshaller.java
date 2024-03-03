/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.model.GeschaeftsfallTyp;

/**
 *
 */
@Component
public class GeschaeftsfallEnumMarshaller implements Function<GeschaeftsfallTyp, GeschaeftsfallEnumType> {
    @Nullable
    @Override
    public GeschaeftsfallEnumType apply(@Nullable GeschaeftsfallTyp gfTyp) {
        switch (gfTyp) {
            case VA_KUE_MRN:
                return GeschaeftsfallEnumType.VA_KUE_MRN;
            case VA_KUE_ORN:
                return GeschaeftsfallEnumType.VA_KUE_ORN;
            case VA_RRNP:
                return GeschaeftsfallEnumType.VA_RRNP;
            default:
                return null;
        }
    }
}

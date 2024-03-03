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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;
import de.mnet.wbci.model.Rufnummernportierung;

/**
 *
 */
@Component
public class RufnummernportierungMarshaller extends
        RufnummernportierungBaseMarshaller<RufnummernportierungType> implements
        Function<Rufnummernportierung, RufnummernportierungType> {

    @Nullable
    @Override
    public RufnummernportierungType apply(@Nullable Rufnummernportierung input) {
        if (input == null) {
            return null;
        }

        RufnummernportierungType rufnummernportierungType = V1_OBJECT_FACTORY.createRufnummernportierungType();
        super.apply(rufnummernportierungType, input);

        return rufnummernportierungType;
    }
}

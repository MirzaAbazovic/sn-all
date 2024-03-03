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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMitPortierungskennungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;

@Component
public class RufnummernportierungMitPortierungskennungUnmarshaller implements Function<RufnummernportierungType, String> {

    @Nullable
    @Override
    public String apply(@Nullable RufnummernportierungType input) {
        if (input instanceof RufnummernportierungMitPortierungskennungType) {
            RufnummernportierungMitPortierungskennungType pkType = (RufnummernportierungMitPortierungskennungType) input;
            return pkType.getPortierungskennungPKIauf();
        }

        return null;
    }

}

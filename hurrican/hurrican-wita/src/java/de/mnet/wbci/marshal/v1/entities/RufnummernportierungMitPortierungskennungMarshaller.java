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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMitPortierungskennungType;
import de.mnet.wbci.model.Rufnummernportierung;

/**
 *
 */
@Component
public class RufnummernportierungMitPortierungskennungMarshaller extends
        RufnummernportierungBaseMarshaller<RufnummernportierungMitPortierungskennungType> implements
        Function<Rufnummernportierung, RufnummernportierungMitPortierungskennungType> {

    /**
     * marshalls the {@link de.mnet.wbci.model.Rufnummernportierung} object to the {@link
     * RufnummernportierungMitPortierungskennungType}.
     */
    @Nullable
    @Override
    public RufnummernportierungMitPortierungskennungType apply(@Nullable Rufnummernportierung input) {
        if (input == null) {
            return null;
        }

        RufnummernportierungMitPortierungskennungType rufnummernportierungType = V1_OBJECT_FACTORY.createRufnummernportierungMitPortierungskennungType();
        rufnummernportierungType.setPortierungskennungPKIauf(input.getPortierungskennungPKIauf());

        super.apply(rufnummernportierungType, input);

        return rufnummernportierungType;
    }

}

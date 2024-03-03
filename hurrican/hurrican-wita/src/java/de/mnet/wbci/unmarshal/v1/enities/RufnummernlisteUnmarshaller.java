/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernListeType;
import de.mnet.wbci.model.RufnummerOnkz;

@Component
public class RufnummernlisteUnmarshaller implements Function<RufnummernListeType, List<RufnummerOnkz>> {

    @Autowired
    private OnkzRufNrUnmarshaller onkzRufNrUnmarshaller;

    @Nullable
    @Override
    public List<RufnummerOnkz> apply(@Nullable RufnummernListeType input) {
        if (input == null || input.getZuPortierendeOnkzRnr() == null) {
            return null;
        }

        List<RufnummerOnkz> result = new ArrayList<>(input.getZuPortierendeOnkzRnr().size());
        for (OnkzRufNrType type : input.getZuPortierendeOnkzRnr()) {
            result.add(onkzRufNrUnmarshaller.apply(type));
        }

        return result;
    }

}

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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.model.Rufnummernblock;

@Component
public class RufnummernbloeckeUnmarshaller implements Function<List<RufnummernblockType>, List<Rufnummernblock>> {

    @Autowired
    private RufnummernblockUnmarshaller rufnummernblockUnmarshaller;

    @Nullable
    @Override
    public List<Rufnummernblock> apply(@Nullable List<RufnummernblockType> input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        List<Rufnummernblock> result = new ArrayList<>();
        for (RufnummernblockType type : input) {
            result.add(rufnummernblockUnmarshaller.apply(type));
        }

        return result;
    }

}

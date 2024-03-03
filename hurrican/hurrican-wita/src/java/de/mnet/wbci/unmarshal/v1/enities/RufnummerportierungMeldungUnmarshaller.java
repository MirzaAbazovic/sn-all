/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;
import de.mnet.wbci.model.Rufnummernportierung;

/**
 *
 */
@Component
public class RufnummerportierungMeldungUnmarshaller implements Function<RufnummernportierungMeldungType, Rufnummernportierung> {

    @Autowired
    private PortierungRufnummernEinzelnMeldungUnmarshaller portierungRufnummernMeldungUnmarshaller;
    @Autowired
    private PortierungRufnummernBloeckeMeldungUnmarshaller portierungRufnummernBloeckeUnmarshaller;

    @Nullable
    @Override
    public Rufnummernportierung apply(@Nullable RufnummernportierungMeldungType input) {
        if (input != null) {
            assert !(input.getPortierungRufnummernbloecke() != null && input.getPortierungRufnummern() != null) : "Only one of the fields 'porierungRufnummern' or 'portierungRufnummernblocke' can be set!";
            if (input.getPortierungRufnummern() != null) {
                return portierungRufnummernMeldungUnmarshaller.apply(input.getPortierungRufnummern());
            }
            else if (input.getPortierungRufnummernbloecke() != null) {
                return portierungRufnummernBloeckeUnmarshaller.apply(input.getPortierungRufnummernbloecke());
            }
            else {
                return null;
            }
        }
        return null;
    }
}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockPortierungskennerType;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;

/**
 *
 */
@Component
public class PortierungRufnummernBloeckeMeldungUnmarshaller implements
        Function<PortierungRufnummernbloeckeMeldungType, RufnummernportierungAnlage> {

    @Autowired
    private RufnummernblockUnmarshaller rufnummernblockUnmarshaller;

    @Nullable
    @Override
    public RufnummernportierungAnlage apply(@Nullable PortierungRufnummernbloeckeMeldungType input) {
        if (input != null) {
            RufnummernportierungAnlage output = new RufnummernportierungAnlageBuilder().build();
            if (input.getOnkzDurchwahlAbfragestelle() != null) {
                output.setAbfragestelle(input.getOnkzDurchwahlAbfragestelle().getAbfragestelle());
                output.setOnkz(input.getOnkzDurchwahlAbfragestelle().getONKZ());
                output.setDurchwahlnummer(input.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer());
            }
            if (input.getZuPortierenderRufnummernblock() != null && !input.getZuPortierenderRufnummernblock().isEmpty()) {
                for (RufnummernblockPortierungskennerType rnblockPortierungskennerType :
                        input.getZuPortierenderRufnummernblock()) {
                    output.addRufnummernblock(rufnummernblockUnmarshaller.apply(rnblockPortierungskennerType));
                }
            }
            return output;
        }
        return null;
    }
}

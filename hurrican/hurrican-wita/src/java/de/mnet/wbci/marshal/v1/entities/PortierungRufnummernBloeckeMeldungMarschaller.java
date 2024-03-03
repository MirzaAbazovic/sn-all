/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.OnkzDurchwahlAbfragestelleTypeBuilder;

/**
 *
 */
@Component
public class PortierungRufnummernBloeckeMeldungMarschaller extends AbstractBaseMarshaller
        implements Function<RufnummernportierungAnlage, PortierungRufnummernbloeckeMeldungType> {

    @Autowired
    private RufnummernblockPortierungMarshaller rufnummernblockPortierungMarshaller;

    @Nullable
    @Override
    public PortierungRufnummernbloeckeMeldungType apply(@Nullable RufnummernportierungAnlage input) {
        if (input == null) {
            return null;
        }

        PortierungRufnummernbloeckeMeldungType rufnummernbloeckeMeldungType = V1_OBJECT_FACTORY.createPortierungRufnummernbloeckeMeldungType();

        rufnummernbloeckeMeldungType.setOnkzDurchwahlAbfragestelle(new OnkzDurchwahlAbfragestelleTypeBuilder()
                .withAbfragestelle(input.getAbfragestelle())
                .withDurchwahlnummer(input.getDurchwahlnummer())
                .withOnkz(input.getOnkz()).build());

        for (Rufnummernblock rufnummernblock : input.getRufnummernbloecke()) {
            rufnummernbloeckeMeldungType.getZuPortierenderRufnummernblock().add(rufnummernblockPortierungMarshaller.apply(rufnummernblock));
        }

        return rufnummernbloeckeMeldungType;
    }
}

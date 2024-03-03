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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;

/**
 *
 */
@Component
public class RufnummernportierungMeldungMarshaller extends AbstractBaseMarshaller implements
        Function<Rufnummernportierung, RufnummernportierungMeldungType> {

    @Autowired
    private PortierungRufnummernEinzelnMeldungMarshaller einzelnMeldungMarshaller;

    @Autowired
    private PortierungRufnummernBloeckeMeldungMarschaller bloeckeMeldungMarshaller;

    @Nullable
    @Override
    public RufnummernportierungMeldungType apply(@Nullable Rufnummernportierung input) {
        if (input == null) {
            return null;
        }

        RufnummernportierungMeldungType rufnummernportierungMeldungType = V1_OBJECT_FACTORY.createRufnummernportierungMeldungType();

        if (input instanceof RufnummernportierungEinzeln) {
            rufnummernportierungMeldungType.setPortierungRufnummern(einzelnMeldungMarshaller.apply((RufnummernportierungEinzeln) input));
        }
        else if (input instanceof RufnummernportierungAnlage) {
            rufnummernportierungMeldungType.setPortierungRufnummernbloecke(bloeckeMeldungMarshaller.apply((RufnummernportierungAnlage) input));
        }

        return rufnummernportierungMeldungType;
    }
}

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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernMeldungType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.RufnummernportierungEinzeln;

/**
 *
 */
@Component
public class PortierungRufnummernEinzelnMeldungMarshaller extends AbstractBaseMarshaller
        implements Function<RufnummernportierungEinzeln, PortierungRufnummernMeldungType> {

    @Autowired
    private RufnummerOnkzPortierungMarshaller rufnummerOnkzPortierungMarshaller;

    @Nullable
    @Override
    public PortierungRufnummernMeldungType apply(@Nullable RufnummernportierungEinzeln input) {
        if (input == null) {
            return null;
        }

        PortierungRufnummernMeldungType portierungRufnummernMeldungType = V1_OBJECT_FACTORY.createPortierungRufnummernMeldungType();

        for (RufnummerOnkz rufnummerOnkz : input.getRufnummernOnkz()) {
            portierungRufnummernMeldungType.getZuPortierendeOnkzRnr().add(rufnummerOnkzPortierungMarshaller.apply(rufnummerOnkz));
        }

        return portierungRufnummernMeldungType;
    }
}

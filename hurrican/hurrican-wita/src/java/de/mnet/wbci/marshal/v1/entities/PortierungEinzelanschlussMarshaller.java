/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.common.tools.lang.BooleanTools;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernListeType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.RufnummernportierungEinzeln;

/**
 *
 */
@Component
public class PortierungEinzelanschlussMarshaller extends AbstractBaseMarshaller implements
        Function<RufnummernportierungEinzeln, PortierungEinzelanschlussType> {

    @Autowired
    private RufnummerOnkzMarshaller rufnummerOnkzMarshaller;

    @Nullable
    @Override
    public PortierungEinzelanschlussType apply(@Nullable RufnummernportierungEinzeln input) {
        if (input == null) {
            return null;
        }

        PortierungEinzelanschlussType portierungEinzelanschlussType = V1_OBJECT_FACTORY
                .createPortierungEinzelanschlussType();

        // if isAlleRufnummernPortieren() == false, then leave null
        // the xsd accepts only 'true' or null as valid values
        if (BooleanTools.nullToFalse(input.getAlleRufnummernPortieren())) {
            portierungEinzelanschlussType.setAlleRufnummern(Boolean.TRUE);
        }

        if (input.getRufnummernOnkz() != null) {
            RufnummernListeType rufnummernListeType = V1_OBJECT_FACTORY.createRufnummernListeType();
            for (RufnummerOnkz rufnummerOnkz : input.getRufnummernOnkz()) {
                rufnummernListeType.getZuPortierendeOnkzRnr().add(rufnummerOnkzMarshaller.apply(rufnummerOnkz));
            }
            portierungEinzelanschlussType.setRufnummernliste(rufnummernListeType);
        }
        return portierungEinzelanschlussType;
    }
}

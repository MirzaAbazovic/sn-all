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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StrasseType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Standort;

/**
 *
 */
@Component
public class StandortMarshaller extends AbstractBaseMarshaller implements Function<Standort, StandortType> {

    @Nullable
    @Override
    public StandortType apply(@Nullable Standort input) {
        if (input == null) {
            return null;
        }

        StandortType standortType = V1_OBJECT_FACTORY.createStandortType();
        standortType.setOrt(input.getOrt());
        standortType.setPostleitzahl(input.getPostleitzahl());

        if (input.getStrasse() != null) {
            StrasseType strasseType = V1_OBJECT_FACTORY.createStrasseType();
            strasseType.setStrassenname(input.getStrasse().getStrassenname());
            strasseType.setHausnummer(input.getStrasse().getHausnummer());
            strasseType.setHausnummernZusatz(input.getStrasse().getHausnummernZusatz());
            standortType.setStrasse(strasseType);
        }
        return standortType;
    }
}

/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.StornierungType;
import de.mnet.wita.message.Storno;

@Component
public class StornoMarshallerV2 extends RequestMarshallerV2<Storno, StornierungType> {

    @Override
    StornierungType createAuftragType(Storno input) {
        return new StornierungType();
    }
}

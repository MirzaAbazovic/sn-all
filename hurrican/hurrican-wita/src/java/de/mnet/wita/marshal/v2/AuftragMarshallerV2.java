/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragType;
import de.mnet.wita.message.Auftrag;

@Component
public class AuftragMarshallerV2 extends RequestMarshallerV2<Auftrag, AuftragType> {

    @Override
    AuftragType createAuftragType(Auftrag input) {
        return new AuftragType();
    }

}

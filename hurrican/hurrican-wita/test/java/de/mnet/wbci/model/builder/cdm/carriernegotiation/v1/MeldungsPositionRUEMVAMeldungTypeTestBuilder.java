/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import org.apache.commons.lang.StringUtils;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionRUEMVAMeldungTypeTestBuilder extends MeldungsPositionRUEMVATypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungsPositionRUEMVAType> {
    @Override
    public MeldungsPositionRUEMVAType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        if (StringUtils.isEmpty(objectType.getMeldungscode())) {
            objectType.setMeldungscode("8001");
        }
        if (StringUtils.isEmpty(objectType.getMeldungstext())) {
            objectType.setMeldungstext("Auftragsbestätigung (ZWA)");
        }
        if (objectType.getAdresseAbweichend() == null) {
            objectType.setAdresseAbweichend(new AdresseAbweichendTypeTestBuilder().buildValid(
                    geschaeftsfallEnumType));
        }
        return build();
    }
}

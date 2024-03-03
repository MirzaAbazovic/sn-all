/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;

/**
 *
 */
public class MeldungsPositionRUEMVATypeBuilder extends AbstractMeldungsPositionTypeBuilder<MeldungsPositionRUEMVAType> {

    public MeldungsPositionRUEMVATypeBuilder() {
        objectType = OBJECT_FACTORY.createMeldungsPositionRUEMVAType();
    }

    public MeldungsPositionRUEMVATypeBuilder withAdresseAbweichend(AdresseAbweichendType adresseAbweichend) {
        objectType.setAdresseAbweichend(adresseAbweichend);
        return this;
    }

}

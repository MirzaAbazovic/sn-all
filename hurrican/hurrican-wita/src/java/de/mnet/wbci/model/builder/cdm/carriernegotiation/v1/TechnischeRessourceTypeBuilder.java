/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;

/**
 *
 */
public class TechnischeRessourceTypeBuilder extends V1AbstractBasicBuilder<TechnischeRessourceType> {

    public TechnischeRessourceTypeBuilder() {
        objectType = OBJECT_FACTORY.createTechnischeRessourceType();
    }

    public TechnischeRessourceTypeBuilder withIdentifizierer(String identifizierer) {
        objectType.setIdentifizierer(identifizierer);
        return this;
    }

    public TechnischeRessourceTypeBuilder withKennungTNBabg(String kennungTNBabg) {
        objectType.setKennungTNBabg(kennungTNBabg);
        return this;
    }

    public TechnischeRessourceTypeBuilder withLineID(String lineID) {
        objectType.setLineID(lineID);
        return this;
    }

    public TechnischeRessourceTypeBuilder withVertragsnummer(String vertragsnummer) {
        objectType.setVertragsnummer(vertragsnummer);
        return this;
    }

}

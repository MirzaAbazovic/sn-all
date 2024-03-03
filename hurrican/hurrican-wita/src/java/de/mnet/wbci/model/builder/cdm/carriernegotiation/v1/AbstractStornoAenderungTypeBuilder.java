/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractStornoAenderungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;

/**
 *
 */
public class AbstractStornoAenderungTypeBuilder<T extends AbstractStornoAenderungType> extends AbstractStornoTypeBuilder<T> {

    public AbstractStornoAenderungTypeBuilder<T> withPersonOrderFirma(PersonOderFirmaType personOderFirma) {
        objectType.setName(personOderFirma);
        return this;
    }

    public AbstractStornoAenderungTypeBuilder<T> withStandort(StandortType standort) {
        objectType.setStandort(standort);
        return this;
    }
}

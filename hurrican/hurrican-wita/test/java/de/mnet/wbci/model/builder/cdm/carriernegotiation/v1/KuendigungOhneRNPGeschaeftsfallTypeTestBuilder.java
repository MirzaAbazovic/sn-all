/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationRequestTypeTestBuilder;

public class KuendigungOhneRNPGeschaeftsfallTypeTestBuilder extends KuendigungOhneRNPGeschaeftsfallTypeBuilder
        implements CarrierNegotiationRequestTypeTestBuilder<KuendigungOhneRNPGeschaeftsfallType> {

    @Override
    public KuendigungOhneRNPGeschaeftsfallType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractVorabstimmungTypeTestBuilder.enrichTestData(objectType, geschaeftsfallEnumType);
        if (objectType.getAnschlussidentifikation() == null) {
            objectType.setAnschlussidentifikation(new OnkzRufNrTypeTestBuilder<>().buildValid(null));
        }

        return objectType;
    }

}

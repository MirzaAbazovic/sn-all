/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationRequestTypeTestBuilder;

public class KuendigungMitRNPGeschaeftsfallTypeTestBuilder extends KuendigungMitRNPGeschaeftsfallTypeBuilder
        implements CarrierNegotiationRequestTypeTestBuilder<KuendigungMitRNPGeschaeftsfallType> {

    @Override
    public KuendigungMitRNPGeschaeftsfallType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractVorabstimmungTypeTestBuilder.enrichTestData(objectType, geschaeftsfallEnumType);
        if (objectType.getRufnummernPortierung() == null) {
            objectType.setRufnummernPortierung(new RufnummernportierungTypeTestBuilder().buildValidEinzel());
        }

        if (objectType.getStandort() == null) {
            objectType.setStandort(new StandortTypeTestBuilder().buildValid());
        }

        return build();
    }
}

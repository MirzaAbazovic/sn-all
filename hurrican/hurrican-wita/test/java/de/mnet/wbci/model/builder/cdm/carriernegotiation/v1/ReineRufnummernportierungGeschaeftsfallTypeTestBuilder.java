/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationRequestTypeTestBuilder;

public class ReineRufnummernportierungGeschaeftsfallTypeTestBuilder extends ReineRufnummernPortierungGeschaeftsfallTypeBuilder
        implements CarrierNegotiationRequestTypeTestBuilder<ReineRufnummernportierungGeschaeftsfallType> {

    @Override
    public ReineRufnummernportierungGeschaeftsfallType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractVorabstimmungTypeTestBuilder.enrichTestData(objectType, geschaeftsfallEnumType);
        if (objectType.getRufnummernPortierung() == null) {
            objectType.setRufnummernPortierung(new RufnummernPortierungMitPortierungskennerTypeTestBuilder().buildValid());
        }

        return objectType;
    }

}

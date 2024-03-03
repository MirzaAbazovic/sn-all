/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMitPortierungskennungType;

public class ReineRufnummernPortierungGeschaeftsfallTypeBuilder extends AbstractVorabstimmungTypeBuilder<ReineRufnummernportierungGeschaeftsfallType> {

    public ReineRufnummernPortierungGeschaeftsfallTypeBuilder() {
        objectType = OBJECT_FACTORY.createReineRufnummernportierungGeschaeftsfallType();
    }

    public ReineRufnummernPortierungGeschaeftsfallTypeBuilder withRufnummernportierungType(RufnummernportierungMitPortierungskennungType rufnummernportierungType) {
        objectType.setRufnummernPortierung(rufnummernportierungType);
        return this;
    }

}

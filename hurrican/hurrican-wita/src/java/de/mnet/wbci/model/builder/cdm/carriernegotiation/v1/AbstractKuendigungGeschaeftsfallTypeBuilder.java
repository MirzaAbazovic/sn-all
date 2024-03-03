/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractKuendigungGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;

public abstract class AbstractKuendigungGeschaeftsfallTypeBuilder<T> extends AbstractVorabstimmungTypeBuilder<T> {

    public AbstractKuendigungGeschaeftsfallTypeBuilder withStandortType(StandortType standortType) {
        ((AbstractKuendigungGeschaeftsfallType) objectType).setStandort(standortType);
        return this;
    }

}

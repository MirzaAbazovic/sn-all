/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AnfrageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;

public abstract class AbstractAnfrageTypeBuilder<T> extends V1AbstractBasicBuilder<T> {

    public AbstractAnfrageTypeBuilder<T> withAbsender(CarrierIdentifikatorType absender) {
        ((AnfrageType) objectType).setAbsender(absender);
        return this;
    }

    public AbstractAnfrageTypeBuilder<T> withEmpfaenger(CarrierIdentifikatorType empfaenger) {
        ((AnfrageType) objectType).setEmpfaenger(empfaenger);
        return this;
    }

    public AbstractAnfrageTypeBuilder<T> withVersion(int version) {
        ((AnfrageType) objectType).setVersion(version);
        return this;
    }

    public AbstractAnfrageTypeBuilder<T> withEnkundenvertragspartner(EKPType ekpType) {
        ((AnfrageType) objectType).setEndkundenvertragspartner(ekpType);
        return this;
    }

}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
public abstract class AbstractMeldungTypeBuilder<M extends AbstractMeldungType> extends V1AbstractBasicBuilder<M> {

    public AbstractMeldungTypeBuilder<M> withEKPTyp(EKPType ekpTyp) {
        objectType.setEndkundenvertragspartner(ekpTyp);
        return this;
    }

    public AbstractMeldungTypeBuilder<M> withGeschaeftsfall(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        objectType.setGeschaeftsfall(geschaeftsfallEnumType);
        return this;
    }

    public AbstractMeldungTypeBuilder<M> withAbsender(CarrierIdentifikatorType absender) {
        objectType.setAbsender(absender);
        return this;
    }

    public AbstractMeldungTypeBuilder<M> withEmpfaenger(CarrierIdentifikatorType empfaenger) {
        objectType.setEmpfaenger(empfaenger);
        return this;
    }

    public AbstractMeldungTypeBuilder<M> withVorabstimmugsIdRef(String vorabstimmugsIdRef) {
        objectType.setVorabstimmungsIdRef(vorabstimmugsIdRef);
        return this;
    }

    public AbstractMeldungTypeBuilder<M> withVersion(WbciVersion version) {
        objectType.setVersion(Integer.parseInt(version.getVersion()));
        return this;
    }
}

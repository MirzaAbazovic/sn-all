/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request;

import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AnfrageType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPGeschaeftsfallMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 *
 */
public abstract class AnfrageMarshaller<GF extends WbciGeschaeftsfall, AT extends AnfrageType> extends
        AbstractBaseMarshaller implements Function<GF, AT> {

    @Autowired
    private EKPGeschaeftsfallMarshaller ekpMarshaller;
    @Autowired
    private CarrierIdentifikatorMarshaller ciMarshaller;

    /**
     * Base marshaller method for {@link AnfrageType} types.
     *
     * @param anfrage any {@link AnfrageType}
     * @param input   any {@link WbciGeschaeftsfall}
     * @return an inherit type of {@link AnfrageType}
     */
    public AT apply(AT anfrage, GF input) {
        anfrage.setEndkundenvertragspartner(ekpMarshaller.apply(input));
        anfrage.setAbsender(ciMarshaller.apply(input.getAbsender()));
        anfrage.setEmpfaenger(ciMarshaller.apply(input.getAbgebenderEKP()));

        return anfrage;
    }

}

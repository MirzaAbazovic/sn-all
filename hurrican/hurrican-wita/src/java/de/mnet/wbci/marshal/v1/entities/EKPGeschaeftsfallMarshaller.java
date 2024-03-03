/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 *
 */
@Component
public class EKPGeschaeftsfallMarshaller extends AbstractBaseMarshaller implements Function<WbciGeschaeftsfall, EKPType> {

    @Autowired
    private CarrierIdentifikatorMarshaller ciMarshaller;

    @Nullable
    @Override
    public EKPType apply(@Nullable WbciGeschaeftsfall wbciGeschaeftsfall) {
        if (wbciGeschaeftsfall == null) {
            return null;
        }
        EKPType endkundenvertragspartner = V1_OBJECT_FACTORY.createEKPType();
        endkundenvertragspartner.setEKPauf(ciMarshaller.apply(wbciGeschaeftsfall.getAufnehmenderEKP()));
        endkundenvertragspartner.setEKPabg(ciMarshaller.apply(wbciGeschaeftsfall.getAbgebenderEKP()));
        return endkundenvertragspartner;
    }
}

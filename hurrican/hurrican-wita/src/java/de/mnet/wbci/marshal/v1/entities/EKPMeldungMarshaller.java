/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.model.Meldung;

/**
 *
 */
@Component
public class EKPMeldungMarshaller extends AbstractBaseMarshaller implements Function<Meldung, EKPType> {

    @Autowired
    private CarrierIdentifikatorMarshaller ciMarshaller;

    @Nullable
    @Override
    public EKPType apply(@Nullable Meldung meldung) {
        EKPType endkundenvertragspartner = V1_OBJECT_FACTORY.createEKPType();
        endkundenvertragspartner.setEKPauf(ciMarshaller.apply(meldung.getWbciGeschaeftsfall().getAufnehmenderEKP()));
        endkundenvertragspartner.setEKPabg(ciMarshaller.apply(meldung.getWbciGeschaeftsfall().getAbgebenderEKP()));
        return endkundenvertragspartner;
    }
}

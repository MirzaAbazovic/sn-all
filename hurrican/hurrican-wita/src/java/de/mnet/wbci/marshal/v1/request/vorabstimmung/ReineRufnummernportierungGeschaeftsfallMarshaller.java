/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request.vorabstimmung;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.wbci.marshal.v1.entities.RufnummernportierungMitPortierungskennungMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;

/**
 *
 */
@Component
public class ReineRufnummernportierungGeschaeftsfallMarshaller extends
        VorabstimmungMarshaller<WbciGeschaeftsfallRrnp, ReineRufnummernportierungGeschaeftsfallType> implements
        Function<WbciGeschaeftsfallRrnp, ReineRufnummernportierungGeschaeftsfallType> {

    @Autowired
    private RufnummernportierungMitPortierungskennungMarshaller rufnummernportierungMitPortierungskennungMarshaller;

    @Nullable
    @Override
    public ReineRufnummernportierungGeschaeftsfallType apply(@Nullable WbciGeschaeftsfallRrnp input) {
        if (input == null) {
            return null;
        }

        ReineRufnummernportierungGeschaeftsfallType anfrageType = V1_OBJECT_FACTORY.createReineRufnummernportierungGeschaeftsfallType();

        anfrageType.setRufnummernPortierung(rufnummernportierungMitPortierungskennungMarshaller.apply(input
                .getRufnummernportierung()));

        super.apply(anfrageType, input);

        return anfrageType;
    }
}

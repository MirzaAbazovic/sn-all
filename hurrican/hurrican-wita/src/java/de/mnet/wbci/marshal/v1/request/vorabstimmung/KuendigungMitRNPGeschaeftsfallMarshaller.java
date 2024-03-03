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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.wbci.marshal.v1.entities.RufnummernportierungMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;

/**
 *
 */
@Component
public class KuendigungMitRNPGeschaeftsfallMarshaller extends
        AbstractKuendigungGeschaeftsfallMarshaller<WbciGeschaeftsfallKueMrn, KuendigungMitRNPGeschaeftsfallType>
        implements Function<WbciGeschaeftsfallKueMrn, KuendigungMitRNPGeschaeftsfallType> {

    @Autowired
    private RufnummernportierungMarshaller rufnummernportierungMarshaller;

    @Nullable
    @Override
    public KuendigungMitRNPGeschaeftsfallType apply(@Nullable WbciGeschaeftsfallKueMrn input) {
        if (input == null) {
            return null;
        }

        KuendigungMitRNPGeschaeftsfallType anfrageType = V1_OBJECT_FACTORY.createKuendigungMitRNPGeschaeftsfallType();
        anfrageType.setRufnummernPortierung(rufnummernportierungMarshaller.apply(input.getRufnummernportierung()));

        super.apply(anfrageType, input);

        return anfrageType;
    }
}

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

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.wbci.marshal.v1.entities.RufnummerOnkzMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;

/**
 *
 */
@Component
public class KuendigungOhneRNPGeschaeftsfallMarshaller extends
        AbstractKuendigungGeschaeftsfallMarshaller<WbciGeschaeftsfallKueOrn, KuendigungOhneRNPGeschaeftsfallType>
        implements Function<WbciGeschaeftsfallKueOrn, KuendigungOhneRNPGeschaeftsfallType> {

    @Autowired
    private RufnummerOnkzMarshaller rufnummerOnkzMarshaller;

    @Nullable
    @Override
    public KuendigungOhneRNPGeschaeftsfallType apply(@Nullable WbciGeschaeftsfallKueOrn input) {
        if (input == null) {
            return null;
        }

        KuendigungOhneRNPGeschaeftsfallType anfrageType = V1_OBJECT_FACTORY.createKuendigungOhneRNPGeschaeftsfallType();
        anfrageType.setAnschlussidentifikation(rufnummerOnkzMarshaller.apply(input.getAnschlussIdentifikation()));

        super.apply(anfrageType, input);

        return anfrageType;
    }
}

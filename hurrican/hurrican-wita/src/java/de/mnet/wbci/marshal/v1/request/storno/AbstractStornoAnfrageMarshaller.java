/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.13
 */
package de.mnet.wbci.marshal.v1.request.storno;

import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoType;
import de.mnet.wbci.marshal.v1.AbstractBaseMarshaller;
import de.mnet.wbci.marshal.v1.entities.CarrierIdentifikatorMarshaller;
import de.mnet.wbci.marshal.v1.entities.EKPGeschaeftsfallMarshaller;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Abstract base storno marshaller for basic storno properties.
 *
 *
 */
public abstract class AbstractStornoAnfrageMarshaller<S extends StornoAnfrage, T extends StornoType>
        extends AbstractBaseMarshaller implements Function<S, T> {

    @Autowired
    private EKPGeschaeftsfallMarshaller ekpMarshaller;
    @Autowired
    private CarrierIdentifikatorMarshaller ciMarshaller;

    protected T apply(S input, T output) {
        output.setEndkundenvertragspartner(ekpMarshaller.apply(input.getWbciGeschaeftsfall()));
        output.setAbsender(ciMarshaller.apply(CarrierCode.MNET)); // when marshalling, M-Net is always the Absender
        output.setEmpfaenger(ciMarshaller.apply(getEmpfaenger(input.getWbciGeschaeftsfall())));

        output.setVorabstimmungsIdRef(input.getWbciGeschaeftsfall().getVorabstimmungsId());
        output.setStornoId(input.getAenderungsId());

        return output;
    }

    /**
     * Calculate which carrier will receive the Storno.
     *
     * @param wbciGeschaeftsfall
     * @return
     */
    private CarrierCode getEmpfaenger(WbciGeschaeftsfall wbciGeschaeftsfall) {
        if (CarrierCode.MNET.equals(wbciGeschaeftsfall.getAbgebenderEKP())) {
            return wbciGeschaeftsfall.getAufnehmenderEKP();
        }
        else {
            return wbciGeschaeftsfall.getAbgebenderEKP();
        }
    }
}

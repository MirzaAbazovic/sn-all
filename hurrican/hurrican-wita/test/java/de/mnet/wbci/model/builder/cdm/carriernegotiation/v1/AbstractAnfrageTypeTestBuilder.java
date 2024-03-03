/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AnfrageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
public abstract class AbstractAnfrageTypeTestBuilder {

    public static <M extends AnfrageType> void enrichTestData(M objectType) {
        EKPType ekpType = new EKPMeldungTypeTestBuilder()
                .withEKPabg(new CarrierIdentifikatorTypeBuilder().withCarrierCode(CarrierCode.MNET.getITUCarrierCode()).build())
                .withEKPauf(new CarrierIdentifikatorTypeBuilder().withCarrierCode(CarrierCode.DTAG.getITUCarrierCode()).build())
                .build();

        if (objectType.getEndkundenvertragspartner() == null) {
            objectType.setEndkundenvertragspartner(ekpType);
        }

        if (objectType.getAbsender() == null) {
            objectType.setAbsender(ekpType.getEKPauf());
        }

        if (objectType.getEmpfaenger() == null) {
            objectType.setEmpfaenger(ekpType.getEKPabg());
        }

        if (objectType.getVersion() <= 0) {
            objectType.setVersion(Integer.valueOf(WbciVersion.getDefault().getVersion()));
        }
    }

}

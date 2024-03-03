/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.model.WbciVersion;

/**
 *
 */
public abstract class AbstractMeldungTypeTestBuilder {

    public static <M extends AbstractMeldungType> void enrichTestData(M objectType,
            GeschaeftsfallEnumType geschaeftsfall) {
        objectType.setGeschaeftsfall(geschaeftsfall);
        EKPType ekpType = new EKPMeldungTypeTestBuilder().buildValid();
        if (objectType.getEndkundenvertragspartner() == null) {
            objectType.setEndkundenvertragspartner(ekpType);
        }
        if (objectType.getAbsender() == null) {
            objectType.setAbsender(ekpType.getEKPabg());
        }
        if (objectType.getEmpfaenger() == null) {
            objectType.setEmpfaenger(ekpType.getEKPauf());
        }
        if (objectType.getVersion() <= 0) {
            objectType.setVersion(Integer.valueOf(WbciVersion.getDefault().getVersion()));
        }
        if (isEmpty(objectType.getVorabstimmungsIdRef())) {
            objectType.setVorabstimmungsIdRef("DEU.MNET.V000000001");
        }

    }

}

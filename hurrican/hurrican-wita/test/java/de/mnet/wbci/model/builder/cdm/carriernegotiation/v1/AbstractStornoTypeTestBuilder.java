/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoType;

/**
 *
 */
public abstract class AbstractStornoTypeTestBuilder {

    public static <M extends StornoType> void enrichTestData(M objectType) {
        AbstractAnfrageTypeTestBuilder.enrichTestData(objectType);

        if (objectType.getVorabstimmungsIdRef() == null) {
            objectType.setVorabstimmungsIdRef("DEU.DTAG.V000000001");
        }

        if (objectType.getStornoId() == null) {
            objectType.setStornoId("DEU.DTAG.S000000001");
        }
    }
}

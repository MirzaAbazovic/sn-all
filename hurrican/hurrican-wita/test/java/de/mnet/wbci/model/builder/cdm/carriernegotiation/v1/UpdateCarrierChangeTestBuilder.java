/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;
import de.mnet.wbci.model.MeldungTyp;

/**
 *
 */
public class UpdateCarrierChangeTestBuilder extends UpdateCarrierChangeBuilder {

    public UpdateCarrierChange buildValid(MeldungTyp meldungtyp, GeschaeftsfallEnumType geschaeftsfallEnumType) {
        // make all null to prevent errors
        withABBM(null);
        withABBMTR(null);
        withAKMTR(null);
        withERLM(null);
        withRUEMVA(null);

        if (meldungtyp.equals(MeldungTyp.RUEM_VA)) {
            withRUEMVA(new MeldungRUEMVATypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        else if (meldungtyp.equals(MeldungTyp.ABBM)) {
            withABBM(new MeldungABBMTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        else if (meldungtyp.equals(MeldungTyp.ABBM_TR)) {
            withABBMTR(new MeldungABBMTRTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        else if (meldungtyp.equals(MeldungTyp.AKM_TR)) {
            withAKMTR(new MeldungAKMTRTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        else if (meldungtyp.equals(MeldungTyp.ERLM)) {
            withERLM(new MeldungERLMTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        else {
            throw new IllegalArgumentException("MeldungTyp " + meldungtyp + "is not supported ");
        }
        return build();
    }

}

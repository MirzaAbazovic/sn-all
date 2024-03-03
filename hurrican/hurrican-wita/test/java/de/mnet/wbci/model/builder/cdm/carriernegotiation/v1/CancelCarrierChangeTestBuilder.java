/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CancelCarrierChange;
import de.mnet.wbci.model.RequestTyp;

/**
 *
 */
public class CancelCarrierChangeTestBuilder extends CancelCarrierChangeBuilder {

    public CancelCarrierChange buildValid(RequestTyp requestTyp) {
        switch (requestTyp) {
            case STR_AUFH_AUF:
                if (objectType.getSTRAUFREC() == null) {
                    withSTRAUFREC(new StornoAufhebungEKPaufTypeTestBuilder()
                            .buildValid());
                }
                break;
            case STR_AUFH_ABG:
                if (objectType.getSTRAUFDON() == null) {
                    withSTRAUFDON(new StornoAufhebungEKPabgTypeTestBuilder()
                            .buildValid());
                }
                break;
            case STR_AEN_AUF:
                if (objectType.getSTRAENREC() == null) {
                    withSTRAENREC(new StornoAenderungEKPaufTypeTestBuilder()
                            .buildValid());
                }
                break;
            case STR_AEN_ABG:
                if (objectType.getSTRAENDON() == null) {
                    withSTRAENDON(new StornoAenderungEKPabgTypeTestBuilder()
                            .buildValid());
                }
                break;
            default:
                throw new IllegalArgumentException("RequestTyp '" + requestTyp
                        + "' not supported in this case");
        }

        return build();
    }
}

/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;

/**
 *
 */
@Component
public class AbbruchmeldungStornoAenMarshaller extends AbstractAbbruchmeldungMarshaller<AbbruchmeldungStornoAen> {

    @Override
    protected GeschaeftsfallEnumType evaluateGeschaeftsfall(MeldungABBMType meldungType) {
        return GeschaeftsfallEnumType.STR_AEN;
    }

}

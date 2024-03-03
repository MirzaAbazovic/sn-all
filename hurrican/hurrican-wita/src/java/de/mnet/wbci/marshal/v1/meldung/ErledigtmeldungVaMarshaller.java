/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.wbci.model.Erledigtmeldung;

/**
 *
 */
@Component
public class ErledigtmeldungVaMarshaller extends AbstractErledigtmeldungMarshaller<Erledigtmeldung> {

    @Override
    protected GeschaeftsfallEnumType evaluateGeschaeftsfall(MeldungERLMType meldungType) {
        return meldungType.getGeschaeftsfall();
    }
}

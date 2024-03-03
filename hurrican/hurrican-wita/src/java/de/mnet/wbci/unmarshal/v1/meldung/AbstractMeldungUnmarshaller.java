/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.unmarshal.v1.enities.CarrierIdentifikatorUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.GeschaeftsfallEnumUnmarshaller;

/**
 *
 */
public abstract class AbstractMeldungUnmarshaller<IN extends AbstractMeldungType, OUT extends Meldung> implements
        Function<IN, OUT> {

    @Autowired
    private CarrierIdentifikatorUnmarshaller carrierIdentifikatorUnmarshaller;
    @Autowired
    private GeschaeftsfallEnumUnmarshaller geschaeftsfallEnumUnmarshaller;

    /**
     * Base apply method for basic unmarshalling of Meldung types.
     *
     * @param meldung
     * @param input
     * @return
     */
    public OUT apply(OUT meldung, IN input) {
        meldung.setAbsender(carrierIdentifikatorUnmarshaller.apply(input.getAbsender()));
        meldung.setVorabstimmungsIdRef(input.getVorabstimmungsIdRef());
        meldung.setWbciVersion(WbciVersion.getWbciVersion(String.valueOf(input.getVersion())));

        return meldung;
    }

}

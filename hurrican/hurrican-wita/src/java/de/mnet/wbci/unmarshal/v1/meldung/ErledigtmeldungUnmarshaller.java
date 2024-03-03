/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.builder.ErledigtmeldungBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionERLMUnmarshaller;

/**
 *
 */
@Component
public class ErledigtmeldungUnmarshaller extends AbstractMeldungUnmarshaller<MeldungERLMType, Erledigtmeldung> {

    @Autowired
    private MeldungsPositionERLMUnmarshaller meldungPositionUnmarshaller;

    @Nullable
    @Override
    public Erledigtmeldung apply(@Nullable MeldungERLMType input) {
        if (input == null) {
            return null;
        }

        ErledigtmeldungBuilder builder = new ErledigtmeldungBuilder();

        builder.withWechseltermin(DateConverterUtils.toLocalDate(input.getWechseltermin()));
        builder.withAenderungsIdRef(input.getAenderungsIdRef());
        builder.withStornoIdRef(input.getStornoIdRef());

        if (!CollectionUtils.isEmpty(input.getPosition())) {
            for (MeldungsPositionERLMType position : input.getPosition()) {
                builder.addMeldungPosition(meldungPositionUnmarshaller.apply(position));
            }
        }

        Erledigtmeldung meldung;

        if (input.getGeschaeftsfall().equals(GeschaeftsfallEnumType.TVS_VA)) {
            meldung = builder.buildForTv();
        }
        else if (input.getGeschaeftsfall().equals(GeschaeftsfallEnumType.STR_AUF)) {
            meldung = builder.buildForStorno(RequestTyp.STR_AUFH_AUF);
        }
        else if (input.getGeschaeftsfall().equals(GeschaeftsfallEnumType.STR_AEN)) {
            meldung = builder.buildForStorno(RequestTyp.STR_AEN_AUF);
        }
        else {
            meldung = builder.build();
        }

        super.apply(meldung, input);

        return meldung;
    }
}

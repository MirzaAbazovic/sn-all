/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionABBMUnmarshaller;

/**
 *
 */
@Component
public class AbbruchmeldungUnmarshaller extends AbstractMeldungUnmarshaller<MeldungABBMType, Abbruchmeldung> {

    @Autowired
    private MeldungsPositionABBMUnmarshaller meldungPositionABBMUnmarshaller;

    @Nullable
    @Override
    public Abbruchmeldung apply(@Nullable MeldungABBMType input) {
        if (input == null) {
            return null;
        }

        AbbruchmeldungBuilder builder = new AbbruchmeldungBuilder();

        if (!CollectionUtils.isEmpty(input.getPosition())) {
            for (MeldungsPositionABBMType positionABBMType : input.getPosition()) {
                builder.addMeldungPosition(meldungPositionABBMUnmarshaller.apply(positionABBMType));
            }
        }

        builder.withBegruendung(input.getBegruendung(), false);
        builder.withWechseltermin(DateConverterUtils.toLocalDate(input.getWechseltermin()));
        builder.withAenderungsIdRef(input.getAenderungsIdRef());
        builder.withStornoIdRef(input.getStornoIdRef());

        Abbruchmeldung abbruchmeldung;

        if (input.getGeschaeftsfall().equals(GeschaeftsfallEnumType.TVS_VA)) {
            abbruchmeldung = builder.buildForTv();
        }
        else if (input.getGeschaeftsfall().equals(GeschaeftsfallEnumType.STR_AUF)) {
            abbruchmeldung = builder.buildForStorno(RequestTyp.STR_AUFH_AUF);
        }
        else if (input.getGeschaeftsfall().equals(GeschaeftsfallEnumType.STR_AEN)) {
            abbruchmeldung = builder.buildForStorno(RequestTyp.STR_AEN_AUF);
        }
        else {
            abbruchmeldung = builder.build();
        }

        super.apply(abbruchmeldung, input);

        return abbruchmeldung;
    }

}

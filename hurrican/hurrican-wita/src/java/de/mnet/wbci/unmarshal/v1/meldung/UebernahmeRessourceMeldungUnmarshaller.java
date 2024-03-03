/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import javax.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UebernahmeLeitungType;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungBuilder;
import de.mnet.wbci.unmarshal.v1.enities.LeitungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionAKMTRUnmarshaller;

/**
 *
 */
@Component
public class UebernahmeRessourceMeldungUnmarshaller extends
        AbstractMeldungUnmarshaller<MeldungAKMTRType, UebernahmeRessourceMeldung> {

    @Autowired
    private MeldungsPositionAKMTRUnmarshaller meldungsPositionAKMTRUnmarshaller;

    @Autowired
    private LeitungUnmarshaller leitungUnmarshaller;

    @Nullable
    @Override
    public UebernahmeRessourceMeldung apply(@Nullable MeldungAKMTRType input) {
        if (input == null) {
            return null;
        }

        final UebernahmeRessourceMeldungBuilder builder = new UebernahmeRessourceMeldungBuilder();
        if (!CollectionUtils.isEmpty(input.getPosition())) {
            for (MeldungsPositionAKMTRType positionAKMTRType : input.getPosition()) {
                builder.addMeldungPosition(meldungsPositionAKMTRUnmarshaller.apply(positionAKMTRType));
            }
        }

        if (!CollectionUtils.isEmpty(input.getUebernahmeLeitung())) {
            for (UebernahmeLeitungType uebernahmeLeitungType : input.getUebernahmeLeitung()) {
                builder.addLeitung(leitungUnmarshaller.apply(uebernahmeLeitungType));
            }
        }

        builder.withPortierungskennungPKIauf(input.getPortierungskennungPKIauf());
        builder.withSichererhafen(input.isSichererHafen());
        builder.withUebernahme(input.isRessourcenuebernahme());

        final UebernahmeRessourceMeldung meldung = builder.build();
        super.apply(meldung, input);
        return meldung;
    }

}

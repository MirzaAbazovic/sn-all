/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.wbci.marshal.v1.entities.MeldungPositionUebernahmeRessourceMeldungMarshaller;
import de.mnet.wbci.marshal.v1.entities.UebernahmeLeitungMarshaller;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;

/**
 *
 */
@Component
public class UebernahmeRessourceMeldungMarshaller extends
        AbstractMeldungMarshaller<UebernahmeRessourceMeldung, MeldungAKMTRType, MeldungPositionUebernahmeRessourceMeldung, MeldungsPositionAKMTRType> {

    @Autowired
    private UebernahmeLeitungMarshaller uebernahmeLeitungMarshaller;
    @Autowired
    private MeldungPositionUebernahmeRessourceMeldungMarshaller meldungPositionUebernahmeRessourceMeldungMarshaller;

    @Nullable
    @Override
    public MeldungAKMTRType apply(@Nullable UebernahmeRessourceMeldung meldung) {
        if (meldung != null) {
            MeldungAKMTRType meldungAKMTRType = V1_OBJECT_FACTORY.createMeldungAKMTRType();
            meldungAKMTRType.setRessourcenuebernahme(meldung.isUebernahme());
            meldungAKMTRType.setSichererHafen(meldung.isSichererHafen());
            meldungAKMTRType.setPortierungskennungPKIauf(meldung.getPortierungskennungPKIauf());

            for (Leitung leitung : meldung.getLeitungen()) {
                meldungAKMTRType.getUebernahmeLeitung().add(uebernahmeLeitungMarshaller.apply(leitung));
            }
            super.apply(meldung, meldungAKMTRType);
            return meldungAKMTRType;
        }
        return null;
    }

    @Override
    protected void applyPositionen(Set<MeldungPositionUebernahmeRessourceMeldung> meldungsPositionen,
            MeldungAKMTRType output) {
        if (meldungsPositionen != null) {
            for (MeldungPositionUebernahmeRessourceMeldung position : meldungsPositionen) {
                output.getPosition().add(meldungPositionUebernahmeRessourceMeldungMarshaller.apply(position));
            }
        }
    }
}

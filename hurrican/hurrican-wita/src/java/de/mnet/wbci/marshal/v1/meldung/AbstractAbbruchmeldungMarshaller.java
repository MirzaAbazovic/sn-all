/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMType;
import de.mnet.wbci.marshal.v1.entities.MeldungPositionAbbruchmeldungMarshaller;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;

public abstract class AbstractAbbruchmeldungMarshaller<T extends Abbruchmeldung> extends
        AbstractMeldungMarshaller<T, MeldungABBMType, MeldungPositionAbbruchmeldung, MeldungsPositionABBMType> {

    @Autowired
    private MeldungPositionAbbruchmeldungMarshaller meldungPositionAbbruchmeldungMarshaller;

    @Nullable
    @Override
    public MeldungABBMType apply(@Nullable T meldung) {
        if (meldung != null) {
            MeldungABBMType meldungABBMType = V1_OBJECT_FACTORY.createMeldungABBMType();
            if (meldung.getWechseltermin() != null) {
                meldungABBMType.setWechseltermin(DateConverterUtils.toXmlGregorianCalendar(meldung.getWechseltermin()));
            }

            meldungABBMType.setStornoIdRef(meldung.getStornoIdRef());
            meldungABBMType.setAenderungsIdRef(meldung.getAenderungsIdRef());
            meldungABBMType.setBegruendung(meldung.getBegruendung());

            super.apply(meldung, meldungABBMType);

            meldungABBMType.setGeschaeftsfall(evaluateGeschaeftsfall(meldungABBMType));

            return meldungABBMType;
        }
        return null;
    }

    /**
     * Evaluates geschaeftsfall according to meldung circumstances. For instance when meldung is linked to a {@link
     * de.mnet.wbci.model.StornoAnfrage} geschaeftsfall has to be set to STR_AUF. In case of {@link
     * de.mnet.wbci.model.TerminverschiebungsAnfrage} it has to be set to TVS_VA.
     *
     * @return
     */
    protected abstract GeschaeftsfallEnumType evaluateGeschaeftsfall(MeldungABBMType meldungType);

    @Override
    protected void applyPositionen(Set<MeldungPositionAbbruchmeldung> meldungsPositionen, MeldungABBMType output) {
        for (MeldungPositionAbbruchmeldung position : meldungsPositionen) {
            output.getPosition().add(meldungPositionAbbruchmeldungMarshaller.apply(position));
        }
    }

}

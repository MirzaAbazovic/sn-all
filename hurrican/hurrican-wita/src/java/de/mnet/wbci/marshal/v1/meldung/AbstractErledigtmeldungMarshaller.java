/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import java.util.*;
import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.marshal.v1.entities.MeldungPositionErledigtmeldungMarshaller;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.MeldungPositionErledigtmeldung;

/**
 *
 */
public abstract class AbstractErledigtmeldungMarshaller<T extends Erledigtmeldung> extends
        AbstractMeldungMarshaller<T, MeldungERLMType, MeldungPositionErledigtmeldung, MeldungsPositionERLMType> {

    @Autowired
    private MeldungPositionErledigtmeldungMarshaller meldungPositionMarshaller;

    @Nullable
    @Override
    public MeldungERLMType apply(@Nullable T meldung) {
        if (meldung != null) {
            MeldungERLMType meldungERLMType = V1_OBJECT_FACTORY.createMeldungERLMType();
            meldungERLMType.setAenderungsIdRef(meldung.getAenderungsIdRef());
            meldungERLMType.setStornoIdRef(meldung.getStornoIdRef());
            if (meldung.getWechseltermin() != null) {
                meldungERLMType.setWechseltermin(DateConverterUtils.toXmlGregorianCalendar(meldung.getWechseltermin()));
            }

            super.apply(meldung, meldungERLMType);

            meldungERLMType.setGeschaeftsfall(evaluateGeschaeftsfall(meldungERLMType));

            return meldungERLMType;
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
    protected abstract GeschaeftsfallEnumType evaluateGeschaeftsfall(MeldungERLMType meldungType);

    @Override
    protected void applyPositionen(Set<MeldungPositionErledigtmeldung> meldungsPositionen, MeldungERLMType output) {
        if (meldungsPositionen != null) {
            for (MeldungPositionErledigtmeldung position : meldungsPositionen) {
                output.getPosition().add(meldungPositionMarshaller.apply(position));
            }
        }
    }

}

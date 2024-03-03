/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request.vorabstimmung;

import javax.annotation.*;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.VorabstimmungType;
import de.mnet.wbci.marshal.v1.entities.PersonOderFirmaMarshaller;
import de.mnet.wbci.marshal.v1.entities.ProjektMarshaller;
import de.mnet.wbci.marshal.v1.entities.WeitereAnschlussInhaberMarshaller;
import de.mnet.wbci.marshal.v1.request.AnfrageMarshaller;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 *
 */
public abstract class VorabstimmungMarshaller<GF extends WbciGeschaeftsfall, VT extends VorabstimmungType> extends
        AnfrageMarshaller<GF, VT> implements Function<GF, VT> {

    @Autowired
    private ProjektMarshaller projektMarshaller;
    @Autowired
    private PersonOderFirmaMarshaller personOderFirmaMarshaller;
    @Autowired
    private WeitereAnschlussInhaberMarshaller weitereAnschlussInhaberMarshaller;

    @Nullable
    @Override
    public VT apply(VT anfrage, GF input) {
        if (input.getWeitereAnschlussinhaber() != null) {
            anfrage.getWeitereAnschlussinhaber().addAll(
                    weitereAnschlussInhaberMarshaller.apply(input.getWeitereAnschlussinhaber()));
        }

        anfrage.setEndkunde(personOderFirmaMarshaller.apply(input.getEndkunde()));
        anfrage.setVorabstimmungsId(input.getVorabstimmungsId());
        anfrage.setProjektId(projektMarshaller.apply(input.getProjekt()));
        anfrage.setKundenwunschtermin(DateConverterUtils.toXmlGregorianCalendar(input.getKundenwunschtermin()));

        super.apply(anfrage, input);

        return anfrage;
    }

}

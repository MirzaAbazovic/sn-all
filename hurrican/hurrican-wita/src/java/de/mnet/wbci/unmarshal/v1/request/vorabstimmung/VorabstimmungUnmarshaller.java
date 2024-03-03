/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.vorabstimmung;

import javax.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.VorabstimmungType;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.unmarshal.v1.enities.PersonOderFirmaUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.ProjektUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.AnfrageUnmarshaller;

@Component
public abstract class VorabstimmungUnmarshaller<VT extends VorabstimmungType, GF extends WbciGeschaeftsfall>
        extends AnfrageUnmarshaller<VT, GF> {

    @Autowired
    private PersonOderFirmaUnmarshaller personOderFirmaUnmarshaller;
    @Autowired
    private ProjektUnmarshaller projektUnmarshaller;

    @Nullable
    @Override
    public GF apply(GF geschaeftsfall, VT input) {
        super.apply(geschaeftsfall, input);
        geschaeftsfall.setVorabstimmungsId(input.getVorabstimmungsId());
        geschaeftsfall.setKundenwunschtermin(DateConverterUtils.toLocalDate(input.getKundenwunschtermin()));
        geschaeftsfall.setEndkunde(personOderFirmaUnmarshaller.apply(input.getEndkunde()));
        if (input.getWeitereAnschlussinhaber() != null) {
            for (PersonOderFirmaType personOderFirmaType : input.getWeitereAnschlussinhaber()) {
                geschaeftsfall.addWeitererAnschlussinhaber(personOderFirmaUnmarshaller.apply(personOderFirmaType));
            }
        }
        geschaeftsfall.setProjekt(projektUnmarshaller.apply(input.getProjektId()));
        return geschaeftsfall;
    }

}

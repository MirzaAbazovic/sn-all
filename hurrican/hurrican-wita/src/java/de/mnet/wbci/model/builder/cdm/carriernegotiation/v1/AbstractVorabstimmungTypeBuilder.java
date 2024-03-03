/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import java.util.*;
import javax.xml.datatype.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ProjektIDType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.VorabstimmungType;

public abstract class AbstractVorabstimmungTypeBuilder<T> extends AbstractAnfrageTypeBuilder<T> {


    public AbstractVorabstimmungTypeBuilder withVorabstimmungsId(String vorabstimmungsId) {
        ((VorabstimmungType) objectType).setVorabstimmungsId(vorabstimmungsId);
        return this;
    }

    public AbstractVorabstimmungTypeBuilder withKundenwunschtermin(XMLGregorianCalendar kundenwunschtermin) {
        ((VorabstimmungType) objectType).setKundenwunschtermin(kundenwunschtermin);
        return this;
    }

    public AbstractVorabstimmungTypeBuilder withEndkunde(PersonOderFirmaType personOderFirma) {
        ((VorabstimmungType) objectType).setEndkunde(personOderFirma);
        return this;
    }

    public AbstractVorabstimmungTypeBuilder withWeitereAnschlussinhaber(List<PersonOderFirmaType> weitereAnschlussinhaber) {
        ((VorabstimmungType) objectType).getWeitereAnschlussinhaber().addAll(weitereAnschlussinhaber);
        return this;
    }

    public AbstractVorabstimmungTypeBuilder withProjektId(ProjektIDType projektIDType) {
        ((VorabstimmungType) objectType).setProjektId(projektIDType);
        return this;
    }

}
